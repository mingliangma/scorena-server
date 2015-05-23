package com.doozi.scorena.gamedata.useroutput.baseball

import java.util.List;

class GameDataEventMlb {
	private String _gameId;
	private String _gameEventStatusId;
	private String _gameEventStatusText;
	private String _homeTeamId;
	private String _awayTeamId;
	private String _league;
	private String _gameDate;
	private String _gameDateTime; //2015/05/19 7:05 PM ET
	
	private Map<String, GameDataTeamMlb> _teamListMlb = [:]
	private List<GameDataHomeRunPlayer> _homeRunPlayerList = []	//home_runs.player
	
}
