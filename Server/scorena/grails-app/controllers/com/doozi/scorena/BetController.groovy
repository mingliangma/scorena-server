package com.doozi.scorena

import grails.converters.JSON

//curl -i -v -X POST -H "Content-Type: application/json" -d '{"sessionToken":"wp86tnzle4j9hlx0scuynqjpy", "gameId":"1", "questionId":"1", "pick":"1", "wager":"5"}' localhost:8080/scorena/v1/sports/soccer/leagues/epl/wagers/new
class BetController {
	def BetService
	def userService
    def placeBet() { 
		if (!request.JSON.wager||!request.JSON.pick|| !request.JSON.questionId|| !request.JSON.gameId||!request.JSON.sessionToken){
			response.status =404
			def resp = [
				code: 101,
				error: "invalid parameters"
				]
			render resp as JSON
		}
		
		def sessionValidation = userService.validateSession(request.JSON.sessionToken)
		//println "session code: "+sessionValidation.code
		if (sessionValidation.code){
			println "session code: "+sessionValidation
			response.status = 404
			render sessionValidation as JSON
			
		}else{
			def today = new Date() 
			def result = betService.saveBetTrans(request.JSON.wager.toInteger(), today,request.JSON.pick.toInteger(), sessionValidation.objectId, request.JSON.questionId.toInteger(), request.JSON.gameId.toInteger())
			if (result==201){
				
				def resp = [date:today]
				response.status = 201
				render resp as JSON
			}else{
				response.status =400
				def resp = [
					code: 202,
					error: "the bet transaction did not go through"
					]
				render resp as JSON
			}
		}
	}
}
