package com.doozi.scorena.tournament

import grails.converters.JSON


class TournamentController {
	def userService
	def tournamentService
	def helperService
	//tournamentStatus=0 not started
	//tournamentStatus=1 active
	//tournamentStatus=2 expire
	
	//enrollmentStatus: 0 not enrolled
	//enrollmentStatus: 1 enrolled
    def listTournaments() { 
		List tournament = []

//		if (params.tstatus)
//			tournamentStatus = params.tstatus.toInteger()
//		
//		if (params.estatus)
//			enrollmentStatus = params.estatus.toInteger()
		
		if (params.userId){			
			if (!userService.accountExists(params.userId)){
				response.status = 404
				def errorMap = [code: 102, error: "User Id does not exists"]
				render errorMap as JSON
				return
			}

			
			tournament = tournamentService.listTournaments(params.userId)
			
		}else{
			tournament = tournamentService.listTournaments()
		
		}

		render tournament as JSON
	}
	
	def getWorldCupTournament(){
		Map tournament = [:]
		println "tournament starts..."
		
//		
//			if (params.tstatus)
//				tournamentStatus = params.tstatus.toInteger()
//			
//			if (params.estatus)
//				enrollmentStatus = params.estatus.toInteger()
			
			if (params.userId){
				if (!userService.accountExists(params.userId)){
					response.status = 404
					def errorMap = [code: 102, error: "User Id does not exists"]
					render errorMap as JSON
					return
				}
	
				
				tournament = tournamentService.getWorldCupTournament(params.userId)
				
			}else{
				tournament = tournamentService.getWorldCupTournament()
			
			}
			println "tournament: "+tournament
			render tournament as JSON
			return
	}
	
	def enrollTournament(){
		if (!params.userId || params.userId==null || params.userId==""){
			response.status = 404
			def result = [error: "userId is required"]
			render result as JSON
			return
		}
		
		if (!params.tournamentId){
			response.status = 404
			def result = [error: "tournamentId is required"]
			render result as JSON
			return
		}
		
		Map rankingResult = tournamentService.joinTournament(params.userId, params.tournamentId)
		
		
		if (rankingResult.error){
			response.status = 404
			render rankingResult as JSON
			return
		}

//		Map tourRank = [rank: rankingResult.weekly] 
		render rankingResult as JSON
	}
	
	def createTournament(){
		println "createTournament"
		
		
		if ( !request.JSON.title || !request.JSON.content || !request.JSON.type ||!request.JSON.prize || !request.JSON.startDate || !request.JSON.expireDate){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		if (!request.JSON.password || request.JSON.password!="mz8785"){
			response.status = 404
			def result = [error: "password is incorrect"]
			render result as JSON
			return
		}
		
		if (!helperService.dateValidator(request.JSON.startDate)){
			response.status = 404
			def result = [error: "invalid startDate format. Correct format is yyyy-MM-dd HH:mm:ss"]
			render result as JSON
			return
		}
		
		if (!helperService.dateValidator(request.JSON.expireDate)){
			response.status = 404
			def result = [error: "invalid expireDate format. Correct format is yyyy-MM-dd HH:mm:ss"]
			render result as JSON
			return
		}
		
		
		Map result = tournamentService.createTournament(request.JSON.title, request.JSON.content, request.JSON.type, request.JSON.prize, request.JSON.startDate, request.JSON.expireDate)
		if (result.error){
			response.status = 404
			render result as JSON
			return
		}
		render result as JSON
	}
	
	def handleException(Exception e) {
		render e.toString()
		log.info "${e.toString()}"
	}
}
