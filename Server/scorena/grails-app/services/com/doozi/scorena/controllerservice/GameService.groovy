package com.doozi.scorena.controllerservice

import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional

@Transactional
class GameService {
	def sportsDataService
	def questionService
	def betService
	def helperService
	def customGameService
	
	public static final String POSTEVENT = "post-event"
	public static final String PREEVENT = "pre-event"
	public static final String INTERMISSION = "intermission"
	public static final String MIDEVENT = "mid-event"
	
	List listUpcomingNonCustomGames(){
		List upcomingGames = sportsDataService.getAllUpcomingGames()
		List upcomingGamesResult=[]
		upcomingGamesResult.addAll(upcomingGames)
		return upcomingGamesResult
	}
	
	List listUpcomingGames(){
		List upcomingGames = sportsDataService.getAllUpcomingGames()
		List upcomingCustomGames = customGameService.getAllUpcomingGames()
		List upcomingGamesResult=[]		
		upcomingGamesResult.addAll(upcomingCustomGames)
		upcomingGamesResult.addAll(upcomingGames)
		return upcomingGamesResult
	}
	
	List listUpcomingGames(def userId){
				
		List upcomingGamesResult=listUpcomingGames()
		
		def playedGames = betService.listDistinctBetEventKeyByUserId(userId)
		
		for (def upcomingGame: upcomingGamesResult){
			def gameId = upcomingGame.gameId
			upcomingGame.placedBet = false
			for (def eventKey: playedGames){
				if (gameId == eventKey){
					upcomingGame.placedBet = true
				}
			}
		}
		return upcomingGamesResult
	}
	
	def listPastGames(){
		List pastGames = sportsDataService.getAllPastGames()	
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult=[]	
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		return pastGamesResult
	}
	
	def listPastGames(def userId){

		List pastGamesResult=listPastGames()
		
		def playedGames = betService.listDistinctBetEventKeyByUserId(userId)

		for (def pastGame: pastGamesResult){
			def gameId = pastGame.gameId
			if (pastGame.gameStatus != "post-event"){
				println "gameService::listPastGames():wrong event: "+ pastGame
			}
			pastGame.placedBet = false
			for (def eventKey: playedGames){
				if (gameId == eventKey){					
					pastGame.placedBet = true
				}
			}
		}
		return pastGamesResult
	}
	
	def listFeatureGames(userId){
		List featureGames = questionService.listFeatureQuestions(userId)
		return featureGames
	}
	
	def listFeatureGames(){
		List featureGames = questionService.listFeatureQuestions()
		return featureGames
	}
	
	def getGame(String gameId){
		
		if (gameId.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return customGameService.getGame(gameId)
		else	
			return sportsDataService.getGame(gameId)
		
	}	
	
	def listUpcomingGamesAndQuestions() {
		def today = new Date();
		def weekLater = today + 7;
		def upcomingGames = Game.findAllByStartDateBetween(today, weekLater)
		
		def all = upcomingGames.collect {Game game ->
			[	home: 	game.homeTeamNameFirst,
				away: game.awayTeamNameFirst,					
				date: game.startDate,
				league: game.league,
				question: game.question.collect{ Question q ->
					[
						content: q.questionContent.content,
						pick1: q.pick1,
						pick2: q.pick2						
						]
				}
			]
		  }
		return all
    }
}
