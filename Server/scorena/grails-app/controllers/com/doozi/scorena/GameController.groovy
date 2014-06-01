package com.doozi.scorena

import grails.converters.JSON
import grails.web.JSONBuilder
import com.doozi.scorena.gamedata.manager.GameDataAdapter;

import com.doozi.scorena.sportsdata.*
// /v1/sports/soccer/premier/upcomingevents - current day's game information
//		get

// /v1/sports/soccer/premier/topevents - get feature games
//		get

// /v1/sports/soccer/premier/historyevents - last seven days

//response: team names, home team, away team, date (UTC?), away # people, home # people

//1) game list
//2) question list + preview question content
//3) question content + social friendlist (home friend request)
//3b social friend list from away side 

class GameController {
	
	def gameService
	def sportsDataService
	def userService
	def processEngineManagerService
	
	def upcomingEplSportsDb()
	{
		def upcomingGames = GameDataAdapter.get_gameDataAdapterInstance().getUpcomingEplMatches();
		render upcomingGames as JSON
	}
	
	def upcomingChampSportsDb()
	{
		def upcomingGames = viewService.getUpcomingChampMatches()
		render upcomingGames as JSON
	}
	
	def pastEplSportsDb()
	{
		def pastGames = viewService.getPastEplMatches()
		render pastGames as JSON
	}
	
	def pastChampSportsDb()
	{
		def pastGames = viewService.getPastChampMatches()
		render pastGames as JSON
	}

	def getUpcomingGames(){
		def upcomingGames
		if (params.userId){
			if (!userService.accountExists(params.userId)){
				response.status = 404
				def errorMap = [code: 102, error: "User Id does not exists"]
				render errorMap as JSON
				return
			}
			
			upcomingGames = gameService.listUpcomingGames(params.userId)
		}else{
			upcomingGames = gameService.listUpcomingGames()
		}

		render upcomingGames as JSON
	}
	
	def getPastGames(){
		def pastGames
		if (params.userId){
			if (!userService.accountExists(params.userId)){
				response.status = 404
				def errorMap = [code: 102, error: "User Id does not exists"]
				render errorMap as JSON
				return
			}
			
			pastGames = gameService.listPastGames(params.userId)
		}else{
			 pastGames = gameService.listPastGames()
		}

		render pastGames as JSON
	}
	
	def getGame(){
		println params.gameId
		if (params.gameId){
			def questions = gameService.getGame(params.gameId)
			render questions as JSON
		}
	}
	
	def getFeatureGames(){
		def featureGames
		if (params.userId){			
			if (!userService.accountExists(params.userId)){
				response.status = 404
				def errorMap = [code: 102, error: "User Id does not exists"]
				render errorMap as JSON
				return
			}
			
			featureGames = gameService.listFeatureGames(params.userId)
		}else{
			featureGames = gameService.listFeatureGames()
		}		
		render featureGames as JSON
	}
	
	

	def processGameTesting(){
		def result = processEngineManagerService.startProcessEngine()
		render result as JSON
	}

	def testGames(){
		def games = sportsDataService.getAllUpcomingGames()
		render games as JSON
	}
}
