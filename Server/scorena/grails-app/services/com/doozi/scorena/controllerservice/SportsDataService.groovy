package com.doozi.scorena.controllerservice
import com.doozi.scorena.sportsdata.*;

import grails.transaction.Transactional

@Transactional
class SportsDataService {
	static String PREMIER_LEAGUE = "l.premierlea"
	static String CHAMP_LEAGUE = "l.uefa.org."
	static String BRAZIL_SERIES_A = "l.cbf.br.ser"
	static String CALCIO_SERIES_A = "l.lega-calci"
	static String LA_LIGA= "l.lfp.es.pri"
	static String MLS = "l.mlsnet.com"
	
	static String PREEVENT = "pre-event"
	static String POSTEVENT = "post-event"
	static String INTERMISSION = "intermission"
	static String MIDEVENT = "mid-event"
	
	
	static int UPCOMING_DATE_RANGE = 7
	static int PAST_DATE_RANGE = 7
	
	def helperService
	def customGameService
		
	private String getLeagueNameFromEventKey(String eventKey){
		
		if (eventKey.startsWith(PREMIER_LEAGUE))
			return "Premier League"
		else if (eventKey.startsWith(CHAMP_LEAGUE)) 
			return "UEFA Champions League"
		else if (eventKey.startsWith(BRAZIL_SERIES_A))
			return "Brazil league Serie A"
		else if (eventKey.startsWith(CALCIO_SERIES_A))
			return "Calcio Series A"
		else if (eventKey.startsWith(LA_LIGA))
			return "La Liga"
		else if (eventKey.startsWith(MLS))
			return "Major League Soccer"
		else if (eventKey.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return "Launch Party"
	}
	
	private String getLeagueCodeFromEventKey(String eventKey){
		if (eventKey.startsWith(PREMIER_LEAGUE))
			return PREMIER_LEAGUE
		else if (eventKey.startsWith(CHAMP_LEAGUE))
			return CHAMP_LEAGUE
		else if (eventKey.startsWith(BRAZIL_SERIES_A))
			return BRAZIL_SERIES_A
		else if (eventKey.startsWith(CALCIO_SERIES_A))
			return CALCIO_SERIES_A
		else if (eventKey.startsWith(LA_LIGA))
			return LA_LIGA
		else if (eventKey.startsWith(MLS))
			return MLS
		else if (eventKey.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return customGameService.CUSTOM_EVENT_PREFIX
	}
	

	
	def getAllUpcomingGames(){
		def todayDate = new Date()		
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def upcomingGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1], [cache: true])
		def upcomingGamesMap = [:]
		List upcomingGamesList = []
		for (ScorenaAllGames game: upcomingGames){
			String eventKey = game.eventKey
			def upcomingGame = upcomingGamesMap.get(eventKey)
			
			String matchDateString = helperService.setUTCFormat(game.startDateTime)				
			def matchDate = helperService.parseDateFromString(matchDateString)				
			if (todayDate > matchDate){
				if (game.eventStatus == "pre-event"){
					println "ERROR: SportsDataService::getAllUpcomingGames(): gameStatus should not be pre-event!"
					println "gameEvent: "+ game.eventKey
					println "score: "+ game.score
					continue
				}
			}
						
			
			
			if (!upcomingGame){
				def	gameInfo = [
						"leagueName": getLeagueNameFromEventKey(eventKey),
						"leagueCode": getLeagueCodeFromEventKey(eventKey),
						"gameId":eventKey,
						"type":"soccer",
						"gameStatus":game.eventStatus,
						"date": helperService.setUTCFormat(game.startDateTime) ,
						(game.alignment):[
							"teamname":game.fullName,
							"score":game.score
						]
				]
				upcomingGamesMap.putAt(eventKey, gameInfo)
			}else{
			
				if (upcomingGame.gameStatus != game.eventStatus){
					println "ERROR: SportsDataService::getAllUpcomingGames(): gameStatus does not match!"
					println "First set data: "+upcomingGame
					println "second set data: "+ game.eventStatus
				}
			
				if (!upcomingGame.away){
					upcomingGame.away = ["teamname":game.fullName, "score":game.score]
				}else{
					upcomingGame.home = ["teamname":game.fullName, "score":game.score]
				}
				upcomingGamesList.add(upcomingGame)
				
			}
		}		
		
		return upcomingGamesList
	}
	
	List getAllPastGames(){
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
		def pastGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime>? and g.startDateTime<?and g.eventStatus='post-event'", [pastDate, todayDate+1])
		def pastGamesMap = [:]
		List pastGamesList = []
		for (ScorenaAllGames game: pastGames){
			if (game.eventStatus != "post-event"){
				println "SportsDataService::getAllPastGames():wrong event: "+ game.eventKey
			}
			
			String eventKey = game.eventKey
			def pastGame = pastGamesMap.get(eventKey)
			
			if (!pastGame){
				def	gameInfo = [
						"leagueName": getLeagueNameFromEventKey(eventKey),
						"leagueCode": getLeagueCodeFromEventKey(eventKey),
						"gameId":eventKey,
						"type":"soccer",
						"gameStatus":game.eventStatus,
						"date":helperService.setUTCFormat(game.startDateTime),
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
	
	def getGame(def eventKey){
		def games = ScorenaAllGames.findAllByEventKey(eventKey,[cache: true])
		
		def gameInfo = []
		for (ScorenaAllGames game: games){
			if (gameInfo.empty){
				gameInfo = [
					"leagueName": getLeagueNameFromEventKey(eventKey),
					"leagueCode": getLeagueCodeFromEventKey(eventKey),
					"gameId":eventKey,
					"type":"soccer",
					"gameStatus":game.eventStatus,
					"date":helperService.setUTCFormat(game.startDateTime),
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
	
//    def getUpcomingEplMatches() {
//		def upcomingGames = UpcomingEplView.listOrderByStartDateTime()
//		
//		def upcomingGamesMap = [:]
//		List upcomingGamesList = []
//		for (UpcomingEplView game: upcomingGames){
//			def eventKey = game.eventKey
//			def upcomingGame = upcomingGamesMap.get(eventKey)
//			
//			if (!upcomingGame){
//				def	gameInfo = [
//						"league": "EPL",
//						"gameId":eventKey,
//						"type":"soccer",
//						"gameStatus":game.eventStatus,
//						"date":game.startDateTime,
//						(game.alignment):[
//							"teamname":game.fullName,
//							"score":game.score
//						]
//				]
//				upcomingGamesMap.putAt(eventKey, gameInfo)
//			}else{
//			
//				if (!upcomingGame.away){
//					upcomingGame.away = ["teamname":game.fullName, "score":game.score]
//				}else{
//					upcomingGame.home = ["teamname":game.fullName, "score":game.score]
//				}
//				upcomingGamesList.add(upcomingGame)
//				
//			}
//		}
//		
//		return upcomingGamesList
//    }
//	
//	def getUpcomingChampMatches() {
//		def upcomingGames = UpcomingChampView.findAll(sort:"startDateTime")
//		return upcomingGames
//	}
	
	def getPastEplMatches() {
		
		def pastGames = PastEplView.listOrderByStartDateTime()
		def pastGamesMap = [:]
		List pastGamesList = []
		for (PastEplView game: pastGames){
			def eventKey = game.eventKey
			def pastGame = pastGamesMap.get(eventKey)
			
			if (!pastGame){	
				def	gameInfo = [
						"league": "EPL",
						"gameId":eventKey,
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
	

}
