package com.doozi.scorena.controllerservice
import com.doozi.scorena.sportsdata.*;
import com.doozi.scorena.LeagueTypeEnum;

import grails.transaction.Transactional

@Transactional
class SportsDataService {
	static String PREMIER_LEAGUE = "l.premierlea"
	static String CHAMP_LEAGUE = "l.uefa.org."
	static String BRAZIL_SERIES_A = "l.cbf.br.ser"
	static String CALCIO_SERIES_A = "l.lega-calci"
	static String LA_LIGA= "l.lfp.es.pri"
	static String MLS = "l.mlsnet.com"
	static String WORLD_CUP = "l.fifaworldc"
	
	static String PREEVENT = "pre-event"
	static String POSTEVENT = "post-event"
	static String INTERMISSION = "intermission"
	static String MIDEVENT = "mid-event"
	
	
	static int UPCOMING_DATE_RANGE = 7
	static int PAST_DATE_RANGE = 7
	
	def helperService
	def customGameService
		
	public String getLeagueNameFromEventKey(String eventKey){
		
		if (eventKey.startsWith(PREMIER_LEAGUE))
			return "Premier League"
		else if (eventKey.startsWith(CHAMP_LEAGUE)) 
			return "UEFA Champions League"
		else if (eventKey.startsWith(BRAZIL_SERIES_A))
			return "Brazil league Series A"
		else if (eventKey.startsWith(CALCIO_SERIES_A))
			return "Calcio Series A"
		else if (eventKey.startsWith(LA_LIGA))
			return "La Liga"
		else if (eventKey.startsWith(MLS))
			return "Major League Soccer"
		else if (eventKey.startsWith(WORLD_CUP))
			return "World Cup 2014"
		else if (eventKey.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return "Launch Party"
	}
	
	public String getLeagueCodeFromEventKey(String eventKey){
		if (eventKey.startsWith(PREMIER_LEAGUE))
			return LeagueTypeEnum.EPL
		else if (eventKey.startsWith(CHAMP_LEAGUE))
			return LeagueTypeEnum.CHAMP
		else if (eventKey.startsWith(BRAZIL_SERIES_A))
			return LeagueTypeEnum.CBF
		else if (eventKey.startsWith(CALCIO_SERIES_A))
			return LeagueTypeEnum.LEGA
		else if (eventKey.startsWith(LA_LIGA))
			return LeagueTypeEnum.LFP
		else if (eventKey.startsWith(MLS))
			return LeagueTypeEnum.MLS
		else if (eventKey.startsWith(WORLD_CUP))
			return LeagueTypeEnum.WORLDCUP
		else if (eventKey.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return customGameService.CUSTOM_EVENT_PREFIX
	}
	
	List listUpcomingGameIds(){
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def c = ScorenaAllGames.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
			order("startDateTime", "asc")
			projections {
				distinct "eventKey"
			}
		}
	}
	
	def getAllUpcomingGames(){
		def todayDate = new Date()		
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
//		def upcomingGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1])
		def c = ScorenaAllGames.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
		    order("startDateTime", "asc")
		}
		println "SportsDataService::getAllUpcomingGames(): upcoming game size: "+upcomingGames.size()
		
	
		
		def upcomingGamesMap = [:]
		List upcomingGamesList = []
		for (ScorenaAllGames game: upcomingGames){
			String eventKey = game.eventKey
			def upcomingGame = upcomingGamesMap.get(eventKey)
			def upcomingGameFullName = game.fullName.trim()
			
			String matchDateString = helperService.setUTCFormat(game.startDateTime)				
			def matchDate = helperService.parseDateFromString(matchDateString)				
			if (todayDate > matchDate){
				if (game.eventStatus == "pre-event"){
					println "ERROR: SportsDataService::getAllUpcomingGames(): gameStatus should not be pre-event!"
					println "gameEvent: "+ game.eventKey
					println "eventStatus: " +game.eventStatus
					println "score: " +game.score
					println "team: " +upcomingGameFullName
					println "===================================="
					continue
				}
			}
						
			if (eventKey.startsWith("l.fifaworldcup.com-2013")){
					println "ERROR: SportsDataService::getAllUpcomingGames(): incorrect EventKey! "
					println "------gameEvent: "+ game.eventKey
					println "------teamname: " +upcomingGameFullName
					println "------score: "+ game.score
					println "------gameStatus: "+game.eventStatus
					continue
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
							"teamname":upcomingGameFullName,
							"score":game.score,
							"teamLogoUrl": "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"
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
					upcomingGame.away = ["teamname":upcomingGameFullName, "score":game.score, "teamLogoUrl": "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"]
				}else{
					upcomingGame.home = ["teamname":upcomingGameFullName, "score":game.score, "teamLogoUrl": "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"]
				}
				upcomingGamesList.add(upcomingGame)
				
			}
		}		
		
		return upcomingGamesList
	}
	
	List getAllPastGames(){
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
//		def pastGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime>? and g.startDateTime<?and g.eventStatus='post-event'", [pastDate, todayDate+1])
		
		def c = ScorenaAllGames.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
		}
		
		println "SportsDataService::getAllPastGames(): past game size: "+pastGames.size()
		def pastGamesMap = [:]
		List pastGamesList = []
		for (ScorenaAllGames game: pastGames){
			
			def pastGameFullName = game.fullName.trim()
			
			if (game.eventStatus != "post-event"){
				println "SportsDataService::getAllPastGames():wrong event: "+ game.eventKey
				println "eventStatus: " +game.eventStatus
				println "score: " +game.score
				println "team: " +pastGameFullName
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
							"teamname":pastGameFullName,
							"score":game.score,
							"teamLogoUrl": "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"
						]
				]
				pastGamesMap.putAt(eventKey, gameInfo)
			}else{
			
				if (!pastGame.away){
					pastGame.away = ["teamname":pastGameFullName, "score":game.score, "teamLogoUrl": "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"]
				}else{
					pastGame.home = ["teamname":pastGameFullName, "score":game.score, "teamLogoUrl": "https://s3-us-west-2.amazonaws.com/teamlogo/world_cup/default.png"]
				}
				pastGamesList.add(pastGame)
				
			}
		}
		return pastGamesList
	}
	
	Map getGame(def eventKey){
		def games = ScorenaAllGames.findAllByEventKey(eventKey)
		
		def gameInfo = []
		for (ScorenaAllGames game: games){
			
			def gameFullName = game.fullName.trim()
			
			if (gameInfo.empty){
				gameInfo = [
					"leagueName": getLeagueNameFromEventKey(eventKey),
					"leagueCode": getLeagueCodeFromEventKey(eventKey),
					"gameId":eventKey,
					"type":"soccer",
					"gameStatus":game.eventStatus,
					"date":helperService.setUTCFormat(game.startDateTime),
					(game.alignment):[
						"teamname":gameFullName,
						"score":game.score
					]
				]
			}else{
				if (!gameInfo.away){
					gameInfo.away = ["teamname":gameFullName, "score":game.score]
				}else{
					gameInfo.home = ["teamname":gameFullName, "score":game.score]
				}
				
			}
		}
		
		return gameInfo
	}
}
