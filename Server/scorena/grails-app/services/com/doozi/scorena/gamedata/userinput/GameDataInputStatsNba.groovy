package com.doozi.scorena.gamedata.userinput

import com.doozi.scorena.gamedata.manager.IGameDataAdapter
import com.doozi.scorena.gamedata.helper.GameDataConstantsStatsNba
import groovy.time.TimeCategory

class GameDataInputStatsNba implements IGameDataInput{

	private String _apiUrl;
	private String _dayOffSet;
	private String _leagueID;
	private String _gameDate;
	
	public GameDataInputStatsNba(){
		
		_dayOffSet = GameDataConstantsStatsNba.defaultDayOffsetValue
		_leagueID = GameDataConstantsStatsNba.defaultLeagueIDValue
		
		Date gameDate = new Date()
		use( TimeCategory ) {
			gameDate = gameDate - 8.hours // adjust to EST (-5 hours) and -3
		}
		_gameDate = gameDate.format('MM/dd/yyyy')
		_apiUrl = getTodayGamesApiUrl(_dayOffSet, _leagueID, _gameDate)
	}
	
	public GameDataInputStatsNba(String gameDate, String dayOffSet){
		
		_dayOffSet = dayOffSet
		_leagueID = GameDataConstantsStatsNba.defaultLeagueIDValue
		_gameDate = gameDate
		_apiUrl = getTodayGamesApiUrl(_dayOffSet, _leagueID, _gameDate)
	}
	
	String getTodayGamesApiUrl(String dayOffSet, String leagueId, gameDate){
		return GameDataConstantsStatsNba.apiUrl + "?" + GameDataConstantsStatsNba.dayOffset + "=" + dayOffSet + "&" + 
		GameDataConstantsStatsNba.leagueID + "=" + leagueId + "&" + GameDataConstantsStatsNba.gameDate + "=" + gameDate;
	}
}
