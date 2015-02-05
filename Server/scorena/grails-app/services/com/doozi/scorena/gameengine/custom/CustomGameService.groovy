package com.doozi.scorena.gameengine.custom

import com.doozi.scorena.CustomGame
import com.doozi.scorena.sportsdata.*
import com.doozi.scorena.gameengine.GameService;

import org.springframework.transaction.annotation.Transactional

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
	def sportsDataService
	
	def getGame(def eventKey){
		log.info "getGame(): begins with eventKey = ${eventKey}"
		
		def games = NbaGames.findAllByEventKey(eventKey,[cache: true])
		
		def gameInfo = []
		for (NbaGames game: games){
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
						"score":game.score,
						"fieldGoalPct": game.fieldGoalsPercentage,
						"rebounds": game.rebounds,
						"freeThrowPct": game.freeThrowPercentage,
						"threePointersPct": game.threePointersPercentage,
						"assists": game.assists,
						"turnovers": game.turnovers
					]
				]
			}else{
				if (!gameInfo.away){
					gameInfo.away = ["teamname":game.fullName, "score":game.score, "fieldGoalPct": game.fieldGoalsPercentage, "rebounds": game.rebounds,
						"freeThrowPct": game.freeThrowPercentage,
						"threePointersPct": game.threePointersPercentage,
						"assists": game.assists,
						"turnovers": game.turnovers]
				}else{
					gameInfo.home = ["teamname":game.fullName, "score":game.score, "fieldGoalPct": game.fieldGoalsPercentage, "rebounds": game.rebounds,
						"freeThrowPct": game.freeThrowPercentage,
						"threePointersPct": game.threePointersPercentage,
						"assists": game.assists,
						"turnovers": game.turnovers]
				}
				
			}
		}
		
		log.info "getGame(): ends with gameInfo = ${gameInfo}"
		
		return gameInfo
	}
	
	def getAllUpcomingGames(){
		log.info "getAllUpcomingGames(): begins..."
		
		println "CustomGameService::getAllUpcomingGames(): starts..."
		def todayDate = new Date()
		def upcomingDate = todayDate + UPCOMING_DATE_RANGE;
		def upcomingGames = NbaGames.findAll("from NbaGames as g where g.startDateTime<? and g.startDateTime>? and g.eventStatus<>'post-event' order by g.startDateTime", [upcomingDate, todayDate-1], [cache: true])
		List upcomingGamesList = sportsDataService.constructGameList(upcomingGames, sportsDataService.PREEVENT, todayDate)
		
		log.info "getAllUpcomingGames(): ends with upcomingGamesList = ${upcomingGamesList}"
		
		return upcomingGamesList
	}
	
	def getAllPastGames(){
		log.info "getAllPastGames(): begins..."
		
		def todayDate = new Date()
		def pastDate = todayDate - PAST_DATE_RANGE;
		def pastGames = NbaGames.findAll("from NbaGames as g where g.startDateTime>? and g.startDateTime<? and g.eventStatus='post-event' order by g.startDateTime desc", [pastDate, todayDate+1])

		List pastGamesList = sportsDataService.constructGameList(pastGames, sportsDataService.PREEVENT, todayDate)		
		
		log.info "getAllPastGames(): ends with pastGamesList = ${pastGamesList}"
		
		return pastGamesList
	}
	
    def createCustomGameByName(String awayTeamName, String homeTeamName, String eventName, String startDateTimeInput) {
		log.info "createCustomGameByName(): begins with awayTeamName = ${awayTeamName}, eventName = ${eventName}, startDateTimeInput = ${startDateTimeInput}"
		
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
			log.info "createCustomGameByName(): Custom Game Created successfully saved"
			return [awayTeamName:awayTeamName, awayTeamKey:awayTeamKey, homeTeamName:homeTeamName, homeTeamKey:homeTeamKey, eventId:eventKey, 
				eventStatus:gameService.PREEVENT, startDateTime:helperService.setUTCFormat(startDateTime)]
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
//		if (status!=null && status!="" && (status==gameService.PREEVENT ||status==gameService.POSTEVENT ||status==gameService.MIDEVENT ||status==gameService.INTERMISSION)){
//			
//		}
//	}
	
//	def createCustomGameByKey(String awayTeamKey, String homeTeamKey, String eventKey, String startDateTime){
//		
//	}
}
