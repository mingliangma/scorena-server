package com.doozi.scorena.transaction

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder
import grails.async.Promise
import static grails.async.Promises.*

class BetController {
	def betTransactionService
	def userService
	def notificationService
    
	def placeBet() { 
		log.info "placeBet(): begins..."
		
		//validate all required input parameters exist
		Map validateResult = validatePlaceBetRequest(request)
		if (validateResult != [:]){
			response.status = 404
			render validateResult as JSON
			log.error "placeBet(): validateResult = ${validateResult}"
			return
		}
		
		//validate userId exist on Parse
		def validation = userService.validateSession(request.JSON.sessionToken)
		if (validation.code){
			response.status = 404
			render validation as JSON
			log.error "placeBet(): validation = ${validation}"
			return
			
		} 
		
		//place user's bet
		Map result = betTransactionService.createBetTrans(request.JSON.wager.toInteger(), request.JSON.pick.toInteger(), validation.objectId , 
			request.JSON.questionId.toInteger())
				
		if (result == [:]){
						
			Promise p = task {
				
				notificationService.friendBetReminder(validation.objectId, request.JSON.wager.toInteger(),
					 request.JSON.pick.toInteger(), request.JSON.questionId.toLong())
			}
			p.onComplete { cresult ->
				println "friendBetReminder completed and returned $cresult"
			}
		}
		
		if (result==[:]){			
			def resp = [date:new Date()]
			response.status = 201
			render resp as JSON
			log.info "placeBet(): ends with resp = ${resp}"
			return
		}else{
			response.status =404	
			render result as JSON
			log.error "placeBet(): result = ${result}"
			return
		}
//		
//		Question question = Question.get(request.JSON.questionId.toInteger())
//		pushService.registerUserToGameChannel(validation.objectId, question.eventKey)
//		return
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
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.error "${e.toString()}", e
	}
}
