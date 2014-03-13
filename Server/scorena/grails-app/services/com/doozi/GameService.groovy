package com.doozi

import grails.transaction.Transactional

@Transactional
class GameService {

	def listUpcomingGames() {
		def today = new Date();
		def weekLater = today + 7;
		def upcomingGames = Game.findAllByDateBetween(today, weekLater)
		
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
