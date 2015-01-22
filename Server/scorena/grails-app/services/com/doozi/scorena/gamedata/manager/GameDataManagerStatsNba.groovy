package com.doozi.scorena.gamedata.manager

import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.*

import grails.plugins.rest.client.RestBuilder

class GameDataManagerStatsNba implements IGameDataManager {
	private static final GameDataManagerStatsNba _gameDataManagerStatsNbaInstance = new GameDataManagerStatsNba()
	private static final RESULT_SET_GAME_HEAD_INDEX = 0
	private static final RESULT_SET_LINE_SCORE_INDEX = 1
	private static final RESULT_SET_LAST_MEETING_INDEX = 3
	
	private static final GAME_HEAD_GAME_DATE_EST_INDEX = 0
	private static final GAME_HEAD_GAME_ID_INDEX = 2
	private static final GAME_HEAD_STATUS_ID_INDEX = 3
	private static final GAME_HEAD_STATUS_TEXT_INDEX = 4
	private static final GAME_HEAD_HOME_TEAM_ID_INDEX = 6
	private static final GAME_HEAD_AWAY_TEAM_ID_INDEX = 7
	private static final GAME_HEAD_LIVE_PERIOD_INDEX = 9
	
	private static final LINE_SCORE_GAME_SEQUENCE = 1
	private static final LINE_SCORE_GAME_ID = 2
	private static final LINE_SCORE_TEAM_ID = 3
	private static final LINE_SCORE_TEAM_ABBREVIATION = 4 
	private static final LINE_SCORE_TEAM_CITY_NAME = 5
	private static final LINE_SCORE_TEAM_WINS_LOSSES = 6 
	private static final LINE_SCORE_PTS_QTR1 = 7
	private static final LINE_SCORE_PTS_QTR2 = 8
	private static final LINE_SCORE_PTS_QTR3 = 9
	private static final LINE_SCORE_PTS_QTR4 = 10
	private static final LINE_SCORE_PTS_OT1 = 11
	private static final LINE_SCORE_PTS_OT2 = 12
	private static final LINE_SCORE_PTS_OT3 = 13
	private static final LINE_SCORE_PTS_OT4 = 14
	private static final LINE_SCORE_PTS_OT5 = 15
	private static final LINE_SCORE_PTS_OT6 = 16
	private static final LINE_SCORE_PTS_OT7 = 17
	private static final LINE_SCORE_PTS_OT8 = 18
	private static final LINE_SCORE_PTS_OT9 = 19
	private static final LINE_SCORE_PTS_OT10 = 20
	private static final LINE_SCORE_PTS = 21
	private static final LINE_SCORE_FG_PCT = 22
	private static final LINE_SCORE_FT_PCT = 23
	private static final LINE_SCORE_FG3_PCT = 24
	private static final LINE_SCORE_AST = 25
	private static final LINE_SCORE_REB = 26
	private static final LINE_SCORE_TOV =27
	
	private static final LAST_MEETING_GAME_ID = 0
	private static final LAST_MEETING_LAST_GAME_ID = 1
	private static final LAST_MEETING_GAME_DATE_EST = 2
	private static final LAST_MEETING_GAME_HOME_TEAM_ID = 3
	private static final LAST_MEETING_HOME_TEAM_CITY = 4
	private static final LAST_MEETING_HOME_TEAM_NAME = 5
	private static final LAST_MEETING_HOME_TEAM_ABBREVIATION = 6
	private static final LAST_MEETING_HOME_TEAM_POINTS = 7
	private static final LAST_MEETING_VISITOR_TEAM_ID = 8
	private static final LAST_MEETING_VISITOR_TEAM_CITY = 9 
	private static final LAST_MEETING_VISITOR_TEAM_NAME = 10
	private static final LAST_MEETING_VISITOR_TEAM_ABBREVIATION = 11
	private static final LAST_MEETING_VISITOR_TEAM_POINTS = 12
	
	
	public static GameDataManagerStatsNba get_gameDataManagerStatsNbaInstance()
	{
		return _gameDataManagerStatsNbaInstance
	}
	
