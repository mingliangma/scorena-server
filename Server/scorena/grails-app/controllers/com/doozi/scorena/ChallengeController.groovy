package com.doozi.scorena

import com.doozi.scorena.challenge.Challenge
import grails.converters.JSON

import java.util.Map;

class ChallengeController {
	def userService
	def challengeService
	
    def createChallenge() { 
		Map validateResult = validateCreateChallenge(request)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "createTournament(): validation = ${validateResult}"
			return
		}
		
		//validate userId exist on Parse
		def validation = userService.validateSession(request.JSON.sessionToken)
		if (validation.code){
			response.status = 404
			render validation as JSON
			log.info "createTournament(): validation = ${validation}"
			return
		}
		
		Question q = Question.findByEventKey(request.JSON.gameId)
		
		if (!q){
			response.status = 404
			Map errorResult = [error: "No questions are available for this gameId=${request.JSON.gameId}"]
			render errorResult as JSON
			log.info "createTournament(): errorResult = ${errorResult}"
			return
		}
		
		def result = challengeService.createChallenge(request.JSON.challengerUserId, request.JSON.challengeeUserId, request.JSON.gameId)
		println "what?!?!?!"
		render result as JSON
		
	}
	
	def acceptChallenge(){
		
		Map validateResult = validateAcceptChallenge(request)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "acceptChallenge(): validation = ${validateResult}"
			return
		}
		
		def validation = userService.validateSession(request.JSON.sessionToken)
		if (validation.code){
			response.status = 404
			render validation as JSON
			log.info "acceptChallenge(): validation = ${validation}"
			return
		}
		
		Challenge challenge = Challenge.get(request.JSON.challengeId.toLong())
		
		if (!challenge){
			response.status = 404
			Map errorResult = [error: "invalid challenge id"]
			render errorResult as JSON
			log.info "acceptChallenge(): errorResult = ${errorResult}"
			return
		}
		
		if (challenge.challengeeUserId != validation.objectId){
			response.status = 404
			Map errorResult = [error: "Challengee userId does not equal to your userId"]
			render errorResult as JSON
			log.info "acceptChallenge(): errorResult = ${errorResult}"
			return
		}
		
		Map result = challengeService.acceptChallenge(challenge)
		
		if (result.error){
			response.status = 500
			render result as JSON
			return
		}else{
			render result as JSON
			return
		}
		
	}
	
	def listChallenge(){
		
		if (!params.gameId){
			response.status = 404
			Map result = [error: "input parameter gameId is missing"]
			render result as JSON
			return
		}
		
		if (!params.userId){
			response.status = 404
			Map result = [error: "input parameter userId is missing"]
			render result as JSON
			return
		}
		
		def challenges = challengeService.listGameChallenges(params.gameId, params.userId)
		render challenges as JSON		
		
	}
	
	private Map validateAcceptChallenge(def request){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!request.JSON.challengeId){
			response.status = 404
			Map result = [error: "input parameter challengeId is missing"]
			return result
		}
		
		return [:]
	}
	
	private Map validateCreateChallenge(def request){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!request.JSON.challengerUserId == null){
			response.status = 404
			Map result = [error: "input parameter challengerUserIds is missing"]
			return result
		}
		
		if (!request.JSON.challengeeUserId == null){
			response.status = 404
			Map result = [error: "input parameter challengeeUserIds is missing"]
			return result
		}	
		
		if (!request.JSON.gameId == null){
			response.status = 404
			Map result = [error: "input parameter gameId is missing"]
			return result
		}
		
		return [:]
	}
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.info "${e.toString()}", e
	}
}
