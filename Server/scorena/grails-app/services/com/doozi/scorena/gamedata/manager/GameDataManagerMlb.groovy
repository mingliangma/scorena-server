package com.doozi.scorena.gamedata.manager

import java.util.List;

import com.doozi.scorena.gamedata.userinput.GameDataInputMlb
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba;
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataEventMlb
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataHomeRunPlayer;
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataOutputMlb
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataTeamMlb
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataNbaOutput;
import com.mysql.jdbc.log.Log;

import grails.plugins.rest.client.RestBuilder

class GameDataManagerMlb implements IGameDataManager{
	private static final GameDataManagerMlb _gameDataManagerMlbInstance = new GameDataManagerMlb()
	
	public static GameDataManagerMlb get_gameDataManagerMlbInstance(){
		return _gameDataManagerMlbInstance
	}
	
	public GameDataOutputMlb retrieveGameData(GameDataInputMlb gameDataInputMlb) throws Exception
	{
		RestBuilder rest = new RestBuilder()
		println "retrieveGameData(): api url = ${gameDataInputMlb._apiUrl}"
		def resp = rest.get(gameDataInputMlb._apiUrl)
//		println "retrieveGameData(): ends with resp = ${resp.json}"
		Map originalGameData = resp.json
		return getScores(originalGameData)
	}
	
