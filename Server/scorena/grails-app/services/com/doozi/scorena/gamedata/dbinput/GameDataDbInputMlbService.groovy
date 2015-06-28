package com.doozi.scorena.gamedata.dbinput

import java.util.Date;

import com.doozi.scorena.sportsdata.*
import com.doozi.scorena.transaction.LeagueTypeEnum;
import com.doozi.scorena.enums.EventTypeEnum;
import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlSoccer;
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.userinput.GameDataInputMlb
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataEventMlb
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataHomeRunPlayer
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
	
	def updateScore(){
		log.info "updateScore() begins"
		
		boolean isAllTeamsStored = false
		int offset = 0
		while(offset >= -1){
			GameDataInputMlb gameDataInputMlb = new GameDataInputMlb(offset)
			GameDataOutputMlb gameDataOutputMlb = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputMlb);
			
			Date now = new Date()
			gameDataOutputMlb._eventListMlb.each{
				GameDataEventMlb g = it.value

				List<GameBaseball> games = GameBaseball.findAllByEventKey(getScorenaEventKey(g._gameId))
				for (GameBaseball game: games){
					
					if (g._teamListMlb.containsKey(game.teamKey)){
						GameDataTeamMlb team = g._teamListMlb.get(game.teamKey)
						
						String inningsString = ""
						for (int i = 0; i < team._inningList.size(); i++){
							String inning = team._inningList.get(i)
							if (i == team._inningList.size() - 1){								
								inningsString = inningsString + inning								
							}else{
								inningsString = inningsString + inning + "-"
							}
						}												

						game.homeRun = team._homeRun
						game.errorsCount = team._errors
						game.strikeOuts = team._strikeOuts
						game.runsScored = team._runsScored
						game.score = team._runsScored
						game.stolenBases = team._stolenBases						
						game.hits = team._hits
						game.innings = inningsString
						game.lastUpdate = now
						game.eventStatus = getEventStatus(g._gameEventStatusText)
						game.eventKey = getScorenaEventKey(g._gameId)
						game.eventKeyDup = getScorenaEventKey(g._gameId)
						
						if(g._gameDateTime && g._gameEventStatusText == GameDataEventMlb.STATUS_POSTPONED){
							game.startDateTime = helperService.parseDateTimeFromString(g._gameDateTime)
						}
						
						if (!game.save()) {
							game.errors.each {
								println it
							}
							println "_gameId=" + getScorenaEventKey(g._gameId)
							println "eventStatus="+g._gameEventStatusText
							
						}else{
//							println "================game score updated==============="
//							println "_gameId=" + getScorenaEventKey(g._gameId)
//							println "_gameEventStatusText=" + g._gameEventStatusText
//							println "_gameDateTime" + g._gameDateTime
//							println "================================================="
						}
						
					}else{	
						println "updateScore(): UPDATE SCORE ERROR: NBA team statsNbaTeamId not found in GameDataEventStatsNba: teamKey=${game.teamKey}  teamName=${game.fullName}"
					}

				}				
				
				if(g._homeRunPlayerList.size() > 0){
					for (GameDataHomeRunPlayer hr: g._homeRunPlayerList){
						GameBaseballHomeRun homeRun = GameBaseballHomeRun.findByEventKeyAndPlayerId(getScorenaEventKey(g._gameId), hr._playerId)
						
						if (homeRun){
							homeRun.homeRunYtd = hr._homeRunYtd
							homeRun.homeRun = hr._homeRun
							homeRun.inning = hr._inning
							homeRun.runners = hr._runners
							if (!homeRun.save()) {
								homeRun.errors.each {
								println it
								}
							}else{
//								println "============existing home run updated============"
//								println "_gameId=" + getScorenaEventKey(g._gameId)
//								println "_playerId=" + hr._playerId
//								println "================================================="
							}
						}else{
						
							GameBaseballHomeRun newhr = new GameBaseballHomeRun(
								eventKey: getScorenaEventKey(g._gameId),
								playerId: hr._playerId,
								nameDisplayRoster: hr._nameDisplayRoster,
								firstName: hr._firstName ,
								lastName:hr._lastName ,
								playerNumber:hr._playerNumber ,
								
								teamCode:hr._teamCode ,
								homeRunYtd:hr._homeRunYtd ,
								homeRun:hr._homeRun ,
								inning:hr._inning ,
								runners:hr._runners 
								)
							if (!newhr.save()) {
								
								newhr.errors.each {
								println it
								}
								println "_gameId=" + getScorenaEventKey(g._gameId)
								println "_playerId=" + hr._playerId
							}else{
							
//								println "============new home run added==================="
//								println "_gameId=" + getScorenaEventKey(g._gameId)
//								println "_playerId=" + hr._playerId
//								println "================================================="
							}
						}
					}
				}
				
			}
			offset--
		}
	}
	
	def updateSchedule(){
		log.info "updateSchedule() starts"
		boolean isAllTeamsStored = false
		int offset = 0
		while(offset < 7){
			println "offset: "+offset
			GameDataInputMlb gameDataInputMlb = new GameDataInputMlb(offset)
			GameDataOutputMlb gameDataOutputMlb = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputMlb);
			
			Date now = new Date()
			gameDataOutputMlb._eventListMlb.each{
				GameDataEventMlb g = it.value
				
				if (!GameBaseball.findByEventKey(getScorenaEventKey(g._gameId)).is(null)){
					println g._gameId + " already exists in game table. Skip update schedule"
					return
				}
				
				println "_gameId=" + getScorenaEventKey(g._gameId)
				println "_gameEventStatusId=" + g._gameEventStatusId
				println "_gameEventStatusText=" + g._gameEventStatusText
				println "_homeTeamId=" + g._homeTeamId
				println "_awayTeamId=" + g._awayTeamId
				println "_gameDate=" + g._gameDate
				println "_gameDateTime" + g._gameDateTime
				println "================================================="

				
				
				
				g._teamListMlb.each{
					GameDataTeamMlb team = it.value
					
					String inningsString = ""
					for (int i = 0; i < team._inningList.size(); i++){
						String inning = team._inningList.get(i)
						if (i == team._inningList.size() - 1){
							if (inning == null){
								inningsString = inningsString + "X"
							}else{
								inningsString = inningsString + inning
							}
						}else{
							inningsString = inningsString + inning + "-"
						}
					}
					
					GameBaseball b = new GameBaseball(
						eventKey: getScorenaEventKey(g._gameId),
						eventKeyDup: getScorenaEventKey(g._gameId),
						fullName: team._clubName,
						teamKey: team._teamId,
						eventStatus: getEventStatus(g._gameEventStatusText),
						alignment: team._allignment,
						startDateTime: helperService.parseDateTimeFromString(g._gameDateTime),
						lastUpdate: now,
						league: LeagueTypeEnum.MLB,
						autoQuestionCreation: true,

						probablePitcherNameDisplayRoster: team._probablePitcherNameDisplayRoster,
						probablePitcherWins: team._probablePitcherWins,
						probablePitcherLosses: team._probablePitcherLosses,
						probablePitcherERA: team._probablePitcherERA,
						probablePitcherId: team._probablePitcherId,		
						probablePitcherFirstName: team._probablePitcherFirstName,
						probablePitcherLastName: team._probablePitcherLastName,
						probablePitcherNumber: team._probablePitcherNumber,
						
						homeRun: team._homeRun,
						errorsCount: team._errors,
						strikeOuts: team._strikeOuts,
						runsScored: team._runsScored,
						score: team._runsScored,
						stolenBases: team._stolenBases,
						hits: team._hits,
						innings: inningsString
					)					
					b.save()
				}
				
				if(g._homeRunPlayerList.size() > 0){
					for (GameDataHomeRunPlayer hr: g._homeRunPlayerList){
						
						GameBaseballHomeRun homeRun = GameBaseballHomeRun.findByEventKeyAndPlayerId(getScorenaEventKey(g._gameId), hr._playerId)
						if (homeRun){
							homeRun.homeRunYtd = hr._homeRunYtd
							homeRun.homeRun = hr._homeRun
							homeRun.inning = hr._inning
							homeRun.runners = hr._runners
							homeRun.save()
						}else{
						
							new GameBaseballHomeRun(
								eventKey: getScorenaEventKey(g._gameId),
								playerId: hr._playerId,
								nameDisplayRoster: hr._nameDisplayRoster,
								firstName: hr._firstName ,
								lastName:hr._lastName ,
								playerNumber:hr._playerNumber ,
								
								teamCode:hr._teamCode ,
								homeRunYtd:hr._homeRunYtd ,
								homeRun:hr._homeRun ,
								inning:hr._inning ,
								runners:hr._runners 
								).save()
						}
					}
				}
				
			}

			offset++
		}
		log.info "updateSchedule() ended"
	}
	
	private String getEventStatus(String statusText){
		switch (statusText){
			case GameDataEventMlb.STATUS_GAMEOVER:
				return EventTypeEnum.POSTEVENT.toString()
			case GameDataEventMlb.STATUS_FINAL:
				return EventTypeEnum.POSTEVENT.toString()
			case GameDataEventMlb.STATUS_INPROGRESS:
				return EventTypeEnum.MIDEVENT.toString()
			case GameDataEventMlb.STATUS_DELAYEDSTART:
				return EventTypeEnum.PREEVENT.toString()
			case GameDataEventMlb.STATUS_PREVIEW:
				return EventTypeEnum.PREEVENT.toString()
			case GameDataEventMlb.STATUS_PREGAME:
				return EventTypeEnum.PREEVENT.toString()
			case GameDataEventMlb.STATUS_WARMUP:
				return EventTypeEnum.PREEVENT.toString()
			case GameDataEventMlb.STATUS_POSTPONED:
				return EventTypeEnum.POSTPONED.toString()
			default:
				return null
		}
	}
	
	private String getScorenaEventKey(String mlbGameId){
		return sportsDataService.MLB+"_"+mlbGameId.replaceAll("/", "-")
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
