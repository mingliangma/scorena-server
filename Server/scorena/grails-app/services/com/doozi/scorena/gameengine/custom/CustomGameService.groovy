package com.doozi.scorena.gameengine.custom

import com.doozi.scorena.CustomGame
import grails.transaction.Transactional

@Transactional
class CustomGameService {
	public static final String CUSTOM_TEAM_PREFIX = "customteam-"
	public static final String CUSTOM_EVENT_PREFIX = "customevent-"
	public static final String ALIGNMENT_AWAY = "away"
	public static final String ALIGNMENT_HOME = "home"
	static int UPCOMING_DATE_RANGE = 7
	static int PAST_DATE_RANGE = 7
	
	def helperService
	def gameService
	
	def getGame(def eventKey){
		def games = CustomGame.findAllByEventKey(eventKey,[cache: true])
		
		def gameInfo = []
		for (CustomGame game: games){
			if (gameInfo.empty){
				gameInfo = [
					"leagueName": "",
					"leagueCode": "",
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
	
	def getAllUpcomingGames(){
		
		println "CustomGameService::getAllUpcomingGames(): starts..."
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def upcomingGames = CustomGame.findAll("from CustomGame as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1], [cache: true])
		def upcomingGamesMap = [:]
		List upcomingGamesList = []
		for (CustomGame game: upcomingGames){
			String eventKey = game.eventKey
			def upcomingGame = upcomingGamesMap.get(eventKey)
						
			String matchDateString = helperService.setUTCFormat(game.startDateTime)
			def matchDate = helperService.parseDateFromString(matchDateString)
			if (todayDate > matchDate){
				if (game.eventStatus == "pre-event"){
					println "ERROR: SportsDataService::getAllUpcomingGames(): gameStatus should not be pre-event!"
					println "gameEvent: "+ game.eventKey
					println "score: "+ game.score
				}
			}
						
			
			if (!upcomingGame){
				def	gameInfo = [
						"leagueName": "",
						"leagueCode": "",
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
	
    def createCustomGameByName(String awayTeamName, String homeTeamName, String eventName, String startDateTimeInput) {
		Random random = new Random()
		
		String awayTeamKey = CUSTOM_TEAM_PREFIX+(random.nextInt(1000000)).toString()
		String homeTeamKey = CUSTOM_TEAM_PREFIX+(random.nextInt(1000000)).toString()
		
		println homeTeamKey
		String eventKey = ""
		if (eventName == null || eventName==""){
			eventKey = CUSTOM_EVENT_PREFIX+(random.nextInt(1000000)).toString()
		}else{
			eventKey = CUSTOM_EVENT_PREFIX+eventName+"-"+(random.nextInt(1000000)).toString()
		}
		
		Date startDateTime = helperService.parseDateFromString(startDateTimeInput)
		Date lastUpdate = new Date()
		
		def cgHome = new CustomGame(fullName: homeTeamName, teamKey: homeTeamKey, eventStatus: gameService.PREEVENT, eventKey:eventKey, 
			alignment: ALIGNMENT_HOME, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null)
		
		def cgAway = new CustomGame(fullName: awayTeamName, teamKey: awayTeamKey, eventStatus: gameService.PREEVENT, eventKey:eventKey,
			alignment: ALIGNMENT_AWAY, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null)
		
		if (cgHome.save() && cgAway.save()){
			System.out.println("Custom Game Created successfully saved")
			return [awayTeamName:awayTeamName, awayTeamKey:awayTeamKey, homeTeamName:homeTeamName, homeTeamKey:homeTeamKey, eventId:eventKey, 
				eventStatus:gameService.PREEVENT, startDateTime:helperService.setUTCFormat(startDateTime)]
		}else{
			System.out.println("game save failed")
			cgHome.errors.each{
				println it
			}
			
			cgAway.errors.each{
				println it
			}
			return [error: [cgHome:cgHome.errors, cgAway:cgAway.errors]]
		}
		
    }
	
//	def createCustomGameByKey(String awayTeamKey, String homeTeamKey, String eventKey, String startDateTime){
//		
//	}
}
