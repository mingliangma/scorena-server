package com.doozi.scorena.controllerservice

import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional

@Transactional
class GameService {
	def sportsDataService
	def questionService
	def betService
	
	public static final String POSTEVENT = "post-event"
	def listUpcomingGames(){
		def upcomingGames = sportsDataService.getAllUpcomingGames()
		return upcomingGames
	}
	
	def listUpcomingGames(def userId){
		List upcomingGames = sportsDataService.getAllUpcomingGames()		
		def playedGames = betService.listDistinctBetEventKeyByUserId(userId)
		
		for (def upcomingGame: upcomingGames){
			def gameId = upcomingGame.gameId
			upcomingGame.placedBet = false
			for (def eventKey: playedGames){
				if (gameId == eventKey){
					println "true"
					upcomingGame.placedBet = true
				}
			}
		}
		return upcomingGames
	}
	
	//deprecated
	def getUpcomingGameObjects(){
		def today = new Date();
		def weekLater = today + 7;
		def upcomingGames = Game.findAllByStartDateBetween(today, weekLater)
		return upcomingGames
	}
	
	def listPastGames(){
		def pastGames = sportsDataService.getAllPastGames()		
		return pastGames
	}
	
	def listPastGames(def userId){
		List pastGames = sportsDataService.getAllPastGames()
		def playedGames = betService.listDistinctBetEventKeyByUserId(userId)

		for (def pastGame: pastGames){
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
		return pastGames
	}
	
	def listFeatureGames(userId){
		List featureGames = questionService.listFeatureQuestions(userId)
		
//		def upcomingGames = sportsDataService.getAllUpcomingGames()
//		List featureGames =[]
//		for (int i = 0; i<3; i++){
//			def game = upcomingGames.get(i)
//			def questions = questionService.listQuestionsWithPoolInfo(game.gameId, userId)
//			if (questions.size()!=0){
//				def question = questions.get(i)
//				game.question = question
//				featureGames.add(game)
//			}
//			
//		}
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
	
	def getGame(gameId){
		def game = sportsDataService.getGame(gameId)
		return game
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
