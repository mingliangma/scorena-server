package com.doozi

import grails.transaction.Transactional

@Transactional
class GameService {

	def listUpcomingGames() {
		def upcomingGames = getUpcomingGameObjects()		
		def all = upcomingGames.collect {Game game ->
			[	id: game.id,
				home: 	game.home,
				away: game.away,
				date: game.date,
				league: game.league,
				type: game.type,			
			]
		  }
		return all
	}
	
	def getUpcomingGameObjects(){
		def today = new Date();
		def weekLater = today + 7;
		def upcomingGames = Game.findAllByDateBetween(today, weekLater)
		return upcomingGames
	}
	
	def listPastGames() {
		def today = new Date();
		def upcomingGames = Game.findAllByDateLessThan(today)
		
		def all = upcomingGames.collect {Game game ->
			[	id: game.id,
				home: 	game.home,
				away: game.away,
				date: game.date,
				league: game.league,
				type: game.type,
			]
		  }
		return all
	}
	
	def getGame(gameId){
		def match = Game.findAllById(gameId)
		
		def all = match.collect {Game game ->
			[	id: game.id,
				home: 	game.home,
				away: game.away,
				date: game.date,
				league: game.league,
				type: game.type,
				event: [
					homeScore: game.gameEvent.homeScore,
					awayScore: game.gameEvent.awayScore
					]
				
			]
		  }
		
		return all
	}
	
	
	def listUpcomingGamesAndQuestions() {
		def today = new Date();
		def weekLater = today + 7;
		def upcomingGames = Game.findAllByDateBetween(today, weekLater)
		
		def all = upcomingGames.collect {Game game ->
			[	home: 	game.home,
				away: game.away,					
				date: game.date,
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
