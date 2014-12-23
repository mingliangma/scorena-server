package com.doozi.scorena

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder


class BetController {
	def betTransactionService
	def userService

    
	def placeBet() { 
		
		//validate all required input parameters exist
		Map validateResult = validatePlaceBetRequest(request)
		if (validateResult != [:]){
			response.status = 404
			render validateResult as JSON
			return
		}
		
		//validate userId exist on Parse
		def validation = userService.validateSession(request.JSON.sessionToken)
		if (validation.code){
			response.status = 404
			render validation as JSON
			return
			
		} 
		
		//place user's bet
		Map result = betTransactionService.createBetTrans(request.JSON.wager.toInteger(), request.JSON.pick.toInteger(), validation.objectId , 
			request.JSON.questionId.toInteger())
		
		
		if (result==[:]){			
			def resp = [date:new Date()]
			response.status = 201
			render resp as JSON
			return
		}else{
			response.status =404	
			render result as JSON
			return
		}
		
	}
	
	
	/**
	 * validate all required input parameters exist
	 * @param request
	 * @return
	 */
	private Map validatePlaceBetRequest(def request){
		if (request.JSON.wager == null||!request.JSON.pick|| !request.JSON.questionId || !request.JSON.sessionToken){
			response.status =404
			def resp = [
				code: 101,
				error: "invalid parameters"
				]
			return resp
		}
		return [:]
	}
}

