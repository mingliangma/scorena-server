package com.doozi.scorena.controllerservice
import com.doozi.scorena.sportsdata.*;
import grails.transaction.Transactional

@Transactional
class ViewService {

    def getUpcomingEplMatches() {
		def upcomingGames = UpcomingEplView.findAll()
		
		def upcomingGamesMap = [:]
		List upcomingGamesList = []
		for (UpcomingEplView game: upcomingGames){
			def eventKey = game.eventKey
			def upcomingGame = upcomingGamesMap.get(eventKey)
			
			if (!upcomingGame){
				def	gameInfo = [
						"league": "EPL",
						"id":eventKey,
						"type":"soccer",
						"date":game.startDateTime,
						(game.alignment):[
							"teamname":game.fullName,
						]
				]
				upcomingGamesMap.putAt(eventKey, gameInfo)
			}else{
			
				if (!upcomingGame.away){
					upcomingGame.away = ["teamname":game.fullName]
				}else{
					upcomingGame.home = ["teamname":game.fullName]
				}
				upcomingGamesList.add(upcomingGame)
				
			}
		}
		
		return upcomingGamesList
    }
	
	def getUpcomingChampMatches() {
		def upcomingGames = UpcomingChampView.findAll()
		return upcomingGames
	}
	
	def getPastEplMatches() {
		
		def pastGames = PastEplView.findAll()
		def pastGamesMap = [:]
		List pastGamesList = []
		for (PastEplView game: pastGames){
			def eventKey = game.eventKey
			def pastGame = pastGamesMap.get(eventKey)
			
			if (!pastGame){	
				def	gameInfo = [
						"league": "EPL",
						"id":eventKey,
						"type":"soccer",
						"date":game.startDateTime,
						(game.alignment):[
							"teamname":game.fullName,
							"score":game.score
						]
				]				
				pastGamesMap.putAt(eventKey, gameInfo)
			}else{
			
				if (!pastGame.away){
					pastGame.away = ["teamname":game.fullName, "score":game.score]
				}else{
					pastGame.home = ["teamname":game.fullName, "score":game.score]
				}
				pastGamesList.add(pastGame)
				
			}			
		}		
		
		return pastGamesList
	}
	
	def getPastChampMatches() {
		def pastGames = PastChampView.findAll()
		return pastGames
	}
	
	def getEplMatch(def eventKey){
		def games = AllEplView.findAllByEventKey(eventKey)
		println games.size()
		def gameInfo = []
		for (AllEplView game: games){
			if (gameInfo.empty){
				gameInfo = [
					"league": "EPL",
					"id":eventKey,
					"type":"soccer",
					"eventStatus":game.eventStatus,
					"date":game.startDateTime,
					(game.alignment):[
						"teamname":game.fullName,
						"score":game.score
					]
				]
			}else{
				if (!gameInfo.away){
					gameInfo.away = ["teamname":game.fullName, "score":game.score]
				}else{
					gameInfo.home = ["teamname":game.fullName, "score":game.score]
				}
				
			}
		}
		
		return gameInfo
	}
}
