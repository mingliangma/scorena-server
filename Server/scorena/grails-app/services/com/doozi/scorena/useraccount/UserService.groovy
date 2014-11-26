package com.doozi.scorena.useraccount

import java.util.List;
import java.util.Map;

import grails.transaction.Transactional
import grails.converters.JSON
import grails.web.JSONBuilder
import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

import com.doozi.scorena.utils.*;
import com.doozi.scorena.*;
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction

import grails.plugins.rest.client.RestBuilder

@Transactional
class UserService {
	public static final int INITIAL_BALANCE = 2000
	public static final int SOCIALNETWORK_INITIAL_BALANCE = 2500
	
	public static final int PREVIOUS_BALANCE = INITIAL_BALANCE
	public static final int FREE_COIN_BALANCE_THRESHOLD = 100
	public static final int FREE_COIN_AMOUNT = 1000
	public static final int RANKING_SIZE = 60
	String RANK_TYPE_WEEKLY = "weekly"
	String RANK_TYPE_ALL = "all"
	
	
	def grailsApplication
	def parseService
	def betTransactionService
	def sportsDataService
	def payoutTansactionService
	def userStatsService
	def scoreService 
	def friendSystemService
	
	def getCoins(userId){
		int asset = 0
		int inWagerAmount = 0
		
		def userAccount = Account.findByUserId(userId)
		if (!userAccount)
			return [code: 400, error:"userId is invalid"]
		
		def lastPayoutDate = PayoutTransaction.executeQuery("SELECT max(p.createdAt) from PayoutTransaction p where p.account.id=? ", [userAccount.id])		
		def totalBetAmount = BetTransaction.executeQuery("SELECT sum(p.transactionAmount) from BetTransaction p where p.account.id=? and p.createdAt>?", [userAccount.id, lastPayoutDate[0]])
		if (totalBetAmount[0] != null)
			inWagerAmount = totalBetAmount[0]
			
		asset = inWagerAmount+userAccount.currentBalance
		
		if (asset >=FREE_COIN_BALANCE_THRESHOLD)
			return [code: 400, error:"Balance above "+FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"]
		
		userAccount.currentBalance = userAccount.currentBalance + FREE_COIN_AMOUNT
		return [username: userAccount.username, userId: userAccount.userId, currentBalance: userAccount.currentBalance, newCoinsAmount: FREE_COIN_AMOUNT]
	}
	
	def createSocialNetworkUser(String sessionToken, List facebookIds){
		int currentBalance = SOCIALNETWORK_INITIAL_BALANCE
		RestBuilder rest = new RestBuilder()
		
		Map userProfile = getUserProfileBySessionToken(rest, sessionToken)
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userProfile.objectId)
		
		if (account == null){
			Map accountCreationResult = createUserAccount(rest, userProfile.objectId, userProfile.username, SOCIALNETWORK_INITIAL_BALANCE, SOCIALNETWORK_INITIAL_BALANCE, userProfile.sessionToken)
			if (accountCreationResult!=[:]){
				return accountCreationResult
			}
		}else{
			currentBalance = account.currentBalance
		}
		
		//TODO: automatically add facebook friends to scorena friend system
		Map facebookFriendsUserProfiles = parseService.retrieveUserListByFBIds(facebookIds)		
		println "facebookFriendsUserProfiles="+facebookFriendsUserProfiles

		def result = userProfileMapRender(sessionToken, currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name, 
		userProfile.objectId, userProfile.gender, userProfile.region, userProfile.email, userProfile.pictureURL)
		
		return result
	}
			
	def createUser(String username, String email, String password, String gender, String region){
		return createUser(username, email, password, gender, region, AccountType.USER, "", "")
	}
	
	def createTestUser(String username, String email, String password, String gender, String region, String pictureURL, String facebookId){
		return createUser(username, email, password, gender, region, AccountType.TEST, pictureURL, facebookId)
	}
			
