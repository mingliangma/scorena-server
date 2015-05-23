package com.doozi.scorena.gamedata.dbinput

import java.util.Date;

import com.doozi.scorena.sportsdata.*
import com.doozi.scorena.transaction.LeagueTypeEnum;
import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlSoccer;
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.userinput.GameDataInputMlb
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataEventMlb
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataOutputMlb
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataTeamMlb
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataEventStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataLastMeetingNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataNbaOutput
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataSoccerOutput;
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataSoccerXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataTeamXmlSoccer;

import org.springframework.transaction.annotation.Transactional

//@Transactional
class GameDataDbInputMlbService {
	def sportsDataService
	def helperService
	
    def updateTeamInfo(){
		
		boolean isAllTeamsStored = false
		int offset = 0
		while(!isAllTeamsStored){
			GameDataInputMlb gameDataInputMlb = new GameDataInputMlb(offset)
			GameDataOutputMlb gameDataOutputMlb = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputMlb);
			gameDataOutputMlb._eventListMlb.each{
				GameDataEventMlb g = it.value
				println "_gameId=" + g._gameId
				println "_gameEventStatusId=" + g._gameEventStatusId
				println "_gameEventStatusText=" + g._gameEventStatusText
				println "_homeTeamId=" + g._homeTeamId
				println "_awayTeamId=" + g._awayTeamId
				println "_gameDate=" + g._gameDate
				println "_gameDateTime" + g._gameDateTime
				println "================================================="
				
				if (TeamBaseball.findByTeamId(g._homeTeamId) == null){
					GameDataTeamMlb team = g._teamListMlb.get(g._homeTeamId)
					String teamLogoUrl = "http://mlb.mlb.com/mlb/images/team_logos/logo_"+team._teamAbbreviation+"_79x76.jpg"
					if (team._teamAbbreviation == "sfn"){
						teamLogoUrl = "http://mlb.mlb.com/mlb/images/team_logos/logo_sf_79x76.jpg"
					}
					
					TeamBaseball t = new TeamBaseball(teamId: g._homeTeamId, teamCityName: team._teamCityName, 
						teamAbbreviation: team._teamAbbreviation, clubName: team._clubName, win:team._win, loss: team._loss, 
						teamLogoUrl:teamLogoUrl, mlbLeague: g._league)
					
					t.save()
				}
				
				if (TeamBaseball.findByTeamId(g._awayTeamId) == null){
					GameDataTeamMlb team = g._teamListMlb.get(g._awayTeamId)
					
					String teamLogoUrl = "http://mlb.mlb.com/mlb/images/team_logos/logo_"+team._teamAbbreviation+"_79x76.jpg"
					if (team._teamAbbreviation == "sfn"){
						teamLogoUrl = "http://mlb.mlb.com/mlb/images/team_logos/logo_sf_79x76.jpg"
					}
					
					TeamBaseball t = new TeamBaseball(teamId: g._awayTeamId, teamCityName: team._teamCityName,
						teamAbbreviation: team._teamAbbreviation, clubName: team._clubName, win:team._win, loss: team._loss,
						teamLogoUrl:teamLogoUrl, mlbLeague: g._league)
					
					t.save()
				}
				
			}

			
			if (offset > 15){
				isAllTeamsStored = true
			}
			offset++
		}
	}
	
	def updateSchedule(){
		boolean isAllTeamsStored = false
		int offset = 0
		while(!isAllTeamsStored){
			GameDataInputMlb gameDataInputMlb = new GameDataInputMlb(offset)
			GameDataOutputMlb gameDataOutputMlb = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputMlb);
			
			Date now = new Date()
			gameDataOutputMlb._eventListMlb.each{
				GameDataEventMlb g = it.value
				println "_gameId=" + g._gameId
				println "_gameEventStatusId=" + g._gameEventStatusId
				println "_gameEventStatusText=" + g._gameEventStatusText
				println "_homeTeamId=" + g._homeTeamId
				println "_awayTeamId=" + g._awayTeamId
				println "_gameDate=" + g._gameDate
				println "_gameDateTime" + g._gameDateTime
				println "================================================="

				g._teamListMlb.each{
					GameDataTeamMlb team = it.value
					
//					GameBaseball b = new GameBaseball(
//						eventKey: getScorenaEventKey(g._gameId),
//						fullName: team._clubName,
//						teamKey: team._teamId,
//						eventStatus: sportsDataService.PREEVENT_NAME,
//						alignment: team._allignment,
//						score: "",
//						startDateTime: helperService.parseDateTimeFromString(g._gameDateTime),
//						lastUpdate: now,
//						league: LeagueTypeEnum.MLB,
//						autoQuestionCreation: true,
//
//						probablePitcherNameDisplayRoster: team._probablePitcherNameDisplayRoster,
//						probablePitcherWins: team._probablePitcherWins,
//						probablePitcherLosses: team._probablePitcherLosses,
//						probablePitcherERA: team._probablePitcherERA,
//						probablePitcherId: team._probablePitcherId,				
//					)
//					
//					b.save()

				}
			}

			
			if (offset > 15){
				isAllTeamsStored = true
			}
			offset++
		}
	}
	
	private String getScorenaEventKey(String mlbGameId){
		return sportsDataService.MLB+"_"+mlbGameId
	}
	
	private String getMlbGameId(String scorenaEventKey){
		List values = scorenaEventKey.split("_")
		if (values.size() == 2){
			return values[1]
		}else{
			return null
		}
	}
}
