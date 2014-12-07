package com.doozi.scorena.controllerservice
import com.doozi.scorena.sportsdata.*;
import com.doozi.scorena.transaction.LeagueTypeEnum;

import grails.transaction.Transactional

@Transactional
class SportsDataService {
	final static String PREMIER_LEAGUE = "l.premierlea"
	final static String CHAMP_LEAGUE = "l.uefa.org."
	final static String BRAZIL_SERIES_A = "l.cbf.br.ser"
	final static String CALCIO_SERIES_A = "l.lega-calci"
	final static String LA_LIGA= "l.lfp.es.pri"
	final static String MLS = "l.mlsnet.com"
	final static String WORLD_CUP = "l.fifaworldc"
	final static String NBA = "nba"
	
	final static int PREEVENT = 0
	final static int POSTEVENT = 3
	final static int INTERMISSION = 2
	final static int MIDEVENT = 1
	final static int ALLEVENT = 4
	
	final static String PREEVENT_NAME = "pre-event"
	final static String POSTEVENT_NAME = "post-event"
	final static String INTERMISSION_NAME = "intermission"
	final static String MIDEVENT_NAME = "mid-event"
	
	
	static int UPCOMING_DATE_RANGE = 7
	static int PAST_DATE_RANGE = 7
	
	def helperService
	def customGameService
	def teamLogoService
		
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
		else if (eventKey.startsWith(NBA))
			return "NBA"
		else if (eventKey.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return "Launch Party"
	}
	
	public LeagueTypeEnum getLeagueCodeFromEventKey(String eventKey){
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
		else if (eventKey.startsWith(NBA))
			return LeagueTypeEnum.NBA
		else if (eventKey.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return customGameService.CUSTOM_EVENT_PREFIX
	}
	
	public LeagueTypeEnum getLeagueEnumFromLeagueString(String leagueType){
		if (leagueType.toUpperCase() == LeagueTypeEnum.EPL.toString())
			return LeagueTypeEnum.EPL
		else if (leagueType.toUpperCase() == LeagueTypeEnum.CHAMP.toString())
			return LeagueTypeEnum.CHAMP
		else if (leagueType.toUpperCase() == LeagueTypeEnum.CBF.toString())
			return LeagueTypeEnum.CBF
		else if (leagueType.toUpperCase() == LeagueTypeEnum.LEGA.toString())
			return LeagueTypeEnum.LEGA
		else if (leagueType.toUpperCase() == LeagueTypeEnum.LFP.toString())
			return LeagueTypeEnum.LFP
		else if (leagueType.toUpperCase() == LeagueTypeEnum.MLS.toString())
			return LeagueTypeEnum.MLS
		else if (leagueType.toUpperCase() == LeagueTypeEnum.WORLDCUP.toString())
			return LeagueTypeEnum.WORLDCUP
		else if (leagueType.toUpperCase() == LeagueTypeEnum.NBA.toString())
			return LeagueTypeEnum.NBA
	}
	
	public String getLeaguePrefixFromLeagueEnum(LeagueTypeEnum leagueTypeEnum){
		if (leagueTypeEnum == LeagueTypeEnum.EPL)
			return PREMIER_LEAGUE
		else if (leagueTypeEnum == LeagueTypeEnum.CHAMP)
			return CHAMP_LEAGUE
		else if (leagueTypeEnum== LeagueTypeEnum.CBF)
			return BRAZIL_SERIES_A
		else if (leagueTypeEnum == LeagueTypeEnum.LEGA)
			return CALCIO_SERIES_A
		else if (leagueTypeEnum == LeagueTypeEnum.LFP)
			return LA_LIGA
		else if (leagueTypeEnum == LeagueTypeEnum.MLS)
			return MLS
		else if (leagueTypeEnum == LeagueTypeEnum.WORLDCUP)
			return WORLD_CUP
		else if (leagueTypeEnum == LeagueTypeEnum.NBA)
			return NBA
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
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		return upcomingGamesList
	}
	
	def getAllUpcomingGames(String leagueType){
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
//		def upcomingGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1])
		def c = ScorenaAllGames.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
			order("startDateTime", "asc")
			ilike("eventKey", getLeaguePrefixFromLeagueEnum(getLeagueEnumFromLeagueString(leagueType))+"%")
		}
		println "SportsDataService::getAllUpcomingGames(): upcoming game size: "+upcomingGames.size()
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
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

		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		return pastGamesList
	}
	
