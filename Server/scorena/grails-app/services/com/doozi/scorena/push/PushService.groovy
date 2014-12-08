package com.doozi.scorena.push

import grails.plugins.rest.client.RestBuilder
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.transaction.AbstractTransaction
import grails.transaction.Transactional

@Transactional
class PushService {
 
	def grailsApplication
	def gameService
	
    def updateGameChannel(def rest, String objectID, String eventKey)
	{
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
		
		return resp.json.toString()
	}
	
	def removeGameChannel(def rest, String objectID,String eventKey)
	{
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
		return resp.json.toString()
	}
	
	def getInstallationByUserID(def rest, String userid)
	{
		def parseConfig = grailsApplication.config.parse
		
		def param = ["userId": (userid)]
		def resp = rest.get("https://api.parse.com/1/installations/")
		{
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header  "X-Parse-Master-Key", parseConfig.parseMasterKey
			contentType "application/json"
			json{
				where = param
				}
		}
		
		//return resp.json
		System.out.println(resp.json.results[0].objectId.toString())
		return resp.json.results[0].objectId.toString()
	}
	
	def sendGameStartPush(def rest, String eventKey)
	{
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
		return resp.json.toString()
	}
	
 def endOfGamePush(def rest, String eventKey,String userid, String msg)
 {
	 def parseConfig = grailsApplication.config.parse
	 def Map game = gameService.getGame(eventKey)
	 
	 def userParam = ["userId": (userid)]
	 def chanParam = ["channels":['$in':[(eventKey)]]]
	 def alertParam = ["alert": (msg)]
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
	 return resp.json.toString()
 }
 
}
