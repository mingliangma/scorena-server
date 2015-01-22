package com.doozi.scorena.gamedata

import com.doozi.scorena.CustomGame
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataEventStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataLastMeetingNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataNbaOutput

GameDataInputStatsNba gameDataInputStatsNba = new GameDataInputStatsNba();
GameDataNbaOutput gameDateNbaOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputStatsNba);

gameDateNbaOutput._eventListStatsNba.each {
	GameDataEventStatsNba event = it.value
	GameDataLastMeetingNba lastMeeting = event._lastMeetingEvent
	String teamName1 = lastMeeting._lastGameHomeTeamName
	println "_gameId=${lastMeeting._gameId}"
	println "team=${teamName1}"	
	println "_lastGameDateEasternTime=${lastMeeting._lastGameDateEasternTime}"
	println "_lastGameHomeTeamId=${lastMeeting._lastGameHomeTeamId}"
	println "_lastGameVisitorTeamName=${lastMeeting._lastGameVisitorTeamName}"
	println "_lastGameVisitorTeamId=${lastMeeting._lastGameVisitorTeamId}"
	CustomGame cGame = CustomGame.findByFullName(teamName1)
	println "found team in scorena db team=${cGame.fullName}"
}