	private GameDataOutputMlb getScores(Map originalGameData){
		GameDataOutputMlb gameDataMlbOutput = new GameDataOutputMlb(originalGameData)
		
		if (!originalGameData.data || !originalGameData.data.games || !originalGameData.data.games.year ||  
			!originalGameData.data.games.month || !originalGameData.data.games.day || !originalGameData.data.games.game){
		
			log.error "getScores() originalGameData properties missing"
			return gameDataMlbOutput
		}
		
		List gameList = originalGameData.data.games.game
		if (gameList.size() == 0){
			log.debug "getScores(): empty game list"
			return gameDataMlbOutput
		}
	
		for (Map g: gameList){
			GameDataEventMlb gameDataEventMlb = new GameDataEventMlb() 
			
			gameDataEventMlb._gameDate = g.original_date
			gameDataEventMlb._gameDateTime = g.time_date + " " + g.ampm + " " + g.time_zone
			gameDataEventMlb._gameId = g.id
			gameDataEventMlb._gameEventStatusText = g.status.status
			gameDataEventMlb._homeTeamId = g.home_team_id
			gameDataEventMlb._awayTeamId = g.away_team_id
			gameDataEventMlb._league = g.league
			
			if (g.home_runs){
				def homeRunPlayerList = g.home_runs.player
				
				if (homeRunPlayerList.getClass() == org.codehaus.groovy.grails.web.json.JSONObject){
					GameDataHomeRunPlayer hrp = new GameDataHomeRunPlayer()
					hrp._playerId = homeRunPlayerList.get("id")
					hrp._nameDisplayRoster = homeRunPlayerList.get("name_display_roster")
					hrp._firstName = homeRunPlayerList.get("first")
					hrp._lastName = homeRunPlayerList.get("last")
					hrp._playerNumber = homeRunPlayerList.get("number")
					hrp._teamCode = homeRunPlayerList.get("team_code")
					hrp._homeRunYtd = homeRunPlayerList.get("std_hr").toInteger()
					hrp._homeRun = homeRunPlayerList.get("hr").toInteger()
					hrp._inning = homeRunPlayerList.get("inning").toInteger()
					hrp._runners = homeRunPlayerList.get("runners").toInteger()
					gameDataEventMlb._homeRunPlayerList.add(hrp)
				}else{
					for (def hrPlayer: homeRunPlayerList){
						GameDataHomeRunPlayer hrp = new GameDataHomeRunPlayer()
						hrp._playerId = hrPlayer.get("id")
						hrp._nameDisplayRoster = hrPlayer.get("name_display_roster")
						hrp._firstName = hrPlayer.get("first")
						hrp._lastName = hrPlayer.get("last")
						hrp._playerNumber = hrPlayer.get("number")
						hrp._teamCode = hrPlayer.get("team_code")
						hrp._homeRunYtd = hrPlayer.get("std_hr").toInteger()
						hrp._homeRun = hrPlayer.get("hr").toInteger()
						hrp._inning = hrPlayer.get("inning").toInteger()
						hrp._runners = hrPlayer.get("runners").toInteger()
						gameDataEventMlb._homeRunPlayerList.add(hrp)
					}
				}
			}
			
			GameDataTeamMlb homeTeam = new GameDataTeamMlb()
			homeTeam._teamId = g.home_team_id
			homeTeam._teamCityName = g.home_team_city
			homeTeam._teamAbbreviation = g.home_code
			homeTeam._clubName = g.home_team_name
			homeTeam._win = g.home_win
			homeTeam._loss = g.home_loss
			homeTeam._allignment = "home"
			
			if (g.home_probable_pitcher){
				homeTeam._probablePitcherNameDisplayRoster = g.home_probable_pitcher.name_display_roster
				homeTeam._probablePitcherFirstName = g.home_probable_pitcher.first
				homeTeam._probablePitcherLastName = g.home_probable_pitcher.last
				homeTeam._probablePitcherWins = g.home_probable_pitcher.wins
				homeTeam._probablePitcherLosses = g.home_probable_pitcher.losses
				homeTeam._probablePitcherERA = g.home_probable_pitcher.era
				homeTeam._probablePitcherId = g.home_probable_pitcher.id
				homeTeam._probablePitcherNumber = g.home_probable_pitcher.number
			}
			
			if (g.linescore){
				homeTeam._homeRun = g.linescore.hr.home
				homeTeam._errors  = g.linescore.e.home
				homeTeam._strikeOuts = g.linescore.so.home
				homeTeam._runsScored	= g.linescore.r.home
				homeTeam._stolenBases = g.linescore.sb.home
				homeTeam._hits = g.linescore.h.home
				
				if (g.linescore.inning.getClass() == org.codehaus.groovy.grails.web.json.JSONObject){
					if (g.linescore.inning.home && g.linescore.inning.away){
						homeTeam._inningList.add(g.linescore.inning.home)
					}
				}else{
					List<Map> inningList = g.linescore.inning
					int i = 0
					for (Map inning: inningList){
						if (i == inningList.size()-1 && inning.home == null){
							homeTeam._inningList.add("X")
						}else{							
							if (inning.home == ""){
								homeTeam._inningList.add("0")
							}else{
								homeTeam._inningList.add(inning.home)
							}
						}
						i++
					}
				}
				
			}
			
			GameDataTeamMlb awayTeam = new GameDataTeamMlb()
			awayTeam._teamId = g.away_team_id
			awayTeam._teamCityName = g.away_team_city
			awayTeam._teamAbbreviation = g.away_code
			awayTeam._clubName = g.away_team_name
			awayTeam._win = g.away_win
			awayTeam._loss = g.away_loss
			awayTeam._allignment = "away"
			
			if (g.away_probable_pitcher){
				awayTeam._probablePitcherNameDisplayRoster = g.away_probable_pitcher.name_display_roster
				awayTeam._probablePitcherFirstName = g.away_probable_pitcher.first
				awayTeam._probablePitcherLastName = g.away_probable_pitcher.last
				awayTeam._probablePitcherWins = g.away_probable_pitcher.wins
				awayTeam._probablePitcherLosses = g.away_probable_pitcher.losses
				awayTeam._probablePitcherERA = g.away_probable_pitcher.era
				awayTeam._probablePitcherId = g.away_probable_pitcher.id
				awayTeam._probablePitcherNumber = g.away_probable_pitcher.number
			}
			
			if (g.linescore){
				awayTeam._homeRun = g.linescore.hr.away
				awayTeam._errors  = g.linescore.e.away
				awayTeam._strikeOuts = g.linescore.so.away
				awayTeam._runsScored	= g.linescore.r.away
				awayTeam._stolenBases = g.linescore.sb.away
				awayTeam._hits = g.linescore.h.away
				
				
				if (g.linescore.inning.getClass() == org.codehaus.groovy.grails.web.json.JSONObject){
					if (g.linescore.inning.home && g.linescore.inning.away){
						awayTeam._inningList.add(g.linescore.inning.away)
					}
				}else{
					List<Map> inningList = g.linescore.inning					
					int i = 0
					for (Map inning: inningList){
						if (i == inningList.size()-1 && inning.away == null){
							awayTeam._inningList.add("X")
						}else{
							if (inning.away == ""){
								awayTeam._inningList.add("0")
							}else{
								awayTeam._inningList.add(inning.away)
							}
						}
						i++
					}
					
				}
				
			}
			
			
			gameDataEventMlb._teamListMlb.put(homeTeam._teamId, homeTeam)
			gameDataEventMlb._teamListMlb.put(awayTeam._teamId, awayTeam)
			gameDataMlbOutput._eventListMlb.put(g.id, gameDataEventMlb)
		}
		return gameDataMlbOutput
	}
	
}