	List getAllPastGames(String leagueType){
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
		
		def c = ScorenaAllGames.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
			ilike("eventKey", getLeaguePrefixFromLeagueEnum(getLeagueEnumFromLeagueString(leagueType))+"%")
		}
		
		println "SportsDataService::getAllPastGames(): past game size: "+pastGames.size()

		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		return pastGamesList
	}
	
	Map getGame(def eventKey){
		def games = ScorenaAllGames.findAllByEventKey(eventKey)
		def todayDate = new Date()
		
		def gameInfo = constructGameList(games, ALLEVENT, todayDate)[0]
	
		return gameInfo
	}
	
	/**
	 * @param games
	 * @param eventType: preevent if eventType=0,  midevent if eventType=1, postevent if eventType=2
	 * @return
	 */
	List constructGameList(def games, int eventType, def todayDate){
		
		def gamesMap = [:]
		List gamesList = []
		for (def game: games){
			String eventKey = game.eventKey
			def gamesMapValue = gamesMap.get(eventKey)
			def gameFullName = game.fullName.trim()
			
			
			String matchDateString = helperService.setUTCFormat(game.startDateTime)
			def matchDate = helperService.parseDateFromString(matchDateString)
			
			if (eventType == PREEVENT || eventType == MIDEVENT || eventType == INTERMISSION){
				if (todayDate > matchDate){
					if (game.eventStatus == "pre-event"){
						println "ERROR: SportsDataService::getAllUpcomingGames(): gameStatus should not be pre-event!"
						println "gameEvent: "+ game.eventKey
						println "eventStatus: " +game.eventStatus
						println "score: " +game.score
						println "team: " +gameFullName
						println "===================================="
						continue
					}
				}
			}
			if (eventType == POSTEVENT){
				if (game.eventStatus != "post-event"){
					println "SportsDataService::getAllPastGames():wrong event: "+ game.eventKey
					println "eventStatus: " +game.eventStatus
					println "score: " +game.score
					println "team: " +pastGameFullName
				}
			}
			if (!gamesMapValue){
				def	gameInfo = [
						"leagueName": getLeagueNameFromEventKey(eventKey),
						"leagueCode": getLeagueCodeFromEventKey(eventKey),
						"gameId":eventKey,
						"type":"soccer",
						"gameStatus":game.eventStatus,
						"date": helperService.setUTCFormat(game.startDateTime),
						(game.alignment):[
							"teamname":gameFullName,
							"score":game.score,
							"teamLogoUrl": teamLogoService.getTeamLogo(gameFullName)
						]
				]
				gamesMap.putAt(eventKey, gameInfo)
			}else{
			
				if (gamesMapValue.gameStatus != game.eventStatus){
					println "ERROR: SportsDataService::getAllUpcomingGames(): gameStatus does not match!"
					println "First set data: "+gamesMapValue
					println "second set data: "+ game.eventStatus
				}
			
				if (!gamesMapValue.away){
					gamesMapValue.away = ["teamname":gameFullName, "score":game.score, "teamLogoUrl": teamLogoService.getTeamLogo(gameFullName)]
				}else{
					gamesMapValue.home = ["teamname":gameFullName, "score":game.score, "teamLogoUrl": teamLogoService.getTeamLogo(gameFullName)]
				}
				gamesList.add(gamesMapValue)
				
			}
		}
		
		return gamesList
		
	}
}
