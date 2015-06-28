package com.doozi.scorena.gameengine.custom

import com.doozi.scorena.CustomGame
import com.doozi.scorena.enums.EventTypeEnum;
import com.doozi.scorena.sportsdata.*
import com.doozi.scorena.gameengine.GameService;
import com.doozi.scorena.transaction.LeagueTypeEnum

import org.springframework.transaction.annotation.Transactional

@Transactional
class CustomGameService {
	public static final String CUSTOM_TEAM_PREFIX = "customteam-"
	public static final String CUSTOM_EVENT_PREFIX = "customevent-"
	public static final String ALIGNMENT_AWAY = "away"
	public static final String ALIGNMENT_HOME = "home"
	static int UPCOMING_DATE_RANGE = 9
	static int PAST_DATE_RANGE = 7
	
	def helperService
	def gameService
	def sportsDataService
	
	Map getGame(def eventKey){
		log.info "getGame(): begins with eventKey = ${eventKey}"
		def todayDate = new Date()
		def games = NbaGames.findAllByEventKey(eventKey,[cache: true])
		if (games == null || games == []){
			games = CustomGame.findAllByEventKey(eventKey,[cache: true])
		}
		
		
		List gameList = sportsDataService.constructGameList(games, sportsDataService.ALLEVENT, todayDate)		
		Map game = [:]
		if (gameList.size() > 0)
			game = gameList[0]
		
		log.info "getGame(): ends with gameInfo = ${game}"
		return game
	}
	
