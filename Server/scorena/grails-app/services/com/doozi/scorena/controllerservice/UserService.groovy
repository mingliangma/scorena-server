package com.doozi.scorena.controllerservice

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

import com.doozi.scorena.Account;
import com.doozi.scorena.*;
import com.doozi.scorena.PoolTransaction

import grails.plugins.rest.client.RestBuilder

@Transactional
class UserService {
	def INITIAL_BALANCE = 2000
	def PREVIOUS_BALANCE = INITIAL_BALANCE
	def FREE_COIN_BALANCE_THRESHOLD = 50
	def FREE_COIN_AMOUNT = 1000
	
	def grailsApplication
	def parseService
	def betService
	def sportsDataService
	
	def getCoins(userId){
		def userAccount = Account.findByUserId(userId)
		if (!userAccount)
			return [code: 400, error:"userId is invalid"]
		
		if (userAccount.currentBalance >=FREE_COIN_BALANCE_THRESHOLD)
			return [code: 400, error:"Balance above "+FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"]
			
		userAccount.currentBalance = userAccount.currentBalance + FREE_COIN_AMOUNT
		return [username: userAccount.username, userId: userAccount.userId, currentBalance: userAccount.currentBalance, newCoinsAmount: FREE_COIN_AMOUNT]
	}
	
	def constructRankingData(def username, def netgain, def rank){	
		String netGain = ""
		if (netgain>0)
			netGain="+"+netgain.toString()
		else
			netGain=netgain.toString()
			
		return [username: username, gain: netGain, rank: rank]
	}
	
//	def processRankingData(Account user, int rank){		
//		return [username: user.username, gain: user.currentBalance, rank: rank]
//	}
	
	def getRanking(userId){
		def userRankingAll = UserRankingAll.findAll()
		def userRankingWk = UserRankingWk.findAll()
		List rankingResultAll =[]
		List rankingResultWk =[]
		
		int rankingAllSize = 50
		if (userRankingAll.size() < 50)
			rankingAllSize=userRankingAll.size()
		
		
		for (int i=0; i<rankingAllSize; i++){
			UserRankingAll rankEntry = userRankingAll.getAt(i)
			def userAccount = Account.get(rankEntry.id)
			rankingResultAll.add(constructRankingData(userAccount.username, rankEntry.netGain,i+1))
		}
		
		int rankingWkSize = 50
		if (userRankingAll.size() < 50)
			rankingWkSize=userRankingAll.size()
		
		for (int i=0; i<rankingWkSize; i++){
			UserRankingWk rankEntry = userRankingWk.getAt(i)
			def userAccount = Account.get(rankEntry.id)
			rankingResultWk.add(constructRankingData(userAccount.username, rankEntry.netGain,i+1))
		}
		
		return [weekly: rankingResultWk, all: rankingResultAll]
	}
	
	private Map createUserAccount(RestBuilder rest, String userId, String username, int initialBalance, int previousBalance, String sessionToken){
		def userAccount = Account.findByUserId(userId)
		if (userAccount){
			def result = [
				code:202,
				error:"account already exists"
			]
			return result
		}
		def account = new Account(userId:userId, username:username, currentBalance:initialBalance,previousBalance:previousBalance)
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
	
	private Map createUserProfile(RestBuilder rest, String username, String email, String password, String gender, String region, String displayName){
			
		def resp = parseService.createUser(rest, username, email, password, gender, region, displayName)
		
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
	
	private Map userLogin(RestBuilder rest, String username, String password){
		
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
		def resp = parseService.retreiveUser(rest, userId)
		
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
		
		int currentBalanceResp = INITIAL_BALANCE
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
	
	def createSocialNetworkUser(String sessionToken){
		int currentBalance = INITIAL_BALANCE
		RestBuilder rest = new RestBuilder()
		
		Map userProfile = getUserProfileBySessionToken(rest, sessionToken)
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userProfile.objectId)
		
		if (account == null){
			Map accountCreationResult = createUserAccount(rest, userProfile.objectId, userProfile.username, INITIAL_BALANCE, PREVIOUS_BALANCE, userProfile.sessionToken)
			if (accountCreationResult!=[:]){
				return accountCreationResult
			}
		}else{
			currentBalance = account.currentBalance
		}
						
		def result = userProfileMapRender(sessionToken, currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name, 
		userProfile.objectId, userProfile.gender, userProfile.region, userProfile.email, userProfile.pictureURL)
		
		return result
	}
			
	def createUser(String username, String email, String password, String gender, String region){
		println "UserService::createUser(): username="+username+"  email="+email
		
		int currentBalance = INITIAL_BALANCE
		String usernameLowerCase = username.toLowerCase()
		String displayName = usernameLowerCase
		RestBuilder rest = new RestBuilder()
		
		Map userProfile =  createUserProfile(rest, usernameLowerCase, email, password, gender, region, displayName)
		if (userProfile.code){
			return userProfile
		}
		
		println "user profile created successfully"		
			
		Map accountCreationResult = createUserAccount(rest, userProfile.objectId, username, INITIAL_BALANCE, PREVIOUS_BALANCE, userProfile.sessionToken)
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
	
	def getUserProfile(String userId){
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
		
		def userPayoutTrans = betService.listPayoutTransByUserId(userId)
		def userStats = getBetStats(userPayoutTrans, account.id)
		//todo netgain/total wager in type 1
		userStats.weekly.netGainPercent = ((userStats.weekly.netGain / (account.currentBalance))*100).toInteger()
		userStats.monthly.netGainPercent = ((userStats.monthly.netGain / (account.currentBalance))*100).toInteger()
		userStats.all.netGainPercent = ((userStats.all.netGain / (account.currentBalance))*100).toInteger()
		
		def result = userProfileMapRender("", account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name,
			userProfile.objectId, userProfile.gender, userProfile.region, userProfile.email, userProfile.pictureURL)
		
		result.userStats = userStats
		result.level = 1
		result.levelName = "novice"
		
		return result		
		
	}
	
	def getUserBalance(String userId){
		def rest = new RestBuilder()
		def resp = parseService.retreiveUser(rest, userId)
		
		def account = Account.findByUserId(userId)
		if (account == null){
			println "ERROR: user account does not exist"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}
		return[currentBalance:account.currentBalance]
		
	}
	
	def updateUserProfile(String sessionToken, String userId, JSON updateUserData){
		def rest = new RestBuilder()
		def resp = parseService.updateUser(rest, sessionToken, userId, updateUserData)
		return resp.json
	}
	
	def getLeagueStats(accountId){
		def leagues=[:]
		def userStats = UserLeagueStats.findAllByAccountId(accountId)
		for (UserLeagueStats userStat : userStats){
			def leagueName = sportsDataService.getLeagueNameFromEventKey(userStat.league)
			def leagueMap = [:] 
			leagueMap = leagues.get(leagueName)
			if (leagueMap==null){
				leagueMap = [netGain:"0",wins:0,netLose:"0",losses:0,ties:0]
				leagues.put(leagueName, leagueMap)
			}
			if (userStat.gameResult=="loss"){
				String netGain = ""
				if (userStat.netGain>0)
					netGain="+"+userStat.netGain.toString()
				else
					netGain=userStat.netGain.toString()
					
				leagueMap.netLose=netGain
				leagueMap.losses=userStat.numGames
			}
			
			if (userStat.gameResult=="win"){
				String netGain = ""
				if (userStat.netGain>0)
					netGain="+"+userStat.netGain.toString()
				else
					netGain=userStat.netGain.toString()
					
				leagueMap.netGain=netGain
				leagueMap.wins=userStat.numGames
			}
			
			if (userStat.gameResult=="tie"){					
				leagueMap.ties=userStat.numGames
			}			
		}
		return leagues
	}
	
	def getBetStats(userPayoutTrans, accountId){
		
//		def stats = [all:[netGain:0, wins:0, losses:0, ties:0, leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]], 
//			monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]], 
//			weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]]]
		
		def stats = [all:[netGain:0, wins:0, losses:0, ties:0, leagues:getLeagueStats(accountId)],
			monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:getLeagueStats(accountId)],
			weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:getLeagueStats(accountId)]]
		
		if (userPayoutTrans==null || userPayoutTrans.size()==0){
			println "userPayoutTrans null"
			return stats
		}
		
		def firstDateOfCurrentWeek = getFirstDateOfCurrentWeek()
		def firstDateOfCurrentMonth = getFirstDateOfCurrentMonth()
		
		for (PoolTransaction tran: userPayoutTrans){
			
			if (tran.createdAt > firstDateOfCurrentWeek){
				stats.all.netGain += (tran.transactionAmount - tran.pick2Amount)
				stats.monthly.netGain+=(tran.transactionAmount - tran.pick2Amount)
				stats.weekly.netGain+=(tran.transactionAmount - tran.pick2Amount)
				if (tran.pick==0){
					stats.all.ties+=1
					stats.monthly.ties+=1
					stats.weekly.ties+=1					
				}else if(tran.transactionAmount == 0){
					stats.all.losses+=1
					stats.monthly.losses+=1
					stats.weekly.losses+=1
				}else if (tran.transactionAmount > 0){
					stats.all.wins+=1
					stats.monthly.wins+=1
					stats.weekly.wins+=1					
									
				}else{
					println "ERROR: UserService::getBetStats(): should not go in here"
				}
				continue
			}
			
			if (tran.createdAt > firstDateOfCurrentMonth){
				stats.monthly.netGain+=(tran.transactionAmount - tran.pick2Amount)
				stats.all.netGain+=(tran.transactionAmount - tran.pick2Amount)
				if (tran.pick==0){					
					stats.monthly.ties+=1
					stats.all.ties+=1
					continue
				}else if(tran.transactionAmount == 0){					
					stats.monthly.losses+=1
					stats.all.losses+=1
				}else if (tran.transactionAmount > 0){					
					stats.monthly.wins+=1
					stats.all.wins+=1										

				}else{
					println "ERROR: UserService::getBetStats(): should not go in here"
				}
				continue
			}
			stats.all.netGain+=(tran.transactionAmount - tran.pick2Amount)
			if (tran.pick==0){				
				stats.all.ties+=1
				continue
			}else if(tran.transactionAmount == 0){
				stats.all.losses+=1
			}else if (tran.transactionAmount > 0){
				stats.all.wins+=1

			}else{
				println "ERROR: UserService::getBetStats(): should not go in here"
			}						
		}
		
		
		return stats
	}
	
	def getFirstDateOfCurrentWeek(){
		Calendar c1 = Calendar.getInstance();   // this takes current date
		c1.clear(Calendar.MINUTE);
		c1.clear(Calendar.SECOND);
		c1.clear(Calendar.MILLISECOND);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.DAY_OF_WEEK, 2);		
		return c1.getTime();
	}
	
	def getFirstDateOfCurrentMonth(){
		Calendar c = Calendar.getInstance();   // this takes current date
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
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
}
