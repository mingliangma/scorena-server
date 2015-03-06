package com.doozi.scorena.tournament

import java.util.List;

import com.doozi.scorena.transaction.LeagueTypeEnum;

import grails.converters.JSON


class TournamentController {
	def userService
	def tournamentService
	def helperService
	def sportsDataService
	
	
	def enrollTournament(){
		log.info "enrollTournament() with tournamentId=${params.tournamentId}"
		Map validateResult = validateEnrollTournament(request, params)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "validateTournamentIdAndToken(): validation = ${validateResult}"
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
		
		long tournamentId = params.tournamentId.toLong()
		//validate if user is already enrolled
		Enrollment userEnrollment = Enrollment.find("from Enrollment as e where e.account.userId=(:userId) and e.tournament.id=(:tournamentId)", 
			[userId: validation.objectId, tournamentId: tournamentId])
		
		if (userEnrollment){
			Map vResult = [error: "user already enrolled in the tournament"]
			render vResult as JSON
			return
		}
		
		def tournament = tournamentService.enrollTournament(validation.objectId, tournamentId)
		render tournament as JSON
	}
	
	def createTournament(){
		log.info "createTournament() with sessionToken=${request.JSON.sessionToken}, title=${request.JSON.title}, description=${request.JSON.description}, "+
		"startDate=${request.JSON.startDate}, expireDate=${request.JSON.expireDate}, subscribedLeagues=${request.JSON.subscribedLeagues}, invitingUserIds=${request.JSON.invitingUserIds}"
		
		Map validateResult = validateCreateTournament(request)
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
		
		List<LeagueTypeEnum> subscribedLeagues = constructLeagueTypeEnumList(request.JSON.subscribedLeagues)
		if (subscribedLeagues == null){
			response.status = 404
			def result = [error: "incorrect subscribed leagues"]
			render result as JSON
			return
		}
		
		def result = tournamentService.createTournament(validation.objectId, request.JSON.title, request.JSON.description, subscribedLeagues, request.JSON.startDate, 
			request.JSON.expireDate, request.JSON.invitingUserIds)
		render result as JSON
	}
	
	def searchTournament(){
		log.info "createTournament() with keyword=${params.keyword}"
		def result = tournamentService.searchTournament(params.search)
		render result as JSON
		return
	}
	
	def listTournamentEnrollment(){
		log.info "listTournamentEnrollment() with userId=${params.userId}"
		def result = tournamentService.listTournamentEnrollment(params.userId)
		render result as JSON
		return
	}
	
	def inviteToTournament(){
		log.info "inviteToTournament() with tournamentId=${params.tournamentId}"
		Map validateResult = validateInviteToTournament(request, params)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "inviteToTournament(): validation = ${validateResult}"
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
		
		def tournament = tournamentService.inviteToTournament(params.tournamentId.toLong(), request.JSON.invitingUserIds)
		render tournament as JSON
	}
	
	def getTournamentRanking(){
		log.info "getTournamentRanking() with tournamentId=${params.tournamentId}"
		Map validateResult = validateGetTournamentRanking(params)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "getTournamentRanking(): validation = ${validateResult}"
			return
		}
		