	def createUser(String username, String email, String password, String gender, String region, int accountType, String pictureURL, String facebookId){
		println "UserService::createUser(): username="+username+"  email="+email + " facebookId="+facebookId
		
		int currentBalance = INITIAL_BALANCE
		String usernameLowerCase = username.toLowerCase()
		String displayName = usernameLowerCase
		RestBuilder rest = new RestBuilder()
		
		Map userProfile =  createUserProfile(rest, usernameLowerCase, email, password, gender, region, displayName, pictureURL, facebookId)
		if (userProfile.code){
			return userProfile
		}
		
		println "user profile created successfully"		
		
		Map accountCreationResult = createUserAccount(rest, userProfile.objectId, username, INITIAL_BALANCE, PREVIOUS_BALANCE, userProfile.sessionToken, accountType)
		if (accountCreationResult!=[:]){
			return accountCreationResult
		}
		
		println "user profile and account created successfully"

		
		def result = userProfileMapRender(userProfile.sessionToken, currentBalance, userProfile.createdAt, userProfile.username, displayName, 
		userProfile.objectId, gender, region, email, userProfile.pictureURL)
		
		return result		
	}
	
	def login(String username, String password){
		def rest = new RestBuilder()
		
		Map userProfile = userLogin(rest, username, password)
		
		
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userProfile.objectId)
		
		if (!account){
			return [code:500, error:"user account does not exist"]			
		}
		
		def result = userProfileMapRender(userProfile.sessionToken, account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name, 
				userProfile.objectId, userProfile.gender, userProfile.region, userProfile.email, userProfile.pictureURL)
		
