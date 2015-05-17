package com.doozi.scorena.gamedata.dboutput
import com.doozi.scorena.sportsdata.*;
import com.doozi.scorena.transaction.LeagueTypeEnum;

import org.springframework.transaction.annotation.Transactional


class SportsDataService {
	final static String PREMIER_LEAGUE = "l.premierlea"
	final static String CHAMP_LEAGUE = "l.uefa.org."
	final static String BRAZIL_SERIES_A = "l.cbf.br.ser"
	final static String CALCIO_SERIES_A = "l.lega-calci"
	final static String LA_LIGA= "l.lfp.es.pri"
	final static String MLS = "l.mlsnet.com"
	final static String WORLD_CUP = "l.fifaworldc"
	final static String NBA = "nba"
	final static String MLB = "mlb"
	final static String FRENCHOPEN = "frenchopen"
	final static String NBADRAFT = "nbadraft"
	
	final static int PREEVENT = 0
	final static int POSTEVENT = 3
	final static int INTERMISSION = 2
	final static int MIDEVENT = 1
	final static int ALLEVENT = 4
	
	final static String PREEVENT_NAME = "pre-event"
	final static String POSTEVENT_NAME = "post-event"
	final static String INTERMISSION_NAME = "intermission"
	final static String MIDEVENT_NAME = "mid-event"
	
	
	static int UPCOMING_DATE_RANGE = 14
	static int PAST_DATE_RANGE = 14
	
