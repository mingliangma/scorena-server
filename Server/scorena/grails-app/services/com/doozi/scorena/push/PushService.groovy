package com.doozi.scorena.push

import grails.plugins.rest.client.RestBuilder

import java.net.URLEncoder;
import java.util.Map;

import org.apache.http.util.EntityUtils;

import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.transaction.AbstractTransaction
import com.doozi.scorena.*;

import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.transaction.annotation.Transactional
import com.doozi.scorena.transaction.LeagueTypeEnum

@Transactional
class PushService {
 
	def grailsApplication
	def gameService
	def profitRankingService
	def helperService
	
    def updateGameChannel(def rest, String objectID, String eventKey)
	{
		log.info "updateGameChannel(): begins with rest = ${rest}, objectID = ${objectID}, eventKey = ${eventKey}"
		
		def parseConfig = grailsApplication.config.parse		
		
		def param = ["__op": "AddUnique" ,"objects":[(eventKey)]]
		def resp = rest.put("https://api.parse.com/1/installations/"+ objectID)
		{
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json{
				channels = param
				}
		}
		
		def result = resp.json.toString()
		log.info "updateGameChannel(): ends with ${result}"
		
		return resp.json.toString()
	}
	
	def removeGameChannel(def rest, String objectID,String eventKey)
	{
		log.info "removeGameChannel(): begins with rest = ${rest}, objectID = ${objectID}, eventKey = ${eventKey}"
		
		def parseConfig = grailsApplication.config.parse
		
		
		def param = ["__op": "Remove" ,"objects":[(eventKey)]]
		def resp = rest.put("https://api.parse.com/1/installations/"+ objectID)
		{
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json{
				channels = param
				}
		}
		
		def result = resp.json.toString()
		log.info "removeGameChannel(): ends with result = ${result}"
		
		return resp.json.toString()
	}
	
	def getInstallationByUserID(String userId)
	{
		log.info "getInstallationByUserID(): begins with userId = ${userId}"
		
		def parseConfig = grailsApplication.config.parse		

		String whereContraintsString = URLEncoder.encode('{"userId":"' + userId + '"}', "UTF-8")
		
		String url = "https://api.parse.com/1/installations/?where="+ whereContraintsString 
		
		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);
				
		httpget.addHeader("X-Parse-Application-Id", parseConfig.parseApplicationId);
		httpget.addHeader("X-Parse-REST-API-Key", parseConfig.parseRestApiKey);
		httpget.addHeader("X-Parse-Master-Key", parseConfig.parseMasterKey);
		
		HttpResponse httpResponse = httpclient.execute(httpget);
		JSONObject resultJson = new JSONObject(EntityUtils.toString(httpResponse.getEntity()));
		
		def result = resultJson.results.objectId 	
		log.info "getInstallationByUserID(): ends with result = ${result}"
	