		return result
	}
	
	
	def passwordReset(def email){
		def rest = new RestBuilder()
		def resp = parseService.passwordReset(rest, email)
		if (resp.status != 200 ||resp.json.code)
			return resp.json
			
		return resp.json
	}
	
	def validateSession (def sessionToken){
		def rest = new RestBuilder()
		def resp = parseService.validateSession(rest, sessionToken)
		if (resp.status == 200){
			return resp.json
		}else if (resp.json.code){
			return resp.json
		}else{
			def result = [
				code:resp.status,
				error:resp.json.error
			]
			return result
		}
	}
	
	
	def deleteUser(String sessionToken, String userId){
		def rest = new RestBuilder()
		def resp = parseService.deleteUser(rest, sessionToken, userId)
		if (resp.status != (200)){
			println "delete user profile failed: "+resp.json
			return resp.json
		}
		def account = Account.findByUserId(userId)
		
		if (account != null){
			account.delete()
		}
		return [:]
	}
	
	def getUserProfile(String userId,String month){
		def rest = new RestBuilder()
		
		def userProfile = userRetreive(rest, userId)
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userId)
		if (account == null){
			println "ERROR: user account does not exist"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}	
		
		def userScores = scoreService.listScoresByUserId(userId)
		def userPayoutTrans = payoutTansactionService.listPayoutTransByUserId(userId)
		Map userStats = userStatsService.getUserStats(userScores, userPayoutTrans, month,account)

		def result = userProfileMapRender("", account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name,
			userProfile.objectId, "", "", userProfile.email, userProfile.pictureURL)
		
		result.userStats = userStats
		result.level = 1
		result.levelName = "novice"
		
		return result		
		
	}
	
	def getUserBalance(String userId){
		def rest = new RestBuilder()
		def resp = parseService.retrieveUser(rest, userId)
		
		def account = Account.findByUserId(userId)
		if (account == null){
			println "ERROR: user account does not exist"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}
		int currentBalance = account.currentBalance
		int inWager = getUserInWagerCoins(userId)
		
		return[currentBalance:account.currentBalance, inWager:inWager]
		
	}
	
	private int getUserInWagerCoins(userId){
		int inWager = 0
		def unpaidTransactions = betTransactionService.listUnpaidBetsByUserId(userId)
		for (BetTransaction unpaidTransaction: unpaidTransactions){
			inWager += unpaidTransaction.transactionAmount
		}
		return inWager
	}
	
	def updateUserProfile(String sessionToken, String userId, JSON updateUserData){
		def rest = new RestBuilder()
		def resp = parseService.updateUser(rest, sessionToken, userId, updateUserData)
		return resp.json
	}
	
	
	
	Boolean accountExists(String userId){
		def account = Account.findByUserId(userId)
		if (account){
			return true
		}else{
			return false
		}
	}
	
	String getUserDisplayName(String userId){
		def account = Account.findByUserId(userId)
		return account.username
	
	}

	private List getFacebookFrdsUserId(List facebookIds){
		Map userProfiles = parseService.retrieveUserListByFBIds(facebookIds)
	}


	private Map userLogin(RestBuilder rest, String username, String password){
		println username
		println password
		def resp = parseService.loginUser(rest, username, password)
		
		if (resp.status != 200){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			return result
		}
		return resp.json
	}

	private Map userRetreive(RestBuilder rest, String userId){
		def resp = parseService.retrieveUser(rest, userId)
		
		if (resp.status != 200){
			println "ERROR: UserService::getUserProfile(): user account does not exist in parse"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}
		return resp.json
	}

	private Map userProfileMapRender(String sessionToken, int currentBalance, String createdAt, String username, String displayName,
		String userId, String gender, String region, String email, String pictureURL){
		
		int currentBalanceResp = currentBalance
		String createdAtResp = ""
		String usernameResp = ""
		String userIdResp = ""
		String genderResp = ""
		String regionResp = ""
		String emailResp = ""
		String pictureURLResp = ""
		String sessionTokenResp = ""
		
		if (sessionToken != null)
			sessionTokenResp = sessionToken
		
		if (createdAt != null)
			createdAtResp = createdAt
	
		if (userId != null)
			userIdResp = userId
		
		if (displayName != null && displayName!="")
			usernameResp = displayName
		else if(username != null)
			usernameResp = username
			
			
		if (gender != null)
			genderResp = gender
		
		if (region != null)
			regionResp = region
			
		if (email != null)
			emailResp = email
			
		if (pictureURL != null)
			pictureURLResp = pictureURL
		
		def result = [
			createdAt:createdAtResp,
			username:usernameResp,
			currentBalance:currentBalanceResp,
			sessionToken:sessionTokenResp,
			userId: userIdResp,
			gender: genderResp,
			region: regionResp,
			email: emailResp,
			pictureUrl: pictureURLResp
		]
		return result
	}

	/**
	 * @param rest
	 * @param userId
	 * @param username
	 * @param initialBalance
	 * @param previousBalance
	 * @param sessionToken
	 * @param accountType
	 * @return returns empty map upon successful
	 */
	private Map createUserAccount(RestBuilder rest, String userId, String username, int initialBalance, int previousBalance, String sessionToken, int accountType){
		def userAccount = Account.findByUserId(userId)
		if (userAccount){
			def result = [
				code:202,
				error:"account already exists"
			]
			return result
		}
		def account = new Account(userId:userId, username:username, currentBalance:initialBalance,previousBalance:previousBalance, accountType: accountType)
		if (!account.save()){
			account.errors.each{
				println it
			}
			def delResp = parseService.deleteUser(rest, sessionToken, userId)
			def result = [
				code:202,
				error:"account creation failed"
			]
			return result
		}
		return [:]
	}

	private Map createUserProfile(RestBuilder rest, String username, String email, String password, String gender, String region, String displayName, String pictureURL, String facebookId){
			
		def resp = parseService.createUser(rest, username, email, password, gender, region, displayName, pictureURL, facebookId)
		
		if (resp.status != 201){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			return result
		}
		return resp.json
	}

	private Map getUserProfileBySessionToken(RestBuilder rest, String sessionToken){
		def resp = parseService.validateSession(rest, sessionToken)
		if (resp.status != 200){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			return result
		}
		return resp.json
	}
	
//	private Map getUserProfileBySessionToken_tempFix(RestBuilder rest, String sessionToken){
//		def resp = parseService.validateSessionT3(rest, sessionToken)
//		if (resp.status != 200){
//			def result = [
//				code:resp.json.code,
//				error:resp.json.error
//			]
//			return result
//		}
//		Map userProfileT3 = resp.json
//		println userProfileT3
//		Map userProfile = parseService.retrieveUserByDisplayName(userProfileT3.display_name)
//		println userProfile.results[0]
//		return userProfile.results[0]
//	}
}
