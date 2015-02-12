package com.doozi.scorena.push

import grails.plugins.rest.client.RestBuilder
import java.net.URLEncoder;
import org.apache.http.util.EntityUtils;
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.transaction.AbstractTransaction
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.DefaultHttpClient
import org.codehaus.groovy.grails.web.json.JSONObject
import org.springframework.transaction.annotation.Transactional

@Transactional
class PushService {
 
	def grailsApplication
	def gameService
	
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
		def resp = rest.post("https://api.parse.com/1/push")
		{
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json{
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
//	 def chanParam = ["channels":['$in':[(eventKey)]]]
	 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus), "pushType":"result"]
	 def resp = rest.post("https://api.parse.com/1/push")
	 {
		 header "X-Parse-Application-Id", parseConfig.parseApplicationId
		 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		 contentType "application/json"
		 json{
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
	 def chanParam = ["channels":['$in':[(eventKey)]]]
	 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus),"pushType":"question"]
	 def resp = rest.post("https://api.parse.com/1/push")
	 {
		 header "X-Parse-Application-Id", parseConfig.parseApplicationId
		 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		 contentType "application/json"
		 json{
			 data = alertParam
			 where = chanParam
			 }
	 }
	 
	 def result = resp.json.toString()
	 log.info "customQuestionPush(): ends with result = ${result}"
	 
	 return resp.json.toString()
 }
 
 def sendFollowPush(def rest, String followingId, String followerId, String msg)
 {
	 log.info "sendFollowPush(): begins with rest = ${rest}, followingId = ${followingId}, followerId = ${followerId} ,msg = ${msg}"
	 
	 def parseConfig = grailsApplication.config.parse
	 def userParam = ["userId": (followingId)]
	 def alertParam = ["alert": (msg),"followerId":(followerId),"pushType":"userProfile"]
	 def resp = rest.post("https://api.parse.com/1/push")
	 {
		 header "X-Parse-Application-Id", parseConfig.parseApplicationId
		 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		 contentType "application/json"
		 json{
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
	 def userParam  = ["questions":['$in':[(qId.toInteger())]]]
	 def alertParam = ["alert": (msg),"gameKey":(eventKey),"gameStatus":(eventStatus), "questionKey":(qId), "pushType":"comment"]
	 def resp = rest.post("https://api.parse.com/1/push")
	 {
		 header "X-Parse-Application-Id", parseConfig.parseApplicationId
		 header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		 contentType "application/json"
		 json{
			 data = alertParam
			 where = userParam
			 }
	 

	 }
	 
	 def result = resp.json.toString()
	 log.info "usrCommentPush(): ends with result = ${result}"
	 
	 return resp.json.toString()
 }
 
}
