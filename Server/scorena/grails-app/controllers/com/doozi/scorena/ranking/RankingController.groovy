package com.doozi.scorena.ranking
import grails.converters.JSON
import com.doozi.scorena.transaction.LeagueTypeEnum
class RankingController {

	def scoreRankingService
	def sportsDataService
	def profitRankingService
	// gets the user ranking based on the value and amount of parameters that are passed to the controller
	def getRank()
	{
		
		Map validationResult = validation(params.month, params.league)
		if (validationResult != [:]){
			response.status = 404
			render validationResult as JSON
			log.warn "getRank() validation error: validationResult = ${validationResult}"
			return
		}
		
		// returns user rank for all users -> if month and league parameter is null or empty
		if ( (params.month == null || params.month == "" ) && (params.league == null ||params.league == "" ))
		{
			def rank
			if (params.userId == null || params.userId == "")
				rank = scoreRankingService.getAllRanking()
			else
				rank = scoreRankingService.getAllRanking(params.userId)
			
			render rank as JSON
			return
		}
		
		// returns user rank by month -> if month parameter is set and league parameter is null or empty
		if (params.month != null && params.month != "" &&( params.league == null || params.league == "") )
		{
			def rank
			if (params.userId == null || params.userId == "")
				rank = scoreRankingService.getRankingByMonth(params.month)
			else
				rank = scoreRankingService.getRankingByMonth(params.userId, params.month)
			
			render rank as JSON
			return
		}
		
		// returns user rank by league  -> if league parameter is set, month is null or empty
		if ((params.month == null || params.month == "" ) && params.league != null && params.league != "" )
		{			
			def rank
			if (params.userId == null || params.userId == "")
				rank = scoreRankingService.getRankingByLeague(params.league)
			else
				rank = scoreRankingService.getRankingByLeague(params.userId, params.league)
			
			render rank as JSON
			return
		}
		
		// returns user rank by league and month ->  month and league parameter are
		if (params.month != null && params.month != "" && params.league != null && params.league != "" )
		{			
			def rank
			if (params.userId == null || params.userId == "")
				rank = scoreRankingService.getRankingByLeagueAndMonth(params.month, params.league)
			else
				rank = scoreRankingService.getRankingByLeagueAndMonth(params.userId, params.month, params.league)
			
			render rank as JSON
			return
		}
	}
	
	def getGameRanking(){
		if (params.gameId){
			def gameRanking
			if (params.userId == null)
				gameRanking = profitRankingService.getGameRanking(params.gameId)
			else
				gameRanking = profitRankingService.getGameRanking(params.gameId, params.userId)
			render gameRanking as JSON
		}else{
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "getGameRanking(): result = ${result}"
		}
	}
	
	def getFollowingGameRanking(){
		if (params.gameId && params.userId){
			def gameRanking
			gameRanking = profitRankingService.getFollowingGameRanking(params.gameId, params.userId)
			render gameRanking as JSON
		}else{
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "getGameRanking(): result = ${result}"
		}
	}
	
	private Map validation(String month, String league){
		println "month="+month
		
		if (month != null){
			if (!month.isInteger())
				return [code:400, error: "month is not an ingether. Eg. use 1 as January"]
			
			if(month.toInteger() < 1 || month.toInteger() > 12)
				return [code:400, error: "month is not within 1 to 12"]
		}
		if (league != null){
			if (!sportsDataService.isLeagueSupport(league))		
				return [code:400, error: "Unsupport league. Scorena supports "+LeagueTypeEnum.findAll()]
		}
		return [:]
	}
	
	def handleException(Exception e) {
		response.status = 500
		Map excpetionResponse = [error: e.toString()]
		render excpetionResponse as JSON
		log.error "${e.toString()}", e
	}
}
