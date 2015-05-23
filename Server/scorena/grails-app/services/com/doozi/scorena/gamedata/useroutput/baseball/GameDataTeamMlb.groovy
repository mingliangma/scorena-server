package com.doozi.scorena.gamedata.useroutput.baseball

class GameDataTeamMlb {
	private String _teamId //home_team_id
	private String _teamCityName //home_team_city
	private String _teamAbbreviation //home_name_abbrev
	private String _clubName //home_team_name
	private String _win //home_win
	private String _loss // home_loss 
	
	private String _allignment
	
	private String _probablePitcherNameDisplayRoster //away_probable_pitcher.name_display_roster
	private String _probablePitcherFirstName //away_probable_pitcher.first
	private String _probablePitcherLastName //away_probable_pitcher.last
	private String _probablePitcherWins //away_probable_pitcher.wins
	private String _probablePitcherLosses //away_probable_pitcher.losses
	private String _probablePitcherERA // away_probable_pitcher.era       (stand for Earned Run Average)
	private String _probablePitcherId //away_probable_pitcher.id
	private String _probablePitcherNumber //away_probable_pitcher.number
	
	private String _homeRun 		//linescore.hr
	private String _errors  		//linescore.e
	private String _strikeOuts 	//linescore.so
	private String _runsScored	//linescore.r
	private String _stolenBases	//linescore.sb
	private String _hits			//linescore.h
	private List<String> _inningList = []	//linescore.inning
}
