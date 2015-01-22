package com.doozi.scorena.gamedata.dbinput

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
}