	public GameDataNbaOutput retrieveGameData(GameDataInputStatsNba gameDataInputStatsNba) throws Exception
	{
		RestBuilder rest = new RestBuilder()
		println "retrieveGameData(): api url = ${gameDataInputStatsNba._apiUrl}"
		def resp = rest.get(gameDataInputStatsNba._apiUrl)
//		println "retrieveGameData(): ends with resp = ${resp.json}"
		Map originalGameData = resp.json
		return getScores(originalGameData)
	}
	
	private GameDataNbaOutput getScores(Map originalGameData){
		GameDataNbaOutput gameDataNbaOutput = new GameDataNbaOutput(originalGameData)
		
		
		
		String gamedate = originalGameData.parameters.GameDate
		List eventStatsRowSets = originalGameData.resultSets[RESULT_SET_GAME_HEAD_INDEX].rowSet
		
		for (List event: eventStatsRowSets){
			GameDataEventStatsNba gameDataEventStatsNba = new GameDataEventStatsNba()
			//convert "2015-01-18T00:00:00" format to "2015/01/18"
			gameDataEventStatsNba._gameDate = event[GAME_HEAD_GAME_DATE_EST_INDEX].split("T")[0].replace("-", "/")
			gameDataEventStatsNba._gameId =event[GAME_HEAD_GAME_ID_INDEX]
			gameDataEventStatsNba._gameEventStatusId =event[GAME_HEAD_STATUS_ID_INDEX]
			gameDataEventStatsNba._gameEventStatusText =event[GAME_HEAD_STATUS_TEXT_INDEX]
			gameDataEventStatsNba._homeTeamId =event[GAME_HEAD_HOME_TEAM_ID_INDEX]
			gameDataEventStatsNba._awayTeamId =event[GAME_HEAD_AWAY_TEAM_ID_INDEX]
			gameDataEventStatsNba._livePeriod =event[GAME_HEAD_LIVE_PERIOD_INDEX]
			List teamLineScores = originalGameData.resultSets[RESULT_SET_LINE_SCORE_INDEX].rowSet
			for (List team: teamLineScores){
				if (team[LINE_SCORE_GAME_ID] == gameDataEventStatsNba._gameId){
//					log.info "gameId=${gameDataEventStatsNba._gameId} gameStatus=${gameDataEventStatsNba._gameEventStatusText} teamList=${team}"
					GameDataTeamStatsNba GameDataTeamStatsNba = new GameDataTeamStatsNba()
					GameDataTeamStatsNba._teamId = team[LINE_SCORE_TEAM_ID]
					GameDataTeamStatsNba._teamCityName = team[LINE_SCORE_TEAM_CITY_NAME]
					GameDataTeamStatsNba._teamAbbreviation = team[LINE_SCORE_TEAM_ABBREVIATION]
					GameDataTeamStatsNba._winLosses = team[LINE_SCORE_TEAM_WINS_LOSSES]
					GameDataTeamStatsNba._pointsQuater1 = team[LINE_SCORE_PTS_QTR1]
					GameDataTeamStatsNba._pointsQuater2 = team[LINE_SCORE_PTS_QTR2]
					GameDataTeamStatsNba._pointsQuater3 = team[LINE_SCORE_PTS_QTR3]
					GameDataTeamStatsNba._pointsQuater4 = team[LINE_SCORE_PTS_QTR4]
					GameDataTeamStatsNba._pointsOverTime1 = team[LINE_SCORE_PTS_OT1]
					GameDataTeamStatsNba._pointsOverTime2 = team[LINE_SCORE_PTS_OT2]
					GameDataTeamStatsNba._pointsOverTime3 = team[LINE_SCORE_PTS_OT3]
					GameDataTeamStatsNba._pointsOverTime4 = team[LINE_SCORE_PTS_OT4]
					GameDataTeamStatsNba._pointsOverTime5 = team[LINE_SCORE_PTS_OT5]
					GameDataTeamStatsNba._pointsOverTime6 = team[LINE_SCORE_PTS_OT6]
					GameDataTeamStatsNba._pointsOverTime7 = team[LINE_SCORE_PTS_OT7]
					GameDataTeamStatsNba._pointsOverTime8 = team[LINE_SCORE_PTS_OT8]
					GameDataTeamStatsNba._pointsOverTime9 = team[LINE_SCORE_PTS_OT9]
					GameDataTeamStatsNba._pointsOverTime10 = team[LINE_SCORE_PTS_OT10]
					GameDataTeamStatsNba._points = team[LINE_SCORE_PTS]
					GameDataTeamStatsNba._fieldGoalsPercentage = team[LINE_SCORE_FG_PCT]
					GameDataTeamStatsNba._freeThrowPercentage = team[LINE_SCORE_FT_PCT]
					GameDataTeamStatsNba._3PointersPercentage = team[LINE_SCORE_FG3_PCT]
					GameDataTeamStatsNba._assists = team[LINE_SCORE_AST]
					GameDataTeamStatsNba._rebounds = team[LINE_SCORE_REB]
					GameDataTeamStatsNba._turnovers = team[LINE_SCORE_TOV]
//					GameDataTeamStatsNba._clubName = team[LINE_SCORE_TEAM_ID]
					if (GameDataTeamStatsNba._teamId == gameDataEventStatsNba._homeTeamId)
						GameDataTeamStatsNba._allignment = "home"
					else if (GameDataTeamStatsNba._teamId == gameDataEventStatsNba._awayTeamId)
						GameDataTeamStatsNba._allignment = "away"
					else
						log.error "getScores() allignment not found for teamId=${GameDataTeamStatsNba._teamId} teamCityName=${GameDataTeamStatsNba._teamCityName}"
						
					
					gameDataEventStatsNba._teamListStatsNba.put(GameDataTeamStatsNba._teamId, GameDataTeamStatsNba)
					if (gameDataEventStatsNba._teamListStatsNba.size() == 2)
						break
				}
			}	
			
			List teamLastMeeting = originalGameData.resultSets[RESULT_SET_LAST_MEETING_INDEX].rowSet
			GameDataLastMeetingNba gameDataLastMeetingNba = new GameDataLastMeetingNba()
			for(List lastMeetingEvent : teamLastMeeting){
				if (lastMeetingEvent[LAST_MEETING_GAME_ID] == gameDataEventStatsNba._gameId){					
					gameDataLastMeetingNba._gameId = lastMeetingEvent[LAST_MEETING_GAME_ID]
					gameDataLastMeetingNba._lastGameId = lastMeetingEvent[LAST_MEETING_LAST_GAME_ID]
					gameDataLastMeetingNba._lastGameDateEasternTime = lastMeetingEvent[LAST_MEETING_GAME_DATE_EST]
					gameDataLastMeetingNba._lastGameHomeTeamId = lastMeetingEvent[LAST_MEETING_GAME_HOME_TEAM_ID]
					gameDataLastMeetingNba._lastGameHomeTeamCity = lastMeetingEvent[LAST_MEETING_HOME_TEAM_CITY]
					gameDataLastMeetingNba._lastGameHomeTeamName = lastMeetingEvent[LAST_MEETING_HOME_TEAM_NAME]
					gameDataLastMeetingNba._lastGameHomeTeamAbbreviation = lastMeetingEvent[LAST_MEETING_HOME_TEAM_ABBREVIATION]
					gameDataLastMeetingNba._lastGameHomeTeamPoints = lastMeetingEvent[LAST_MEETING_HOME_TEAM_POINTS]
					gameDataLastMeetingNba._lastGameVisitorTeamId = lastMeetingEvent[LAST_MEETING_VISITOR_TEAM_ID]
					gameDataLastMeetingNba._lastGameVisitorTeamCity = lastMeetingEvent[LAST_MEETING_VISITOR_TEAM_CITY]
					gameDataLastMeetingNba._lastGameVisitorTeamName = lastMeetingEvent[LAST_MEETING_VISITOR_TEAM_NAME]
					gameDataLastMeetingNba._lastGameVisitorTeamAbbreviation = lastMeetingEvent[LAST_MEETING_VISITOR_TEAM_ABBREVIATION]
					gameDataLastMeetingNba._lastGameVisitorTeamPoints = lastMeetingEvent[LAST_MEETING_VISITOR_TEAM_POINTS]
					break
				}
			}
			gameDataEventStatsNba._lastMeetingEvent = gameDataLastMeetingNba
			gameDataNbaOutput._eventListStatsNba.put(gameDataEventStatsNba._gameId, gameDataEventStatsNba)
		}

		return gameDataNbaOutput
	}
	
}
