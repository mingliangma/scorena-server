package com.doozi.scorena.ranking
import grails.converters.JSON
class RankingController {

	def ScoreRankingService

	// gets the user ranking based on the value and amount of parameters that are passed to the controller
	def getRank()
	{
		// returns user rank for all users -> if year, month and league parameter is null or empty
		if ( (params.month == null || params.month == "" ) &&( params.year == null || params.year == "") && (params.league == null ||params.league == "" ))
		{
			def rank = ScoreRankingService.getRanking()
			render rank as JSON
		}
		
		// returns user rank by month -> if month, year parameter are set and league parameter is null or empty
		if (params.month != null && params.month != "" &&  params.year != null &&  params.year != "" &&( params.league == null || params.league == "") )
		{
			def monthRank = ScoreRankingService.getRankingByMonth(params.month, params.year)
			render monthRank as JSON
		}
		
		// returns user rank by league  -> if league parameter is set month, year parameter are null or empty
		if ((params.month == null || params.month == "" ) &&( params.year == null || params.year == "") && params.league != null && params.league != "" )
		{
			def leagueRank = ScoreRankingService.getRankingByLeague(params.league)
			render leagueRank as JSON
		}
		
		// returns user rank by league and month -> if year, month and league parameter are
		if (params.month != null && params.month != "" &&  params.year != null &&  params.year != "" && params.league != null && params.league != "" )
		{
			def leagueRank = ScoreRankingService.getRankingByLeagueAndMonth(params.month, params.year, params.league)
			render leagueRank as JSON
		}
	}
}