	def getAllNonCustomUpcomingGames(){
		log.info "getAllUpcomingGames(): begins..."
		
		println "CustomGameService::getAllUpcomingGames(): starts..."
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def upcomingOriginalGames = NbaGames.findAll("from NbaGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1], [cache: true])
		
		List upcomingGames=[]
		upcomingGames.addAll(upcomingOriginalGames)
		log.info "upcomingGames: "+upcomingGames
		
		List upcomingGamesList = sportsDataService.constructGameList(upcomingGames, sportsDataService.PREEVENT, todayDate)
		
		log.info "getAllUpcomingGames(): ends with upcomingGamesList = ${upcomingGamesList}"
		
		return upcomingGamesList
	}
	
	def getAllUpcomingGames(){
		log.info "getAllUpcomingGames(): begins..."
		
		println "CustomGameService::getAllUpcomingGames(): starts..."
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def upcomingOriginalGames = NbaGames.findAll("from NbaGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1], [cache: true])
		def upcomingCustomGames = CustomGame.findAll("from CustomGame as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' and g.league=? order by g.startDateTime", [upcomingDate, todayDate-1, LeagueTypeEnum.NBA], [cache: true])
		
		List upcomingGames=[]
		upcomingGames.addAll(upcomingCustomGames)
		upcomingGames.addAll(upcomingOriginalGames)		
		
		upcomingGames.sort {
			it.startDateTime
		}
		
		List upcomingGamesList = sportsDataService.constructGameList(upcomingGames, sportsDataService.PREEVENT, todayDate)
		
		log.info "getAllUpcomingGames(): ends with upcomingGamesList = ${upcomingGamesList}"
		
		return upcomingGamesList
	}
	
	def getAllPastGames(){
		log.info "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE-30;
		def pastDate2 = todayDate - PAST_DATE_RANGE;
		def pastOriginalGames = NbaGames.findAll("from NbaGames as g where g.startDateTime>? and g.startDateTime<? and g.eventStatus='post-event' order by g.startDateTime desc", [pastDate, todayDate+1], [max: 30])
		def pastCustomGames = CustomGame.findAll("from CustomGame as g where g.startDateTime>? and g.startDateTime<? and g.eventStatus='post-event' and g.league=? order by g.startDateTime desc", [pastDate, todayDate+1, LeagueTypeEnum.NBA])
		
		
		
		List pastGames=[]
		pastGames.addAll(pastCustomGames)
		pastGames.addAll(pastOriginalGames)
		
		pastGames.sort {
			a,b-> b.startDateTime<=>a.startDateTime
		}
		
		List pastGamesList = sportsDataService.constructGameList(pastGames, sportsDataService.POSTEVENT, todayDate)		
		
		log.info "getAllPastGames(): ends with pastGamesList = ${pastGamesList}"
		
		return pastGamesList
	}
	
    def createCustomGameByName(String awayTeamName, String homeTeamName, String eventName, String startDateTimeInput, String league) {
		log.info "createCustomGameByName(): begins with awayTeamName = ${awayTeamName}, eventName = ${eventName}, startDateTimeInput = ${startDateTimeInput}"
		
		Random random = new Random()
		
		String awayTeamKey = league+CUSTOM_TEAM_PREFIX+(random.nextInt(1000000)).toString()
		String homeTeamKey = league+CUSTOM_TEAM_PREFIX+(random.nextInt(1000000)).toString()
		
		LeagueTypeEnum leagueType = sportsDataService.getLeagueEnumFromLeagueString(league)
		
		if (leagueType == null)
			return [error: "league not supported"]
		
		
		String eventKey = ""
		if (eventName == null || eventName==""){
			eventKey = sportsDataService.getLeaguePrefixFromLeagueEnum(leagueType)+CUSTOM_EVENT_PREFIX+(random.nextInt(1000000)).toString()
		}else{
			eventKey = sportsDataService.getLeaguePrefixFromLeagueEnum(leagueType)+CUSTOM_EVENT_PREFIX+eventName+"-"+(random.nextInt(1000000)).toString()
		}
		
		
		Date startDateTime = helperService.parseDateAndTimeFromString(startDateTimeInput)
		Date lastUpdate = new Date()
		def cgHome
		def cgAway
		if (leagueType == LeagueTypeEnum.NBA){
			
			cgHome = new CustomGame(fullName: homeTeamName, teamKey: homeTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey, 
				alignment: ALIGNMENT_HOME, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, league: LeagueTypeEnum.NBA)
			
			cgAway = new CustomGame(fullName: awayTeamName, teamKey: awayTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_AWAY, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, league: LeagueTypeEnum.NBA)
			
			
		}else if (leagueType == LeagueTypeEnum.MLB){
			
			cgHome = new GameBaseball(fullName: homeTeamName, teamKey: homeTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_HOME, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.MLB, autoQuestionCreation: false)
			
			cgAway = new GameBaseball(fullName: awayTeamName, teamKey: awayTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_AWAY, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.MLB, , autoQuestionCreation: false)
			
			
		}else if (leagueType == LeagueTypeEnum.FRENCHOPEN){
			cgHome = new GameTennis(fullName: homeTeamName, teamKey: homeTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_HOME, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.FRENCHOPEN, autoQuestionCreation: false)
			
			
			cgAway = new GameTennis(fullName: awayTeamName, teamKey: awayTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_AWAY, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.FRENCHOPEN, autoQuestionCreation: false)
			
			
		}else if (leagueType == LeagueTypeEnum.NBADRAFT){
			cgHome = new GameNbaDraft(fullName: homeTeamName, teamKey: homeTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_HOME, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.NBADRAFT, autoQuestionCreation: false)
			
			cgAway = new GameNbaDraft(fullName: awayTeamName, teamKey: awayTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_AWAY, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.NBADRAFT, autoQuestionCreation: false)
			
			
		}else if (leagueType == LeagueTypeEnum.CHAMP){
			cgHome = new GameSoccer(fullName: homeTeamName, teamKey: homeTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_HOME, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.CHAMP, autoQuestionCreation: false)
			
			cgAway = new GameSoccer(fullName: awayTeamName, teamKey: awayTeamKey, eventStatus: EventTypeEnum.PREEVENT.toString(), eventKey:eventKey,
				alignment: ALIGNMENT_AWAY, startDateTime: startDateTime, lastUpdate: lastUpdate, score: null, 
				league: LeagueTypeEnum.CHAMP, autoQuestionCreation: false)
			
			
		}else{
			return [error: "league not supported"]
		}
		
		if (cgHome.save() && cgAway.save()){
			System.out.println("Custom Game Created successfully saved")
			log.info "createCustomGameByName(): Custom Game Created successfully saved"
			return [awayTeamName:awayTeamName, awayTeamKey:awayTeamKey, homeTeamName:homeTeamName, homeTeamKey:homeTeamKey, eventId:eventKey, 
				eventStatus:EventTypeEnum.PREEVENT.toString(), startDateTime:helperService.setUTCFormat(startDateTime)]
		}else{
			System.out.println("game save failed")
			
			log.error "createCustomGameByName(): game save failed"
			log.info "createCustomGameByName(): cgHome: ${cgHome.errors}"
			log.info "createCustomGameByName(): cgAway: ${cgAway.errors}"
			
			cgHome.errors.each{
				println it
			}
			
			cgAway.errors.each{
				println it
			}
			return [error: [cgHome:cgHome.errors, cgAway:cgAway.errors]]
		}
		
    }
	
//	def changeStatus(String status, String eventKey, int questionId, String homeScore, String awayScore ){
//		
//		def question = Question.findById(questionId)
//		if (!question)
//			return [error: "There is no question with given question ID"]
//		
//		if (question.eventKey != eventKey)
//			return [error: "The event does not contain the given question ID"]
//			
//		
//		if (status!=null && status!="" && (status==EventTypeEnum.PREEVENT.toString() ||status==gameService.POSTEVENT ||status==gameService.MIDEVENT ||status==gameService.INTERMISSION)){
//			
//		}
//	}
	
//	def createCustomGameByKey(String awayTeamKey, String homeTeamKey, String eventKey, String startDateTime){
//		
//	}
}
