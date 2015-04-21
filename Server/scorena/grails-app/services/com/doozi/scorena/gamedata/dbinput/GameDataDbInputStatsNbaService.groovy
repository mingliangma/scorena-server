package com.doozi.scorena.gamedata.dbinput

import groovy.time.TimeCategory
import org.springframework.transaction.annotation.Transactional
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataEventStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataNbaOutput
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataTeamStatsNba
import com.doozi.scorena.sportsdata.NbaGames
import com.doozi.scorena.sportsdata.NbaTeams
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba

@Transactional
class GameDataDbInputStatsNbaService {
	def gameDataUtilsStatsNbaService
	def helperService
	def customGameService
    def updateScore() {

			GameDataInputStatsNba gameDataInputStatsNba = new GameDataInputStatsNba();
			GameDataNbaOutput gameDateNbaOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputStatsNba);
			
			log.info "updateScore(): _eventListStatsNba size="+gameDateNbaOutput._eventListStatsNba.size()
			
			gameDateNbaOutput._eventListStatsNba.each {
				GameDataEventStatsNba event = it.value
				if (event._gameEventStatusId.toInteger() == 2 || event._gameEventStatusId.toInteger() == 3){ //update score if event status is mid-event or post-event
					println "update score: event._gameId="+event._gameId
					String gameDate = event._gameDate				
					List<NbaGames> games = NbaGames.findAllByStatsNbaGameId(event._gameId)
					for (NbaGames g : games){
						
						NbaTeams team = NbaTeams.findByScorenaTeamId(g.teamKey)
						if (team != null){
							String statsNbaTeamId = team.statsNbaTeamId
							if (event._teamListStatsNba.containsKey(statsNbaTeamId)){
																
								GameDataTeamStatsNba statsNbaTeam = event._teamListStatsNba.get(statsNbaTeamId)
								println "update score: team name=${statsNbaTeam._teamCityName}  score=${statsNbaTeam._points}"
									
								g.eventStatus = gameDataUtilsStatsNbaService.toScorenaEventStatus(event._gameEventStatusId.toInteger())
								g.score = statsNbaTeam._points
								g.fieldGoalsPercentage = statsNbaTeam._fieldGoalsPercentage
								g.freeThrowPercentage = statsNbaTeam._freeThrowPercentage
								g.threePointersPercentage = statsNbaTeam._3PointersPercentage
								g.assists = statsNbaTeam._assists
								g.rebounds = statsNbaTeam._rebounds
								g.turnovers = statsNbaTeam._turnovers
								g.lastUpdate = new Date()
								g.save(flush:true)
					
							}else{
								println "updateScore(): UPDATE SCORE ERROR: NBA team statsNbaTeamId not found in GameDataEventStatsNba: teamKey=${g.teamKey}  teamName=${g.fullName}"
							}
						}else{
							println "updateScore(): UPDATE SCORE  ERROR: NBA team not found: teamKey=${g.teamKey}  teamName=${g.fullName}"
						}
					}
					
				}else{
					println "updateScore(): no update for ${event._gameId}"
				}
			}

    }
	
	def updateSchedule(){
		
		def gameDate = new Date()
		use( TimeCategory ) {
			gameDate = gameDate - 8.hours // adjust to EST (-5 hours) and -3
		}
		String gameDateString = gameDate.format('MM/dd/yyyy')
		
		int i = 1
		boolean stay = true
		int newGameCounter = 0
		
		while(stay){
			GameDataInputStatsNba gameDataInputStatsNba = new GameDataInputStatsNba(gameDateString,  Integer.toString(i));
			GameDataNbaOutput gameDateNbaOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputStatsNba);
			
			gameDateNbaOutput._eventListStatsNba.each {
				GameDataEventStatsNba event = it.value
				
				
				
				String homeFullName
				String homeTeamKey
				String awayFullName
				String awayTeamKey
				Date startDateTime
				Date lastUpdate = new Date()
				String eventKey = "nba2014-"+event._gameId
				String statsNbaGameId = event._gameId
				String eventStatus = gameDataUtilsStatsNbaService.toScorenaEventStatus(event._gameEventStatusId.toInteger())
				String score = ""
				String fieldGoalsPercentage = ""
				String freeThrowPercentage = ""
				String threePointersPercentage = ""
				String assists = ""
				String rebounds = ""
				String turnovers = ""
				
				if (event._gameEventStatusText.equalsIgnoreCase("TBD")){
					println "TBD"
					return
				}else{
					startDateTime = helperService.parseDateTimeFromString(event._gameDate, event._gameEventStatusText)
					if (startDateTime == null){
						return
					}
				}

				if (!NbaGames.findByStatsNbaGameId(statsNbaGameId).is(null))
					return
				
				NbaTeams homeTeam = NbaTeams.findByStatsNbaTeamId(event._homeTeamId)
				NbaTeams awayTeam = NbaTeams.findByStatsNbaTeamId(event._awayTeamId)
				
				homeFullName = homeTeam.teamName
				homeTeamKey = homeTeam.scorenaTeamId
				awayFullName = awayTeam.teamName
				awayTeamKey = awayTeam.scorenaTeamId
				
				NbaGames homeGame = new NbaGames(eventKey: eventKey, statsNbaGameId:statsNbaGameId, eventStatus: eventStatus,
					startDateTime:startDateTime, lastUpdate:lastUpdate, alignment: "home", fullName:homeFullName,
					teamKey:homeTeamKey)
				NbaGames awayGame = new NbaGames(eventKey: eventKey, statsNbaGameId:statsNbaGameId, eventStatus: eventStatus,
					startDateTime:startDateTime, lastUpdate:lastUpdate, alignment: "away", fullName:awayFullName,
					teamKey:awayTeamKey)
				
				homeGame.save()
				awayGame.save()
				
				if (!homeGame.save()) {
					homeGame.errors.each {
						println it
					}
				}
				if (!awayGame.save()) {
					awayGame.errors.each {
						println it
					}
				}
				newGameCounter++
			}
			
			
			i++
			if (i >= customGameService.UPCOMING_DATE_RANGE)
				stay = false
		}
		return newGameCounter
	}
}