	def helperService
	def customGameService
	def teamLogoService
	static datasource = 'sportsData'
		
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
		else if (eventKey.startsWith(NBADRAFT))
			return "NBA Draft"
		else if (eventKey.startsWith(NBA))
			return "NBA"
		else if (eventKey.startsWith(MLB))
			return "MLB"
		else if (eventKey.startsWith(FRENCHOPEN))
			return "French Open"
		else if (eventKey.startsWith(NBADRAFT))
			return "NBA Draft"
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
		else if (eventKey.startsWith(NBADRAFT))
			return LeagueTypeEnum.NBADRAFT
		else if (eventKey.startsWith(NBA))
			return LeagueTypeEnum.NBA
		else if (eventKey.startsWith(MLB))
			return LeagueTypeEnum.MLB
		else if (eventKey.startsWith(FRENCHOPEN))
			return LeagueTypeEnum.FRENCHOPEN		
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
		else if (leagueType.toUpperCase() == LeagueTypeEnum.MLB.toString())
			return LeagueTypeEnum.MLB
		else if (leagueType.toUpperCase() == LeagueTypeEnum.FRENCHOPEN.toString())
			return LeagueTypeEnum.FRENCHOPEN
		else if (leagueType.toUpperCase() == LeagueTypeEnum.NBADRAFT.toString())
			return LeagueTypeEnum.NBADRAFT
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
		else if (leagueTypeEnum == LeagueTypeEnum.MLB)
			return MLB
		else if (leagueTypeEnum == LeagueTypeEnum.FRENCHOPEN)
			return FRENCHOPEN
		else if (leagueTypeEnum == LeagueTypeEnum.NBADRAFT)
			return NBADRAFT
	}
	
	public isLeagueSupport(String league){
		for (LeagueTypeEnum c : LeagueTypeEnum.values()) {
			if (c.name().equalsIgnoreCase(league)) {
				return true;
			}
		}
	
		return false;
	}
	
	List listUpcomingGameIds(){
		log.debug "listUpcomingGameIds(): begins..."
		
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
		
		log.debug "listUpcomingGameIds(): ends"
	}
	
	def getAllUpcomingGames(){
		log.debug "getAllUpcomingGames(): begins..."
		
		def todayDate = new Date()		
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
//		def upcomingGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1])
		def c = ScorenaAllGames.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
		    order("startDateTime", "asc")
		}
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		
		log.debug "getAllUpcomingGames(): ends"
		
		return upcomingGamesList
	}
	
	def getAllUpcomingGames(String leagueType){
		log.debug "getAllUpcomingGames(): begins with leagueType = ${leagueType}"
		
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
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		
		log.debug "getAllUpcomingGames(): ends"
		
		return upcomingGamesList
	}
	
	def getAllUpcomingTennisGames(){
		log.debug "getAllUpcomingGames(): begins..."
		
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def c = GameTennis.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
			order("startDateTime", "asc")
		}
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		
		log.debug "getAllUpcomingGames(): ends"
		
		return upcomingGamesList
	}
	
	def getAllUpcomingMLBGames(){
		log.debug "getAllUpcomingGames(): begins..."
		
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def c = GameBaseball.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
			order("startDateTime", "asc")
		}
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		
		log.debug "getAllUpcomingGames(): ends"
		
		return upcomingGamesList
	}
	
	def getAllUpcomingNBADraftGames(){
		log.debug "getAllUpcomingGames(): begins..."
		
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def c = GameNBADraft.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
			order("startDateTime", "asc")
		}
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		
		log.debug "getAllUpcomingGames(): ends"
		
		return upcomingGamesList
	}
	
	def getAllUpcomingChampionGames(){
		log.debug "getAllUpcomingGames(): begins..."
		
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def c = GameSoccer.createCriteria()
		def upcomingGames = c.list {
			between("startDateTime", todayDate-1, upcomingDate)
			ne("eventStatus", "post-event")
			order("startDateTime", "asc")
		}
		def upcomingGamesMap = [:]
		List upcomingGamesList = constructGameList(upcomingGames, PREEVENT, todayDate)
		
		log.debug "getAllUpcomingGames(): ends"
		
		return upcomingGamesList
	}
	
	List getAllPastGames(){
		log.debug "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
//		def pastGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime>? and g.startDateTime<?and g.eventStatus='post-event'", [pastDate, todayDate+1])
		
		def c = ScorenaAllGames.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
		}
		
		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		
		log.debug "getAllPastGames(): ends"
		
		return pastGamesList
	}
	
	List getAllPastGames(String leagueType){
		log.debug "getAllPastGames(): begins with leagueType = ${leagueType}"
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
		
		def c = ScorenaAllGames.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
			ilike("eventKey", getLeaguePrefixFromLeagueEnum(getLeagueEnumFromLeagueString(leagueType))+"%")
		}
		
		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		
		log.debug "getAllPastGames(): ends"
		
		return pastGamesList
	}
	
	List getAllPastTennisGames(){
		log.debug "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
//		def pastGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime>? and g.startDateTime<?and g.eventStatus='post-event'", [pastDate, todayDate+1])
		
		def c = GameTennis.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
		}
		
		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		
		log.debug "getAllPastGames(): ends"
		
		return pastGamesList
	}
	
	List getAllPastMLBGames(){
		log.debug "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
//		def pastGames = ScorenaAllGames.findAll("from ScorenaAllGames as g where g.startDateTime>? and g.startDateTime<?and g.eventStatus='post-event'", [pastDate, todayDate+1])
		
		def c = GameBaseball.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
		}
		
		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		
		log.debug "getAllPastGames(): ends"
		
		return pastGamesList
	}
	
	List getAllPastNBADraftGames(){
		log.debug "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
		
		def c = GameNBADraft.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
		}
		
		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		
		log.debug "getAllPastGames(): ends"
		
		return pastGamesList
	}
	
	List getAllPastChampionGames(){
		log.debug "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
		
		def c = GameSoccer.createCriteria()
		def pastGames = c.list {
			between("startDateTime", pastDate, todayDate+1)
			eq("eventStatus", "post-event")
			order("startDateTime", "desc")
		}
		
		List pastGamesList = constructGameList(pastGames, POSTEVENT, todayDate)
		
		log.debug "getAllPastGames(): ends"
		
		return pastGamesList
	}
	
	Map getGame(def eventKey){
		log.debug "getGame(): begins with eventKey = ${eventKey}"
		
		def c = ScorenaAllGames.createCriteria()
		def games = c.list {
			eq("eventKey", eventKey)
		}
		def todayDate = new Date()
		
		Map game = [:]
		if (games.size() > 0)
			game = constructGameList(games, ALLEVENT, todayDate)[0]
		
		log.debug "getGame(): ends"
		return game
	}
	
	Map getMLBGame(def eventKey){
		log.debug "getMLBGame(): begins with eventKey = ${eventKey}"
		
		def c = GameBaseball.createCriteria()
		def games = c.list {
			eq("eventKey", eventKey)
		}
		def todayDate = new Date()
		
		Map game = [:]
		if (games.size() > 0)
			game = constructGameList(games, ALLEVENT, todayDate)[0]
		
		log.debug "getMLBGame(): ends"
		return game
	}
	
	Map getTennisGame(def eventKey){
		log.debug "getTennisGame(): begins with eventKey = ${eventKey}"
		
		def c = GameTennis.createCriteria()
		def games = c.list {
			eq("eventKey", eventKey)
		}
		def todayDate = new Date()
		
		Map game = [:]
		if (games.size() > 0)
			game = constructGameList(games, ALLEVENT, todayDate)[0]
		
		log.debug "getTennisGame(): ends"
		return game
	}
	
	Map getNBADraftGame(def eventKey){
		log.debug "getNBADraftGame(): begins with eventKey = ${eventKey}"
		
		def c = GameNBADraft.createCriteria()
		def games = c.list {
			eq("eventKey", eventKey)
		}
		def todayDate = new Date()
		
		Map game = [:]
		if (games.size() > 0)
			game = constructGameList(games, ALLEVENT, todayDate)[0]
		
		log.debug "getNBADraftGame(): ends"
		return game
	}
	
	Map getChampionGame(def eventKey){
		log.debug "getChampionGame(): begins with eventKey = ${eventKey}"
		
		def c = GameSoccer.createCriteria()
		def games = c.list {
			eq("eventKey", eventKey)
		}
		def todayDate = new Date()
		
		Map game = [:]
		if (games.size() > 0)
			game = constructGameList(games, ALLEVENT, todayDate)[0]
		
		log.debug "getChampionGame(): ends"
		return game
	}
	
	/**
	 * @param games
	 * @param eventType: preevent if eventType=0,  midevent if eventType=1, postevent if eventType=2
	 * @return
	 */
	List constructGameList(def games, int eventType, def todayDate){
		int gameListSize = games.size()
		log.debug "constructGameList(): begins with games = ${games}, eventType = ${eventType}, todayDate = ${todayDate}"
		
		Map gamesMap = [:]
		List gamesList = []
		for (def game: games){
			String eventKey = game.eventKey
			def gamesMapValue = gamesMap.get(eventKey)
			def gameFullName = game.fullName.trim()
			String score = ""
			if (game.score == null){
				score = ""
			}else{
				score = game.score
			}
			String matchDateString = helperService.setUTCFormat(game.startDateTime)
			def matchDate = helperService.parseDateAndTimeFromString(matchDateString)
			
			if (eventType == PREEVENT || eventType == MIDEVENT || eventType == INTERMISSION){
				if (todayDate > matchDate){
					if (game.eventStatus == "pre-event"){						
						log.error "constructGameList(): gameStatus should not be pre-event!"
						log.debug "constructGameList(): gameEvent: "+ game.eventKey
						log.debug "constructGameList(): eventStatus: " +game.eventStatus
						log.debug "constructGameList(): score: " +game.score
						log.debug "constructGameList(): team: " +gameFullName
						
						continue
					}
				}
			}
			if (eventType == POSTEVENT){
				if (game.eventStatus != "post-event"){
					log.error "constructGameList(): wrong event: "+ game.eventKey
					log.debug "constructGameList(): eventStatus: " +game.eventStatus
					log.debug "constructGameList(): score: " +game.score
					log.debug "constructGameList(): team: " +gameFullName
				}
			}
			if (!gamesMapValue){
				Map	gameInfo = [
						"leagueName": getLeagueNameFromEventKey(eventKey),
						"leagueCode": getLeagueCodeFromEventKey(eventKey),
						"gameId":eventKey,
						"type":"soccer",
						"gameStatus":game.eventStatus,
						"date": helperService.setUTCFormat(game.startDateTime),
						(game.alignment):[
							"teamname":gameFullName,
							"score":score,
							"teamLogoUrl": teamLogoService.getTeamLogo(gameFullName)
						]
				]
				if (LeagueTypeEnum.NBA == getLeagueCodeFromEventKey(eventKey)){
					
					gameInfo[game.alignment].fieldGoalPct=0
					gameInfo[game.alignment].freeThrowPct=0
					gameInfo[game.alignment].threePointersPct=0
					gameInfo[game.alignment].rebounds=0
					gameInfo[game.alignment].assists=0
					gameInfo[game.alignment].turnovers=0
					
					if (game.class == com.doozi.scorena.sportsdata.NbaGames){
						gameInfo[game.alignment].fieldGoalPct=game.fieldGoalsPercentage
						gameInfo[game.alignment].freeThrowPct=game.freeThrowPercentage
						gameInfo[game.alignment].threePointersPct=game.threePointersPercentage
						gameInfo[game.alignment].rebounds=game.rebounds
						gameInfo[game.alignment].assists=game.assists
						gameInfo[game.alignment].turnovers=game.turnovers
					}
	
				}
				
				gamesMap.putAt(eventKey, gameInfo)
			}else{
			
				if (gamesMapValue.gameStatus != game.eventStatus){
					log.error "constructGameList(): gameStatus does not match!"
					log.debug "constructGameList(): First set data: "+gamesMapValue
					log.debug "constructGameList(): second set data: "+ game.eventStatus
				}
			
				if (!gamesMapValue.away){
					gamesMapValue.away = ["teamname":gameFullName, "score":score, "teamLogoUrl": teamLogoService.getTeamLogo(gameFullName)]
					if (LeagueTypeEnum.NBA == getLeagueCodeFromEventKey(eventKey)){
						gamesMapValue.away.fieldGoalPct=0
						gamesMapValue.away.freeThrowPct=0
						gamesMapValue.away.threePointersPct=0
						gamesMapValue.away.rebounds=0
						gamesMapValue.away.assists=0
						gamesMapValue.away.turnovers=0
						
						if (game.class == com.doozi.scorena.sportsdata.NbaGames){
							gamesMapValue.away.fieldGoalPct=game.fieldGoalsPercentage
							gamesMapValue.away.freeThrowPct=game.freeThrowPercentage
							gamesMapValue.away.threePointersPct=game.threePointersPercentage
							gamesMapValue.away.rebounds=game.rebounds
							gamesMapValue.away.assists=game.assists
							gamesMapValue.away.turnovers=game.turnovers
						}
					}
				}else{
					gamesMapValue.home = ["teamname":gameFullName, "score":game.score, "teamLogoUrl": teamLogoService.getTeamLogo(gameFullName)]
					if (LeagueTypeEnum.NBA == getLeagueCodeFromEventKey(eventKey)){
						
						gamesMapValue.home.fieldGoalPct=0
						gamesMapValue.home.freeThrowPct=0
						gamesMapValue.home.threePointersPct=0
						gamesMapValue.home.rebounds=0
						gamesMapValue.home.assists=0
						gamesMapValue.home.turnovers=0						
						
						if (game.class == com.doozi.scorena.sportsdata.NbaGames){
							gamesMapValue.home.fieldGoalPct=game.fieldGoalsPercentage
							gamesMapValue.home.freeThrowPct=game.freeThrowPercentage
							gamesMapValue.home.threePointersPct=game.threePointersPercentage
							gamesMapValue.home.rebounds=game.rebounds
							gamesMapValue.home.assists=game.assists
							gamesMapValue.home.turnovers=game.turnovers
						}
					}
				}
				gamesList.add(gamesMapValue)
				
			}
		}
		
		log.debug "constructGameList(): ends"
		
		return gamesList
		
	}
}