		return resultJson.results.objectId 		
	}
	
	def sendGameStartPush(def rest, String eventKey)
	{
		log.info "sendGameStartPush(): begins with rest = ${rest}, eventKey = ${eventKey}"
		
		def parseConfig = grailsApplication.config.parse
		def Map game = gameService.getGame(eventKey)
		
		String home = game.home.teamname
		String away = game.away.teamname
		String msg =  "The game you picked between"+ away +" vs"+ home +" will start shortly."
		
		
		def chanParam = ["channels":['$in':[(eventKey)]]] 
		def alertParam = ["alert": (msg)]
		def expireParam = helperService.setPushExpireTime(1)
		def resp = rest.post("https://api.parse.com/1/push")
		{
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json{
				expiration_time = expireParam
				data = alertParam
				where = chanParam
				}
		}
		
		def result = resp.json.toString()
		log.info "sendGameStartPush(): ends with result = ${result}"
		
		return resp.json.toString()
	}
	
	 def endOfGamePush(def rest,String eventKey,String eventStatus, String userId, String msg)
	 {
		 log.info "endOfGamePush(): begins with rest = ${rest},eventKey = ${eventKey},  eventStatus = ${eventStatus} , userId = ${userId}, msg = ${msg}"
		 
		 def parseConfig = grailsApplication.config.parse	 
		 def userParam = ["userId": (userId)]
		 def expireParam = helperService.setPushExpireTime(24)
	//	 def chanParam = ["channels":['$in':[(eventKey)]]]
		 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus), "pushType":"result"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = userParam
				 }	 
		 }
		 
		 def result = resp.json.toString()
		 log.info "endOfGamePush(): ends with result = ${result}"
		 
		 return resp.json.toString()
	 }
 
 
	 def customQuestionPush(def rest,String eventKey, String eventStatus ,String msg)
	 {
		 log.info "customQuestionPush(): begins with rest = ${rest}, eventKey = ${eventKey},  eventStatus = ${eventStatus} , msg = ${msg}"
		 
		 def parseConfig = grailsApplication.config.parse
		 def expireParam = helperService.setPushExpireTime(24)
		 def chanParam = ["channels":['$in':[(eventKey)]]]
		 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus),"pushType":"question"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = chanParam
				 }
		 }
		 
		 def result = resp.json.toString()
		 log.info "customQuestionPush(): ends with result = ${result}"
		 
		 return resp.json.toString()
	 }
	 
	 def questionPushTargetUserId(def rest, List<String> userIds, String eventKey, String eventStatus, String msg){
		 log.info "questionPushTargetUserId(): begins with rest = ${rest}, eventKey = ${eventKey},  eventStatus = ${eventStatus} , msg = ${msg}"
		 
		 def parseConfig = grailsApplication.config.parse
		 def userParam = ["userId": ['$in':userIds]]
		 def expireParam = helperService.setPushExpireTime(48)
		 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus),"pushType":"question"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = userParam
				 }
		 }
		 
		 def result = resp.json.toString()
		 log.info "questionPushTargetUserId(): ends with result = ${result}"
		 
		 return resp.json.toString()
	 }
	 
	 def inactiveUsersReminder(){
		 
		 List<String> inactiveUserIds = Account.executeQuery("select userId from Account as a where  a.id > 30 and a.id not in "+ 
			 "(select t.account.id from BetTransaction as t where t.createdAt > ? group by t.account.id)", [new Date() -3])
		 
		 List<Map> upcomingNBAGames = gameService.listUpcomingGamesData("", LeagueTypeEnum.NBA.toString())
		 if (upcomingNBAGames.size() > 0){
			 def rest = new RestBuilder()
			 Map upcomingNBAGame = upcomingNBAGames.get(0)
			 List<Question> questions = Question.findAllByEventKey(upcomingNBAGame.gameId)
			 if (questions.size() > 3){
				Question q = questions.last()
				String message = q.questionContent.content + " "+q.pick1 + " or " + q.pick2
				questionPushTargetUserId(rest, inactiveUserIds, upcomingNBAGame.gameId, upcomingNBAGame.gameStatus, message)
			 }else{
			 	Question q = questions.first()
				 String message = q.questionContent.content + " "+q.pick1 + " or " + q.pick2
				 questionPushTargetUserId(rest, inactiveUserIds, upcomingNBAGame.gameId, upcomingNBAGame.gameStatus, message)
			 }
		 }
		 
	 }
 
	 def sendFollowPush(def rest, String followingId, String followerId, String msg)
	 {
		 log.info "sendFollowPush(): begins with rest = ${rest}, followingId = ${followingId}, followerId = ${followerId} ,msg = ${msg}"
		 
		 def parseConfig = grailsApplication.config.parse
		 def expireParam = helperService.setPushExpireTime(24)
		 def userParam = ["userId": (followingId)]
		 def alertParam = ["alert": (msg),"followerId":(followerId),"pushType":"userProfile"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = userParam
				 }
		 }
		 
		 def result = resp.json.toString()
		 log.info "sendFollowPush(): ends with result = ${result}"
		 
		 return resp.json.toString()
	 }
 
	 def userCommentPush(def rest,String userList, String eventKey, String eventStatus ,String qId, String msg)
	 {
		 log.info "usrCommentPush(): begins with rest = ${rest}, userList = ${userList}, eventKey = ${eventKey}, eventStatus = ${eventStatus} ,qId = ${qId} , msg = ${msg}"
		 
		 def parseConfig = grailsApplication.config.parse
		 def expireParam = helperService.setPushExpireTime(24)
		 def userParam  = ["questions":['$in':[(qId.toInteger())]]]
		 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus), "questionKey":(qId), "pushType":"comment"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = userParam
				 }
		 
	
		 }
		 
		 def result = resp.json.toString()
		 log.info "usrCommentPush(): ends with result = ${result}"

		 return resp.json.toString()
	 }
 
	 public void sendEndGamePush(Map userTotalGameProfit, Map gameIdToGameInfoMap)
	 {
		 log.info "sendEndGamePush(): begins and "+gameIdToGameInfoMap.size()+" games will be pushed"
		 def rest = new RestBuilder()
	
		 //userTotalGamesProfit = [gameId : [userId: game profit]]
		 Set gameIds =  userTotalGameProfit.keySet()
		 for (String gameId: gameIds){
			 Map userAGameProfit = userTotalGameProfit[gameId]
			 String status = gameIdToGameInfoMap[gameId].gameStatus
			// println("status - " + status)
			 
			 String awayTeam = gameIdToGameInfoMap[gameId].away.teamname
			 String homeTeam = gameIdToGameInfoMap[gameId].home.teamname
			 String[] userIdKeys = userAGameProfit.keySet()
			 
//			 List userRanks = profitRankingService.getGameRanking(gameId)
//			 
//			 for (Map rank: userRanks)
//			 {
//				 String msg = ""
//				 switch (rank.rank)
//				 {
//					 case 1:
//						 msg ="You have ranked: " + rank.rank + "st in game "+ awayTeam +" vs "+ homeTeam
//						 break;
//					 case 2:
//						 msg ="You have ranked: " + rank.rank + "nd in game "+ awayTeam +" vs "+ homeTeam
//						 break;
//					 case 3:
//						 msg ="You have ranked: " + rank.rank + "rd in game "+ awayTeam +" vs "+ homeTeam
//						 break;
//					default:
//						msg ="You have ranked: " + rank.rank + "th in game "+ awayTeam +" vs "+ homeTeam
//						break;	 
//				 }
//				 
//				 println(rank.userId + " " + msg)
//				 def payoutPush = endOfGamePush(rest,gameId, status ,rank.userId, msg)
//			 }
			 
			for (String userID: userIdKeys )
			 {
				 int gameProfit = userAGameProfit[userID]
					 String msg = ""
					 if ( gameProfit > 0)
					 {
						 msg = "Congratulations! You have won " + gameProfit +" Coins in game "+ awayTeam +" vs "+ homeTeam
					 }
					 
					 else if (gameProfit == 0)
					 {
						 msg = "Sorry, you did not win any Coins in game "+ awayTeam +" vs "+ homeTeam
					 }
					 
					 else
					 {
						 msg = "Sorry, You have lost "+ Math.abs(gameProfit) +" Coins in game "+ awayTeam +" vs "+ homeTeam
					 }
					 
					 // sends end of game push to user with amount of coins won or lost
					 def payoutPush = endOfGamePush(rest,gameId, status ,userID, msg)
				  
			 }
		 }
		 log.info "sendEndGamePush(): ends"
	 }
 
	 public void registerUserToGameChannel(String userId, String eventKey){
		 log.info "registerUserToGameChannel(): begins userId=${userId} eventKey=${eventKey}"
		 // gets the users decvice installation ID by username
		 List objectIDs = getInstallationByUserID(userId)
	 
		 
		 if ( objectIDs != null || objectIDs != "" )
		 {
			 // preps event key for pars channel. parse does not allow for  '.' in a channel name, replaces it with a "_"
			 String parse_channel = eventKey.replace(".","_")
			 
			 
			 for (String objectId: objectIDs)
			 {
			 // Registers user device into push channel for game event key
			 def test = updateGameChannel(new RestBuilder(), objectId, parse_channel)
			 }
		 }
		 log.info "registerUserToGameChannel(): ends"
	 }
 
	 def tournamentInvitationNotification(def rest,long tournamentId, String userId, String msg)
	 {
		 log.info "tournamentInvitationNotification(): begins with rest = ${rest},tournamentId = ${tournamentId}, userId = ${userId}, msg = ${msg}"
		 
		 def parseConfig = grailsApplication.config.parse
		 def expireParam = helperService.setPushExpireTime(24)
		 def userParam = ["userId": (userId)]
		 def alertParam = ["alert": (msg),"tournamentId":(tournamentId), "pushType":"tournamentInvitation"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = userParam
				 }
		 }
		 
		 def result = resp.json.toString()
		 log.info "tournamentInvitationNotification(): ends with result = ${result}"

		 return resp.json.toString()
	 }
	 
	 def acceptTournamentNotification(def rest,long tournamentId, String userId, String msg, String title,String description,Date startDate,Date expireDate,String subscribedLeagues)
	 {
		 log.info "acceptTournamentNotification(): begins with rest = ${rest},tournamentId = ${tournamentId}, userId = ${userId}, subscribedLeagues = ${subscribedLeagues} , title = ${title} , description = ${description}"
		 
		 def parseConfig = grailsApplication.config.parse
		 def expireParam = helperService.setPushExpireTime(24)
		 def userParam = ["userId": (userId) ]
		 def alertParam = ["alert": (msg),"tournamentId":(tournamentId) ,"subscribedLeagues":(subscribedLeagues), 
			 "title":(title) ,"description":(description) ,"startDate":(startDate) ,"expireDate":(expireDate), "pushType":"acceptTournament"]
		 def resp = rest.post("https://api.parse.com/1/push")
		 {
			 header "X-Parse-Application-Id", parseConfig.parseApplicationId
			 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			 contentType "application/json"
			 json{
				 expiration_time = expireParam
				 data = alertParam
				 where = userParam
				 }
		 }
		 
		 def result = resp.json.toString()
		 log.info "acceptTournamentNotification(): ends with result = ${result}"
		 
		 return resp.json.toString()
	 }
	 
	
}
