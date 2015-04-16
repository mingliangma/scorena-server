package com.doozi.scorena.gamedata.useroutput.basketball

import java.util.LinkedHashMap;

class GameDataEventStatsNba {
	private String _gameId;
	private String _gameEventStatusId;
	private String _gameEventStatusText;
	private String _homeTeamId;
	private String _awayTeamId;
	private String _livePeriod;
	private String _gameDate;
	private GameDataLastMeetingNba _lastMeetingEvent
	//_teamListStatsNba = [teamId: GameDataTeamStatsNba]
	private Map<String, GameDataTeamStatsNba> _teamListStatsNba = [:]
}