		def tournamentRanking = tournamentService.getTournamentRanking(params.tournamentId.toLong())
		render tournamentRanking as JSON
	}
	
	def listTournamentInvitation(){
		log.info "getTournamentRanking() with userId=${params.userId}"
		def tournamentInvitations = tournamentService.listTournamentInvitation(params.userId)
		render tournamentInvitations as JSON
	}
	
	def acceptTournamentInvitation(){
		log.info "acceptTournamentInvitation() with tournamentId=${params.tournamentId}"
		Map validateResult = validateTournamentIdAndToken(request, params)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "inviteToTournament(): validation = ${validateResult}"
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
		
		def tournamentInvitations = tournamentService.acceptTournamentInvitation(validation.objectId, params.tournamentId.toLong())
		if (tournamentInvitations){
			render tournamentInvitations as JSON
		}else{
			Map error = [error: "no invitation found for the tournamentId: ${params.tournamentId}"] 
			render error as JSON
		}
	}
	
	def ignoreTournamentInvitation(){
		log.info "ignoreTournamentInvitation() with tournamentId=${params.tournamentId}"
		Map validateResult = validateTournamentIdAndToken(request, params)
		if (validateResult != [:] && validateResult.error){
			response.status = 404
			render validateResult as JSON
			log.info "inviteToTournament(): validation = ${validateResult}"
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
		
		def tournamentInvitations = tournamentService.ignoreTournamentInvitation(validation.objectId, params.tournamentId.toLong())
		if (tournamentInvitations){
			render tournamentInvitations as JSON
		}else{
			Map error = [error: "no invitation found for the tournamentId: ${params.tournamentId}"]
			render error as JSON
		}
	}
	
	private Map validateEnrollTournament(def request, def params){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!params.tournamentId){
			response.status = 404
			Map result = [error: "input parameter tournamentId is missing"]
			return result
		}
		
		if (!params.tournamentId.isLong()){
			response.status = 404
			Map result = [error: "input parameter tournamentId cannot convert to long type"]
			return result
		}
		
		if (!Tournament.exists(params.tournamentId)){
			response.status = 404
			Map result = [error: "The tournament does not exist"]
			return result
		}
		
		return [:]
	}
	
	private Map validateTournamentIdAndToken(def request, def params){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!params.tournamentId){
			response.status = 404
			Map result = [error: "input parameter tournamentId is missing"]
			return result
		}
		
		if (!params.tournamentId.isLong()){
			response.status = 404
			Map result = [error: "input parameter tournamentId cannot convert to long type"]
			return result
		}	
		
		if (!Tournament.exists(params.tournamentId)){
			response.status = 404
			Map result = [error: "The tournament does not exist"]
			return result
		}
		
		return [:]
	}
	
	private Map validateGetTournamentRanking(def params){
		if (!params.tournamentId){
			response.status = 404
			Map result = [error: "input parameter tournamentId is missing"]
			return result
		}
		
		if (!params.tournamentId.isLong()){
			response.status = 404
			Map result = [error: "input parameter tournamentId is not an integer"]
			return result
		}
		
		if (!Tournament.exists(params.tournamentId)){
			response.status = 404
			Map result = [error: "The tournament does not exist"]
			return result
		}
		return [:]
	}
	
	private Map validateInviteToTournament(def request, def params){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!params.tournamentId){
			response.status = 404
			Map result = [error: "input parameter tournamentId is missing"]
			return result
		}
		
		if (!params.tournamentId.isLong()){
			response.status = 404
			Map result = [error: "input parameter tournamentId cannot convert to long type"]
			return result
		}
		
		if (request.JSON.invitingUserIds == null){
			response.status = 404
			Map result = [error: "input parameter invitingUserIds is missing"]
			return result
		}
		
		if (request.JSON.invitingUserIds.isEmpty()){
			response.status = 404
			Map result = [error: "input parameter invitingUserIds is empty"]
			return result
		}
		return [:]
	}
	
	private Map validateCreateTournament(def request){
		if (!request.JSON.sessionToken){
			response.status = 404
			Map result = [error: "input parameter sessionToken is missing"]
			return result
		}
		
		if (!request.JSON.title){
			response.status = 404
			Map result = [error: "input parameter title is missing"]
			return result
		}
		
		if (!request.JSON.startDate ){
			response.status = 404
			Map result = [error: "input parameter startDate is missing"]
			return result
		}
		
		if (!request.JSON.expireDate ){
			response.status = 404
			Map result = [error: "input parameter expireDate is missing"]
			return result
		}
		
		if (!request.JSON.subscribedLeagues){
			response.status = 404
			Map result = [error: "input parameter subscribedLeagues is missing"]
			return result
		}
		
		if (!request.JSON.invitingUserIds == null){
			response.status = 404
			Map result = [error: "input parameter invitingUserIds is missing"]
			return result
		}
		
		if (!helperService.dateValidator(request.JSON.startDate)){
			response.status = 404
			def result = [error: "invalid startDate format. Correct format is yyyy-MM-dd HH:mm:ss"]
			return result
		}
		
		if (!helperService.dateValidator(request.JSON.expireDate)){
			response.status = 404
			def result = [error: "invalid expireDate format. Correct format is yyyy-MM-dd HH:mm:ss"]
			return result
		}
		
		return [:]
	}
	
	private List<LeagueTypeEnum> constructLeagueTypeEnumList(def subscribedLeagues){
		
		List<LeagueTypeEnum> subscribedLeaguesResult = []
		for (String l:subscribedLeagues){
			LeagueTypeEnum league = sportsDataService.getLeagueEnumFromLeagueString(l)
			if (league == null){
				return null
			}else{
				subscribedLeaguesResult.add(league)
			}
		}
		return subscribedLeaguesResult
	}
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.info "${e.toString()}", e
	}
}
