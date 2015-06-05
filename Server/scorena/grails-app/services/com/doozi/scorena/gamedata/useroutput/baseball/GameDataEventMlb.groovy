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
	
	static final String STATUS_PREVIEW = "Preview"
	static final String STATUS_PREGAME = "Pre-Game"
	static final String STATUS_WARMUP = "Warmup"
	static final String STATUS_DELAYEDSTART = "Delayed Start"
	static final String STATUS_POSTPONED = "Postponed"
	static final String STATUS_INPROGRESS = "In Progress"
	static final String STATUS_GAMEOVER = "Game Over"
	static final String STATUS_FINAL = "Final" 

}
