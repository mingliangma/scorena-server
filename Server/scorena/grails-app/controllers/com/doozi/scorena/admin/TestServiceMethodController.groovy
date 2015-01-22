package com.doozi.scorena.admin
import com.doozi.scorena.CustomGame
import com.doozi.scorena.sportsdata.NbaGames;
import com.doozi.scorena.sportsdata.NbaTeams;
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataEventStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataLastMeetingNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataNbaOutput
import com.mysql.jdbc.log.Log;
import grails.converters.JSON
import groovy.time.TimeCategory

class TestServiceMethodController {
	def gameDataDbInputStatsNbaService
	
	//match scorena team id to Stats.Nba.com team id
    def testTeamData() { 
		
		println "test starts"
		int i = 80
		while(NbaTeams.count < 32){
			
			GameDataInputStatsNba gameDataInputStatsNba = new GameDataInputStatsNba("11/25/2014", Integer.toString(i));
			GameDataNbaOutput gameDateNbaOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputStatsNba);
			
			gameDateNbaOutput._eventListStatsNba.each {
				GameDataEventStatsNba event = it.value
				GameDataLastMeetingNba lastMeeting = event._lastMeetingEvent
				String teamName1 = lastMeeting._lastGameHomeTeamName
				if (teamName1 == "East"){
					teamName1 = "East All-Stars"
				}else if (teamName1 == "West"){
					teamName1 = "West All-Stars"
				}
				if (NbaTeams.findByTeamName(teamName1) == null){
					
					println "team name: ${teamName1}"
					NbaTeams team = new NbaTeams(teamName: teamName1, teamCityName: lastMeeting._lastGameHomeTeamCity, teamAbbreviation:lastMeeting._lastGameHomeTeamAbbreviation, 
						statsNbaTeamId: lastMeeting._lastGameHomeTeamId, scorenaTeamId: CustomGame.findByFullName(teamName1).teamKey)
					team.save()
					println "team saved: ${teamName1}"				
				}
				
				String teamName2 = lastMeeting._lastGameVisitorTeamName
				if (teamName2 == "East"){
					teamName2 = "East All-Stars"
				}else if (teamName2 == "West"){
					teamName2 = "West All-Stars"
				}
				if (NbaTeams.findByTeamName(teamName2) == null){
					
					println "team name: ${teamName2}"
					NbaTeams team = new NbaTeams(teamName: teamName2, teamCityName: lastMeeting._lastGameVisitorTeamCity, teamAbbreviation:lastMeeting._lastGameVisitorTeamAbbreviation,
						statsNbaTeamId: lastMeeting._lastGameVisitorTeamId, scorenaTeamId: CustomGame.findByFullName(teamName2).teamKey)
					team.save()
					println "team saved: ${teamName2}"
				}
			}
			i++
		}		
	}
	
	//match scorena game id to Stats.nba.com game id
	def testGameData() {
		int i = 0
		boolean stay = true
		while(stay){
			GameDataInputStatsNba gameDataInputStatsNba = new GameDataInputStatsNba("12/05/2014",  Integer.toString(i));
			GameDataNbaOutput gameDateNbaOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputStatsNba);
					
			gameDateNbaOutput._eventListStatsNba.each {
				GameDataEventStatsNba event = it.value
				println "event gameId="+event._gameId
				println "event game date="+event._gameDate
				println "i="+i
				
				String gameDate = event._gameDate
				if (new Date(gameDate) > new Date("2015/04/18"))
					stay = false
					
//				List<NbaGames> games = NbaGames.findAllByStartDateTimeBetween(new Date(gameDate), new Date(gameDate) + 1)
				def date1 = new Date(gameDate)
				def date2
				use( TimeCategory ) {
					date1 = date1 + 12.hours
					date2 = date1 + 24.hours
				}
				Set statsNbaTeamId = event._teamListStatsNba.keySet()
				if (statsNbaTeamId.size() != 2){
					println "===============ERROR: EVENT TEAMS ARE NOT 2==============="
				}
					
				println "team1="+statsNbaTeamId[0]
				println "team2="+statsNbaTeamId[1]
				println "date1="+date1
				println "date2="+date2
				println "team ids: "+NbaTeams.executeQuery("select scorenaTeamId from NbaTeams as t where t.statsNbaTeamId = ? or t.statsNbaTeamId = ?", [statsNbaTeamId[0], statsNbaTeamId[1]])
				List<NbaGames> games = NbaGames.findAll{
					startDateTime >= date1 && startDateTime<=date2 && teamKey in (
						NbaTeams.executeQuery("select scorenaTeamId from NbaTeams as t where t.statsNbaTeamId = ? or t.statsNbaTeamId = ?", [statsNbaTeamId[0], statsNbaTeamId[1]]))
				}
				
				if (games.size() > 2){
					println "ERROR GAME SIZE > 2"
					stay = false
				}else{
					for (NbaGames g : games){
						if (g.statsNbaGameId == null || g.statsNbaGameId == ""){
							g.statsNbaGameId = event._gameId
							g.lastUpdate=new Date()
							g.save(flush:true)
							println "eventKey=${g.eventKey} , teamKey=${g.teamKey} saved on ${g.startDateTime}"
						}else{
							println "eventKey=${g.eventKey}, statsNbaGameId=${g.statsNbaGameId}, teamKey=${g.teamKey} already exists"
						}
					}
				}
				
				
				println "========================================================================"
				println "========================================================================"

			}
//			if (i==7){
//				stay=false
//			}
			
			i++
			stay = false
		}
		
	}
	
	def updateScore(){
		gameDataDbInputStatsNbaService.updateScore()
	}
	
}
