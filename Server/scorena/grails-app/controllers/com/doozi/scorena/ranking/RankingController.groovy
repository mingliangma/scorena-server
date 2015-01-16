package com.doozi.scorena.ranking
import grails.converters.JSON
class RankingController {

	def ScoreRankingService

	// gets the user ranking based on the value and amount of parameters that are passed to the controller
	def getRank()
	{
		// returns user rank for all users -> if month and league parameter is null or empty
		if ( (params.month == null || params.month == "" ) && (params.league == null ||params.league == "" ))
		{
			def rank = ScoreRankingService.getRanking()
			render rank as JSON
		}
		
		// returns user rank by month -> if month parameter is set and league parameter is null or empty
		if (params.month != null && params.month != "" &&( params.league == null || params.league == "") )
		{
			def monthRank = ScoreRankingService.getRankingByMonth(params.month)
			render monthRank as JSON
		}
		
		// returns user rank by league  -> if league parameter is set, month is null or empty
		if ((params.month == null || params.month == "" ) && params.league != null && params.league != "" )
		{
			def leagueRank = ScoreRankingService.getRankingByLeague(params.league)
			render leagueRank as JSON
		}
		
		// returns user rank by league and month ->  month and league parameter are
		if (params.month != null && params.month != "" && params.league != null && params.league != "" )
		{
			def leagueRank = ScoreRankingService.getRankingByLeagueAndMonth(params.month, params.league)
			render leagueRank as JSON
		}
	}
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.info "${e.toString()}"
	}
}
