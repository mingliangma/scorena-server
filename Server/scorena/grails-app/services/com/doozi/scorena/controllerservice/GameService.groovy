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
	
	def listUpcomingGames(){
		List upcomingGames = sportsDataService.getAllUpcomingGames()
		List upcomingCustomGames = customGameService.getAllUpcomingGames()
		List upcomingGamesResult=[]		
		upcomingGamesResult.addAll(upcomingCustomGames)
		upcomingGamesResult.addAll(upcomingGames)
		return upcomingGamesResult
	}
	
	def listUpcomingGames(def userId){
		List upcomingGames = sportsDataService.getAllUpcomingGames()	
		List upcomingCustomGames = 	customGameService.getAllUpcomingGames()		
		List upcomingGamesResult=[]			
		upcomingGamesResult.addAll(upcomingCustomGames)
		upcomingGamesResult.addAll(upcomingGames)
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
	
	//deprecated
//	def getUpcomingGameObjects(){
//		def today = new Date();
//		def weekLater = today + 7;
//		def upcomingGames = Game.findAllByStartDateBetween(today, weekLater)
//		return upcomingGames
//	}
	
	def listPastGames(){
		List pastGames = sportsDataService.getAllPastGames()	
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult=[]	
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		return pastGamesResult
	}
	
	def listPastGames(def userId){
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult=[]	
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
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
	
//	def listPastGames() {
//		def today = new Date();
//		def upcomingGames = Game.findAllByStartDateLessThan(today)
//		
//		List resultList = []
//		
//		
//		for (Game g: upcomingGames){
//			def hScore
//			def aScore
//			if (g.id%2 == 0){				
//				hScore= "1"
//				aScore= "3"			
//			}else{
//				hScore= "2"
//				aScore= "1"
//			}
//			
//
//			
//			resultList.add([
//				id: g.id,
//				home: 	g.homeTeamNameFirst,
//				away: g.awayTeamNameFirst,
//				date: g.startDate,
//				league: g.league,
//				type: g.type,
//				homeScore: hScore,
//				awayScore: aScore
//			])
//		}
//		
//		
//		return resultList
//	}
	
	def getGame(String gameId){
		
		if (gameId.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return customGameService.getGame(gameId)
		else	
			return sportsDataService.getGame(gameId)
		
	}
	
//	def getGame(gameId){
//		def match = Game.findById(gameId)
//		def gameEvent
//		if (match.gameEvent){
//			gameEvent = [
//						homeScore: match.gameEvent.homeScore,
//						awayScore: match.gameEvent.awayScore
//						]
//		}else{
//			gameEvent=[]
//		}
//		
//		def all = match.collect {Game game ->
//			[	id: game.id,
//				home: 	game.homeTeamNameFirst,
//				away: game.awayTeamNameFirst,
//				date: game.startDate,
//				league: game.league,
//				type: game.type,
//				event: gameEvent
//			]
//		  }
//		
//		return all
//	}
	
	
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
