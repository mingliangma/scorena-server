package com.doozi.scorena.useraccount

import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional
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
import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.OpenAccountTransaction
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
		log.info "getCoins(): begins with userId = ${userId}"
		
		int asset = 0
		int inWagerAmount = 0
		
		def userAccount = Account.findByUserId(userId)
		if (!userAccount){
			log.error "getCoins(): userId is invalid"
			return [code: 400, error:"userId is invalid"]
		}
		
		def lastPayoutDate = PayoutTransaction.executeQuery("SELECT max(p.createdAt) from PayoutTransaction p where p.account.id=? ", [userAccount.id])		
		def totalBetAmount = BetTransaction.executeQuery("SELECT sum(p.transactionAmount) from BetTransaction p where p.account.id=? and p.createdAt>?", [userAccount.id, lastPayoutDate[0]])
		if (totalBetAmount[0] != null)
			inWagerAmount = totalBetAmount[0]
			
		asset = inWagerAmount+userAccount.currentBalance
		
		if (asset >=FREE_COIN_BALANCE_THRESHOLD){
			log.error "getCoins(): Balance above "+FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"
			return [code: 400, error:"Balance above "+FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"]
		}
		
		userAccount.currentBalance = userAccount.currentBalance + FREE_COIN_AMOUNT
		
		def result = [username: userAccount.username, userId: userAccount.userId, currentBalance: userAccount.currentBalance, newCoinsAmount: FREE_COIN_AMOUNT]
		log.info "getCoins(): ends with result = ${result}"
		
		return [username: userAccount.username, userId: userAccount.userId, currentBalance: userAccount.currentBalance, newCoinsAmount: FREE_COIN_AMOUNT]
	}
	
	def createSocialNetworkUser(String sessionToken){
		log.info "createSocialNetworkUser(): begins with sessionToken = ${sessionToken}"
		
		int currentBalance = SOCIALNETWORK_INITIAL_BALANCE
		RestBuilder rest = new RestBuilder()
		
		Map userProfile = getUserProfileBySessionToken(rest, sessionToken)
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userProfile.objectId)
		
		if (account == null){
			Map accountCreationResult = createUserAccount(rest, userProfile.objectId, userProfile.username, SOCIALNETWORK_INITIAL_BALANCE, SOCIALNETWORK_INITIAL_BALANCE, userProfile.sessionToken, AccountType.FACEBOOK_USER)
			if (accountCreationResult!=[:]){
				return accountCreationResult
			}
			
			//automatically add facebook friends to scorena friend system
			if (userProfile.fbFriends){
				def tips = []
				List facebookFriendUserIdList = getFacebookFrdsUserId((List)userProfile.fbFriends)
				def currentUserId = userProfile.objectId
				for(String facebookFriendUserId: facebookFriendUserIdList) {
					tips = friendSystemService.addFacebookFriend(currentUserId, facebookFriendUserId)
				}
				println "tips:" + tips
			}
		}else{
			currentBalance = account.currentBalance
		}
		

		
		def result = userProfileMapRender(sessionToken, currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name, 
		userProfile.objectId, "", "", userProfile.email, userProfile.pictureURL)
		
		log.info "createSocialNetworkUser(): ends"
		
		return result
	}
			
	def createUser(String username, String email, String password, String pictureURL, String gender, String region){
		return createUser(username.toLowerCase(), email, password, gender, region, AccountType.USER, pictureURL, "")
	}
	
	def createTestUser(String username, String email, String password, String gender, String region, String pictureURL, String facebookId){
		return createUser(username.toLowerCase(), email, password, gender, region, AccountType.TEST, pictureURL, facebookId)
	}
			
	def createUser(String username, String email, String password, String gender, String region, int accountType, String pictureURL, String facebookId){
		log.info "createUser(): begins..."
		println "UserService::createUser(): username="+username+"  email="+email + " facebookId="+facebookId
		int currentBalance = INITIAL_BALANCE
		String usernameLowerCase = username.toLowerCase().trim()
		String displayName = usernameLowerCase
		RestBuilder rest = new RestBuilder()
		
		Map userProfile =  createUserProfile(rest, usernameLowerCase, email, password, gender, region, displayName, pictureURL, facebookId)
		if (userProfile.code){
			return userProfile
		}
		
		println "user profile created successfully"		
		log.info "createUser(): user profile created successfully"
		
		Map accountCreationResult = createUserAccount(rest, userProfile.objectId, username, INITIAL_BALANCE, PREVIOUS_BALANCE, userProfile.sessionToken, accountType)
		if (accountCreationResult!=[:]){
			return accountCreationResult
		}
		
		println "user profile and account created successfully"
		log.info "createUser(): user profile and account created successfully"
		def result
		
		if (!facebookId.empty)
		{
		 result = userProfileMapRender(userProfile.sessionToken, currentBalance, userProfile.createdAt, userProfile.username, displayName, 
		userProfile.objectId, "", "", email, userProfile.pictureURL)
		}
		else
		{
			 result = userProfileMapRender(userProfile.sessionToken, currentBalance, userProfile.createdAt, userProfile.username, displayName,
				userProfile.objectId, "", "", email, pictureURL)
		}
		log.info "createUser(): ends with result = ${result}"
		
		return result		
	}
	
	def login(String username, String password){
		log.info "login(): begins with username = ${username}, password = ${password}"
		
		def rest = new RestBuilder()
		
		Map userProfile = userLogin(rest, username, password)
		println "userProfile: "+userProfile
		
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userProfile.objectId)
		
		if (!account){
			log.error "login(): user account does not exist"
			return [code:500, error:"user account does not exist"]			
		}
		
		def result = userProfileMapRender(userProfile.sessionToken, account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name, 
				userProfile.objectId, "", "", userProfile.email, userProfile.pictureURL)
		
		log.info "login(): ends with result = ${result}"
		
		return result
	}
	
	
	def passwordReset(def email){
		log.info "passwordReset(): begins with email = ${email}"
		
		def rest = new RestBuilder()
		def resp = parseService.passwordReset(rest, email)
		if (resp.status != 200 ||resp.json.code){
			log.error "passwordReset(): ${resp.json}"
			return resp.json
		}
			
		log.info "passwordReset(): ends with ${resp.json}"
		return resp.json
	}
	
	def validateSession (def sessionToken){
		log.info "validateSession(): begins with sessionToken = ${sessionToken}"
		
		def rest = new RestBuilder()
		def resp = parseService.validateSession(rest, sessionToken)
		if (resp.status == 200){
			log.info "validateSession(): ${resp.json}"
			return resp.json
		}else if (resp.json.code){
			log.info "validateSession(): ${resp.json}"
			return resp.json
		}else{
			def result = [
				code:resp.status,
				error:resp.json.error
			]
			log.error "validateSession(): ${resp.json.error}"
			return result
		}
	}
	
	
	def deleteUser(String sessionToken, String userId){
		log.info "deleteUser(): begins wiht sessionToken = ${sessionToken}, userId = ${userId}"
		
		def rest = new RestBuilder()
		def resp = parseService.deleteUser(rest, sessionToken, userId)
		if (resp.status != (200)){
			println "delete user profile failed: "+resp.json
			log.error "deleteUser(): delete user profile failed: "+resp.json
			return resp.json
		}
		def account = Account.findByUserId(userId)
		
		if (account != null){
			account.delete()
		}
		
		log.info "deleteUser(): ends"
		
		return [:]
	}
	
	def getUserProfile(String userId,String month){
		log.info "getUserProfile(): begins with userId = ${userId}, month = ${month}"
		
		def rest = new RestBuilder()
		
		def userProfile = userRetreive(rest, userId)
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userId)
		if (account == null){
			println "ERROR: user account does not exist"
			log.error "getUserProfile(): user account does not exist"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}	
		
		
		List<AbstractScore> userScores = scoreService.listScoresByUserId(userId)
		def userPayoutTrans = payoutTansactionService.listPayoutTransByUserId(userId)
		Map userStats = userStatsService.getUserStats(userScores, userPayoutTrans, month,account)

		def result = userProfileMapRender("", account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name,
			userProfile.objectId, "", "", userProfile.email, userProfile.pictureURL)
		
		result.userStats = userStats
		result.level = 1
		result.levelName = "novice"
		result.followerCounter = friendSystemService.getFollowerCounter(userId)
		result.followingCounter = friendSystemService.getFollowingCounter(userId)
		
		log.info "getUserProfile(): ends with result = ${result}"
		
		return result		
		
	}
	
	def getUserBalance(String userId){
		log.info "getUserBalance(): begins with userId = ${userId}"
		
		def rest = new RestBuilder()
		def resp = parseService.retrieveUser(rest, userId)
		
		def account = Account.findByUserId(userId)
		if (account == null){
			println "ERROR: user account does not exist"
			log.error "getUserBalance(): user account does not exist"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}
		int currentBalance = account.currentBalance
		int inWager = getUserInWagerCoins(userId)
		
		def result = [currentBalance:account.currentBalance, inWager:inWager]
		log.info "getUserBalance(): ends with result = ${result}"
		
		return [currentBalance:account.currentBalance, inWager:inWager]
		
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


	
	@Transactional
	void updateUserBalance(String userId, int balanceDelta){
		log.info "updateUserBalance(): begins..."
		
		int retryCount = 0
		while (retryCount<5){
			try{
				Account playerAccount = Account.findByUserId(userId, [lock: true])
				playerAccount.previousBalance = playerAccount.currentBalance
				playerAccount.currentBalance += balanceDelta
				playerAccount.save(flush: true)
				break;
			}catch(org.springframework.dao.CannotAcquireLockException e){
				println "updateUserBalance ERROR: "+e.message
				Thread.sleep(500)
				retryCount++
			}
		}
		
		log.info "updateUserBalance(): ends"
	}

	
	private List getFacebookFrdsUserId(List facebookIds){
		log.info "getFacebookFrdsUserId(): begins with facebookIds = ${facebookIds}"
		
		Map userProfilesMap = parseService.retrieveUserListByFBIds(facebookIds)
		
		println "userProfiles:" + userProfilesMap
		
		List userProfileList = userProfilesMap.results	//TODO format of Map userProfiles
		List userIdList = []
		
		for(Map userProfile: userProfileList) {
			userIdList.add(userProfile.objectId)
		}
		
		log.info "getFacebookFrdsUserId(): ends wiht userIdList = ${userIdList}"
		
		return userIdList
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
			log.error "userLogin(): ${result}"
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
			log.error "userRetreive(): ${result}"
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
		log.info "createUserAccount(): begins..."
		
		def userAccount = Account.findByUserId(userId)
		if (userAccount){
			def result = [
				code:202,
				error:"account already exists"
			]
			log.error "createUserAccount(): ${result}"
			return result
		}
		def account = new Account(userId:userId, username:username, currentBalance:initialBalance,previousBalance:previousBalance, accountType: accountType)
		def openAccountTransaction = new OpenAccountTransaction(transactionAmount: initialBalance, createdAt: new Date(), accountType: accountType)
		
		account.addToTrans(openAccountTransaction)
		
		if (!openAccountTransaction.validate()) {
			String errorMessage = ""
			openAccountTransaction.errors.each {
				println it
				errorMessage += it
			}
			OpenAccountTransaction.withSession { session ->
				session.clear()
			}
			log.error "createUserAccount(): ${errorMessage}"
			return [code:202, error: "UserService:: createUserAccount(): "+errorMessage]
		}
		
		if (!account.validate()) {
			String errorMessage = ""
			account.errors.each {
				println it
				errorMessage += it
			}
			BetTransaction.withSession { session ->
				session.clear()
			}
			log.error "createUserAccount(): ${errorMessage}"
			return [code:202, error: "UserService:: createUserAccount(): "+errorMessage]
		}
		
		
		if (!account.save() || !openAccountTransaction.save()){
			account.errors.each{
				println it
			}
			openAccountTransaction.errors.each{
				println it
			}
			def delResp = parseService.deleteUser(rest, sessionToken, userId)
			def result = [
				code:202,
				error:"account creation failed"
			]
			log.error "createUserAccount(): ${result}"
			return result
		}
		
		log.info "createUserAccount(): ends"
		return [:]
	}

	private Map createUserProfile(RestBuilder rest, String username, String email, String password, String gender, String region, String displayName, String pictureURL, String facebookId){
		log.info "createUserProfile(): begins..."
			
		def resp = parseService.createUser(rest, username, email, password, gender, region, displayName, pictureURL, facebookId)
		
		if (resp.status != 201){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			log.error "createUserProfile(): ${result}"
			return result
		}
		
		log.info "createUserProfile(): ends with ${resp.json}"
		return resp.json
	}

	private Map getUserProfileBySessionToken(RestBuilder rest, String sessionToken){
		def resp = parseService.validateSession(rest, sessionToken)
		if (resp.status != 200){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			log.error "getUserProfileBySessionToken(): ${result}"
			return result
		}
		return resp.json
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
		def parseResp = userRetreive(rest,userId)
		
		if (parseResp.code){
			return parseResp
		}
		
		def account = Account.findByUserId(parseResp.objectId)
		
		if (!account){
			log.error "login(): user account does not exist"
			return [code:500, error:"user account does not exist"]
		}
		
		def result = userProfileMapRender(sessionToken, account.currentBalance, parseResp.createdAt, parseResp.username, parseResp.display_name,
				parseResp.objectId, "", "", parseResp.email, parseResp.pictureURL)
		
		return result
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
