package com.doozi.scorena

import com.doozi.scorena.challenge.Challenge
import grails.converters.JSON
import com.doozi.scorena.utils.*
import java.util.Map;
import com.doozi.scorena.enums.*
import grails.async.Promise
import static grails.async.Promises.*

class ChallengeController {
	def userService
	def challengeService
	def notificationService
	def gameService
	
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
		
		if (result.error){
			response.status = 404
			render result as JSON
			return
		}
		
		Promise p = task {

			notificationService.challengeInvitationNotification(result.challengeId.toLong(), 
				request.JSON.challengeeUserId, validation.display_name, result.eventKey)
		}
		p.onComplete { promiseResult ->
			println "success returned $promiseResult"
		}
		
		render result as JSON
		
	}
	
	def createBotChallengeWithRandomPlayer(){
		challengeService.createBotChallengeWithRandomPlayer()
		
		Map result = [status: "complete"]
		render result as JSON
	}
	
	def createChallengeWithRandomPlayer(){
		
		Map validateResult = validateCreateChallengeWithRandomPlayer(request)
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
		
		def result = challengeService.createChallengeWithRandomPlayer(validation.objectId, request.JSON.gameId)
		
		if (result.error){
			response.status = 404
			render result as JSON
			return
		}else{		
			Promise p = task {			
					notificationService.challengeInvitationNotification(result.challengeId.toLong(),
						result.challengeeUserId, validation.display_name, result.eventKey)
				}
			p.onComplete { promiseResult ->
				println "success returned $promiseResult"
			}
			
			
			if (result.challengeeAccountType == AccountType.TEST){
				Promise p1 = task {
					Map acceptResult = challengeService.acceptChallenge(result.challengeId.toLong())
					notificationService.challengeAcceptanceNotification(result.challengeId.toLong(),
						validation.objectId, result.challengeeDisplayName, result.eventKey)
				}
				p1.onComplete { promiseResult ->
					println "success returned $promiseResult"
				}
				
				
			}
			render result as JSON
		}
	}
	
	def acceptChallenge(){
		
		Map validateResult = validateAcceptChallenge(request, params)
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
		
		Challenge challenge = Challenge.get(params.challengeId.toLong())
		
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
		
		if (challenge.challengeStatus == ChallengeStatusEnum.ACCEPTED){
			Map result = [:]
			render result as JSON
			return
		}
		
		Map result = challengeService.acceptChallenge(challenge)
		
		if (result.error){
			response.status = 500
			render result as JSON
			return
		}else{
		
			Account account = Account.findByUserId(challenge.challengeeUserId)
			String challengeeDisplayName = account.displayName
			
			Promise p1 = task {
				Map game = gameService.getGame(challenge.eventKey)
				notificationService.challengeAcceptanceNotification(challenge.id,
					challenge.challengerUserId, challengeeDisplayName,
					game.home.teamname, game.away.teamname)
			}
			p1.onComplete { promiseResult ->
				println "success returned $promiseResult"
			}
			render result as JSON
			return
		}
		
	}
	
	def ignoreChallenge(){
		
		Map validateResult = validateAcceptChallenge(request, params)
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
		
		Challenge challenge = Challenge.get(params.challengeId.toLong())
		
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
		
		if (challenge.challengeStatus == ChallengeStatusEnum.ACCEPTED){
			Map result = [error:"cannot ignore a challenge that was already accepted"]
			render result as JSON
			return
		}
		
		if (challenge.challengeStatus == ChallengeStatusEnum.DECLINED){
			Map result = [:]
			render result as JSON
			return
		}
		
		Map result = challengeService.ignoreChallenge(challenge)
		
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
	
	def processChallenge(){
		
		List games = gameService.listPastGamesData(null, "all")
		List gameIds = []
		for (Map game: games){
			gameIds.add(game.gameId)
		}
		
		int challengeUpdatecounter = challengeService.processChallengeInit(gameIds)
		Map result = [challengeUpdatecounter:challengeUpdatecounter]
		render result as JSON
	}
	
	private Map validateAcceptChallenge(def request, def params){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!params.challengeId){
			response.status = 404
			Map result = [error: "input parameter challengeId is missing"]
			return result
		}
		
		return [:]
	}
	
	private Map validateCreateChallengeWithRandomPlayer(def request){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!request.JSON.gameId == null){
			response.status = 404
			Map result = [error: "input parameter gameId is missing"]
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
