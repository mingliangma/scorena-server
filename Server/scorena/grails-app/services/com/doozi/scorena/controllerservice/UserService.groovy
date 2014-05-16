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
	
	def getCoins(userId){
		def userAccount = Account.findByUserId(userId)
		if (!userAccount)
			return [code: 400, error:"userId is invalid"]
		
		if (userAccount.currentBalance >=FREE_COIN_BALANCE_THRESHOLD)
			return [code: 400, error:"Balance above "+FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"]
			
		userAccount.currentBalance = userAccount.currentBalance + FREE_COIN_AMOUNT
		return [username: userAccount.username, userId: userAccount.userId, currentBalance: userAccount.currentBalance, newCoinsAmount: FREE_COIN_AMOUNT]
	}
	
	def processRankingData(Account user, int rank){		
		return [username: user.username, gain: user.currentBalance, rank: rank]
	}
	
	def getRanking(userId){
		
		def rest = new RestBuilder()
		def resp = parseService.retreiveUser(rest, userId)
		
		if (resp.status != 200){
			println "ERROR: UserService::getUserProfile(): user account does not exist in parse"
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
		}
		def userAccount = Account.findByUserId(userId)
		def userBalance = userAccount.currentBalance
		
		def higherRankingList = Account.findAll("from Account as a where a.currentBalance > ? order by a.currentBalance", [userBalance])
		
		int higherRank = higherRankingList.size()
		int currentUserRank = higherRank + 1
		int lowerRank = currentUserRank +1
		List rankingResult =[]
		int higherRankingSize = 5
		if (higherRankingList.size()<5)
			higherRankingSize=higherRankingList.size()
		
		for (int i=0; i<higherRankingSize; i++){			
			rankingResult.add(0,processRankingData(higherRankingList.get(i), higherRank))
			higherRank -= 1
		} 
		
		rankingResult.add(processRankingData(userAccount, currentUserRank))
		
		def lowerRankingList = Account.findAll("from Account as a where a.currentBalance < ? order by a.currentBalance", [userBalance])
		int lowerRankingSize = 5
		if (lowerRankingList.size()<5)
			lowerRankingSize=lowerRankingList.size()
			
		for (int i=0; i<lowerRankingSize; i++){
			rankingResult.add(processRankingData(lowerRankingList.get(i), lowerRank))
			lowerRank +=1
		}
		
		return [weekly: rankingResult, all: rankingResult]
	}
			
	def createUser(String _username, String _email, String _password, String gender, String region){
		def rest = new RestBuilder()
		_username = _username.toLowerCase()
		def resp = parseService.createUser(rest, _username.toLowerCase(), _email, _password, gender, region)
		
		if (resp.status != 201){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			println result
			return result
		}
		
		

		println "user profile created successfully"		
			
		def account = new Account(userId:resp.json.objectId, username:_username, currentBalance:INITIAL_BALANCE,previousBalance:PREVIOUS_BALANCE)
		if (!account.save()){

			def delResp = parseService.deleteUser(rest, resp.json.sessionToken)
			def result = [
				code:202,
				error:"account creation failed"
			]
			return result			
		}
				
		println "user profile and account created successfully"
		def result = [			
			createdAt:resp.json.createdAt,
			username:_username,
			currentBalance:INITIAL_BALANCE,
			sessionToken:resp.json.sessionToken,
			userId: resp.json.objectId,
			email: _email,
			gender: gender,
			region: region
		]
		return result		
	}
	
	def login(String username, String password){
		def rest = new RestBuilder()
		def resp = parseService.loginUser(rest, username, password)
		if (resp.status != 200 ||resp.json.code)
			return resp.json
		
		def account = Account.findByUserId(resp.json.objectId)
		
		if (!account){
			return [code:500, error:"user account does not exist"]			
		}
		def result = [
			createdAt:resp.json.createdAt,
			username:resp.json.username,
			currentBalance:account.currentBalance,
			sessionToken:resp.json.sessionToken,
			userId: resp.json.objectId,
			createdAt: resp.json.createdAt,
			email: resp.json.email,
			gender: resp.json.gender,
			region: resp.json.region
		]
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
		def resp = parseService.retreiveUser(rest, userId)
		
		if (resp.status != 200){
			println "ERROR: UserService::getUserProfile(): user account does not exist in parse"			
			def result = [
				code:500,
				error:"user account does not exist"
			]
			return result
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
		def userStats = getBetStats(userPayoutTrans)
		//todo netgain/total wager in type 1
		userStats.weekly.netGainPercent = ((userStats.weekly.netGain / (account.currentBalance))*100).toInteger()
		userStats.monthly.netGainPercent = ((userStats.monthly.netGain / (account.currentBalance))*100).toInteger()
		userStats.all.netGainPercent = ((userStats.all.netGain / (account.currentBalance))*100).toInteger()
		
		def result = [
			createdAt:resp.json.createdAt,
			username:resp.json.username,
			currentBalance:account.currentBalance,
			updatedAt:resp.json.updatedAt,,
			userId: resp.json.objectId,
			gender: resp.json.gender,
			region: resp.json.region,
			email: resp.json.email,
			userStats: userStats,
			level: 1,
			levelName: "novice"
		]
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
	
	def getBetStats(userPayoutTrans){
		
//		def stats = [all:[netGain:0, wins:0, losses:0, ties:0, leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]], 
//			monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]], 
//			weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]]]
		
		def stats = [all:[netGain:0, wins:0, losses:0, ties:0, leagues:[All_Leagues:[netGain:4,wins:2,netLose:2,losses:7,ties:3]]],
			monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:[All_Leagues:[netGain:0,wins:5,netLose:2,losses:3,ties:0]]],
			weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:[All_Leagues:[netGain:0,wins:3,netLose:2,losses:0,ties:0]]]]
		
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
}
