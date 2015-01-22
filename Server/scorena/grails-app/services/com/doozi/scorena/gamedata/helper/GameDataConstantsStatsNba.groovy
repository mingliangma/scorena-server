package com.doozi.scorena.gamedata.helper

class GameDataConstantsStatsNba {
	//http://stats.nba.com/stats/scoreboardV2?DayOffset=0&LeagueID=00&gameDate=01/14/2015
	public static String defaultHost = "http://stats.nba.com";
	public static String defaultUrlPath = "/stats/scoreboardV2";
	public static String dayOffset = "DayOffset";
	public static String leagueID = "LeagueID";
	public static String gameDate = "gameDate";
	public static String defaultDayOffsetValue = "0";
	public static String defaultLeagueIDValue = "00";
	public static String defaultGameDateValue = "01/14/2015";
	public static String defaultQueryString = "?" + dayOffset + "=" + defaultDayOffsetValue + "&" + leagueID + "=" + defaultLeagueIDValue + "&" + gameDate + "=" + defaultGameDateValue;
	public static String apiUrl =  defaultHost + defaultUrlPath;
	
}
