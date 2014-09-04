package com.doozi.scorena

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder

//curl -i -v -X POST -H "Content-Type: application/json" -d '{"sessionToken":"wp86tnzle4j9hlx0scuynqjpy", "gameId":"1", "questionId":"1", "pick":"1", "wager":"5"}' localhost:8080/scorena/v1/sports/soccer/leagues/epl/wagers/new
class BetController {
	def BetService
	def userService
    def placeBet() { 
		if (!request.JSON.wager||!request.JSON.pick|| !request.JSON.questionId || !request.JSON.sessionToken){
			response.status =404
			def resp = [
				code: 101,
				error: "invalid parameters"
				]
			render resp as JSON
		}
		Boolean validationSuccess = false
		String objectId = ""
		
		def validation = userService.validateSession(request.JSON.sessionToken)
		if (!validation.code){
			validationSuccess = true
			objectId = validation.objectId
		}
		Map validationT3
		if (!validationSuccess){
			RestBuilder rest = new RestBuilder()
			validationT3 = userService.getUserProfileBySessionToken_tempFix(rest, request.JSON.sessionToken)
			if (!validationT3.code){
				validationSuccess = true
				objectId = validationT3.objectId
			} 
		}
		//println "session code: "+sessionValidation.code
		if (!validationSuccess){
			response.status = 404
			render validation as JSON
			
		}else{
			def today = new Date() 
			def result = betService.saveBetTrans(request.JSON.wager.toInteger(), today,request.JSON.pick.toInteger(),objectId , request.JSON.questionId.toInteger())
			if (result.code==201){
				
				def resp = [date:today]
				response.status = 201
				render resp as JSON
			}else{
				response.status =400
				def resp = [
					code: result.code,
					error: result.message
					]
				render resp as JSON
			}
		}
	}
}

