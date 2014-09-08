package com.doozi.scorena.controllerservice

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

import com.doozi.scorena.*;

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
	def betService
	def sportsDataService
	
	def getCoins(userId){
		int asset = 0
		int inWagerAmount = 0
		
		def userAccount = Account.findByUserId(userId)
		if (!userAccount)
			return [code: 400, error:"userId is invalid"]
		
		def c = PoolTransaction.createCriteria()
		def lastPayoutDate = PoolTransaction.executeQuery("SELECT max(p.createdAt) from PoolTransaction p where p.account.id=? and p.transactionType=?", [userAccount.id, 1])		
		def totalBetAmount = PoolTransaction.executeQuery("SELECT sum(p.transactionAmount) from PoolTransaction p where p.account.id=? and p.transactionType=? and p.createdAt>?", [userAccount.id, 0, lastPayoutDate[0]])
		if (totalBetAmount[0] != null)
			inWagerAmount = totalBetAmount[0]
			
		asset = inWagerAmount+userAccount.currentBalance
		println asset
		
		if (asset >=FREE_COIN_BALANCE_THRESHOLD)
			return [code: 400, error:"Balance above "+FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"]
		
		userAccount.currentBalance = userAccount.currentBalance + FREE_COIN_AMOUNT
		return [username: userAccount.username, userId: userAccount.userId, currentBalance: userAccount.currentBalance, newCoinsAmount: FREE_COIN_AMOUNT]
	}
	
	Map getRanking(userId){
		def userRankingAll = UserRankingAll.findAll("from UserRankingAll RankingAll order by RankingAll.netGain desc, RankingAll.currentBalance desc", [max: RANKING_SIZE])
		def userRankingWk = UserRankingWk.findAll("from UserRankingWk RankingWk order by RankingWk.netGain desc, RankingWk.currentBalance desc", [max: RANKING_SIZE])
		List rankingResultAll =[]
		List rankingResultWk =[]
		
		int rankingAllSize = userRankingAll.size()
		int rankingWkSize = userRankingWk.size()
		List userIdList = []
		Map userIdMap = [:]
		
		
		for (int i=0; i<rankingAllSize; i++){
			UserRankingAll rankEntry = userRankingAll.get(i)
			Account userAccount = Account.get(rankEntry.id)
			rankingResultAll.add(getAccountInfoMap(userAccount.userId, userAccount.username, rankEntry.netGain,i+1))
			if (!userIdMap.containsKey(userAccount.userId)){
				userIdMap.put(userAccount.userId, "")				
			}
		}

		
		for (int i=0; i<rankingWkSize; i++){
			UserRankingWk rankEntry = userRankingWk.get(i)
			Account userAccount = Account.get(rankEntry.id)
			rankingResultWk.add(getAccountInfoMap(userAccount.userId, userAccount.username, rankEntry.netGain,i+1))
			if (!userIdMap.containsKey(userAccount.userId)){
				userIdMap.put(userAccount.userId, "")
			}
		}
		
		Map userProfileResults = parseService.retrieveUserList(userIdMap)
		Map UserProfileUserIdAsKeyMap = getUserProfileUserIdAsKeyMap(userProfileResults.results)
		
		for (Map rankingAllEntry: rankingResultAll){
			String accountUserId = rankingAllEntry.userId
			Map userProfile = UserProfileUserIdAsKeyMap.get(accountUserId)

			rankingAllEntry.pictureURL = ""
			
			if (userProfile != null){
				if (userProfile.display_name != null && userProfile.display_name != "")
					rankingAllEntry.username = userProfile.display_name
			
				if (userProfile.pictureURL != null && userProfile.pictureURL != "")
					rankingAllEntry.pictureURL = userProfile.pictureURL					
			}
			
		}
		
		for (Map rankingWkEntry: rankingResultWk){
			String accountUserId = rankingWkEntry.userId
			Map userProfile = UserProfileUserIdAsKeyMap.get(accountUserId)
					
			rankingWkEntry.pictureURL = ""
			if (userProfile != null){
				if (userProfile.display_name != null && userProfile.display_name != "")
					rankingWkEntry.username = userProfile.display_name
			
				if (userProfile.pictureURL != null && userProfile.pictureURL != "")
					rankingWkEntry.pictureURL = userProfile.pictureURL
			}
		}
		
		return [weekly: rankingResultWk, all: rankingResultAll]
	}
	
	public Map getUserProfileBySessionToken_tempFix(RestBuilder rest, String sessionToken){
		def resp = parseService.validateSessionT3(rest, sessionToken)
		if (resp.status != 200){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			return result
		}
		Map userProfileT3 = resp.json
		println userProfileT3
		Map userProfile = parseService.retrieveUserByDisplayName(userProfileT3.display_name)
		println userProfile.results[0]
		return userProfile.results[0]
	}
	
	def createSocialNetworkUser(String sessionToken){
		int currentBalance = SOCIALNETWORK_INITIAL_BALANCE
		RestBuilder rest = new RestBuilder()
		
		Map userProfile = getUserProfileBySessionToken_tempFix(rest, sessionToken)
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
		
		println userProfile
		if (userProfile.code){
			return userProfile
		}
		
		def account = Account.findByUserId(userProfile.objectId)
		
		if (!account){
			return [code:500, error:"user account does not exist"]			
		}
		
		def result = userProfileMapRender(userProfile.sessionToken, account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name, 
				userProfile.objectId, userProfile.gender, userProfile.region, userProfile.email, userProfile.pictureURL)
		println result
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
		
		int netGainPercentageDenominator = 1
		if (account.currentBalance > 0){
			netGainPercentageDenominator = account.currentBalance
		}
		
		userStats.weekly.netGainPercent = ((userStats.weekly.netGain / (netGainPercentageDenominator))*100).toInteger()
		userStats.monthly.netGainPercent = ((userStats.monthly.netGain / (netGainPercentageDenominator))*100).toInteger()
		userStats.all.netGainPercent = ((userStats.all.netGain / (netGainPercentageDenominator))*100).toInteger()
		
		def result = userProfileMapRender("", account.currentBalance, userProfile.createdAt, userProfile.username, userProfile.display_name,
			userProfile.objectId, userProfile.gender, userProfile.region, userProfile.email, userProfile.pictureURL)
		
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
		def unpaidTransactions = betService.listUnpaidBetsByUserId(userId)
		for (PoolTransaction unpaidTransaction: unpaidTransactions){
			inWager += unpaidTransaction.transactionAmount
		}
		return inWager
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
		
		println firstDateOfCurrentWeek
		println firstDateOfCurrentMonth
		
		for (PoolTransaction tran: userPayoutTrans){
			
			if (tran.createdAt > firstDateOfCurrentWeek){
				println tran.createdAt
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

	private Map getAccountInfoMap(String userId, String username, int netgain, int rank){	
		String netGain = ""
		if (netgain>0)
			netGain="+"+netgain.toString()
		else
			netGain=netgain.toString()
			
		return [userId: userId, username: username, gain: netGain, rank: rank]
	}

	private def getFirstDateOfCurrentWeek(){
		Calendar c1 = Calendar.getInstance();   // this takes current date
		c1.clear(Calendar.MINUTE);
		c1.clear(Calendar.SECOND);
		c1.clear(Calendar.MILLISECOND);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.DAY_OF_WEEK, 2);		
		return c1.getTime();
	}

	private def getFirstDateOfCurrentMonth(){
		Calendar c = Calendar.getInstance();   // this takes current date
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
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

	private Map getUserProfileUserIdAsKeyMap(List userProfileList){
		Map UserProfileUserIdAsKeyMap = [:]
		for (Map userProfile: userProfileList){
			UserProfileUserIdAsKeyMap.put(userProfile.objectId, userProfile)
		}
		return UserProfileUserIdAsKeyMap
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
}
