package com.doozi.scorena.controllerservice

import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional

@Transactional
class GameService {
	def viewService
	def questionService
	
	def listUpcomingGames(){
		def upcomingGames = viewService.getUpcomingEplMatches()
		return upcomingGames
	}
	
//	def listUpcomingGames() {
//		def upcomingGames = getUpcomingGameObjects()		
//		def all = upcomingGames.collect {Game game ->
//			[	id: game.id,
//				home: 	game.homeTeamNameFirst,
//				away: game.awayTeamNameFirst,
//				date: game.startDate,
//				league: game.league,
//				type: game.type,			
//			]
//		  }
//		return all
//	}
	
	def getUpcomingGameObjects(){
		def today = new Date();
		def weekLater = today + 7;
		def upcomingGames = Game.findAllByStartDateBetween(today, weekLater)
		return upcomingGames
	}
	
	def listPastGames(){
		def pastGames = viewService.getPastEplMatches()		
		return pastGames
	}
	
	def listFeatureGames(userId){
		def upcomingGames = viewService.getUpcomingEplMatches()
		List featureGames =[]
		for (int i = 0; i<3; i++){
			def game = upcomingGames.get(i)
			def questions = questionService.listQuestions(game.gameId, userId)
			def question = questions.get(0)
			game.question = question
			featureGames.add(game)
			
		}
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
	
	def getGame2(gameId){
		def game = viewService.getEplMatch(gameId)
		return game
	}
	
	def getGame(gameId){
		def match = Game.findById(gameId)
		def gameEvent
		if (match.gameEvent){
			gameEvent = [
						homeScore: match.gameEvent.homeScore,
						awayScore: match.gameEvent.awayScore
						]
		}else{
			gameEvent=[]
		}
		
		def all = match.collect {Game game ->
			[	id: game.id,
				home: 	game.homeTeamNameFirst,
				away: game.awayTeamNameFirst,
				date: game.startDate,
				league: game.league,
				type: game.type,
				event: gameEvent
			]
		  }
		
		return all
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
						content: q.content,
						pick1: q.pick1,
						pick2: q.pick2						
						]
				}
			]
		  }
		return all
    }
}
