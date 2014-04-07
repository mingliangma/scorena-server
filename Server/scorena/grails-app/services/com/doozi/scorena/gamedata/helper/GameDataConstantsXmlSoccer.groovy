/**
 * 
 */
package com.doozi.scorena.gamedata.helper;

/**
 * @author hengkuang
 *
 */
public class GameDataConstantsXmlSoccer extends GameDataConstants {

	public static String apiKey = "ApiKey";
	public static String leagueKey = "league";
	public static String sessionDateKey = "seasonDateString";
	public static String defaultApiKeyValue = "CKBJQWVHWIAOMBGNYACRQKGDVYTRRPDHRQEGCIRBIVUDURDLGD";
	public static String defaultLeagueName = "Scottish%20Premier20%League";
	public static String defaultSessionDate = "1314";
	public static String defaultHost = "http://www.xmlsoccer.com/";
	public static String defaultApiType = "FootballDataDemo.asmx/";
	public static String defaultApiMethod = "GetLeagueStandingsBySeason";
	public static String defaultQueryString = "?" + apiKey + "=" + defaultApiKeyValue + "&" + leagueKey + "=" + defaultLeagueName + "&" + sessionDateKey + "=" + defaultSessionDate;
	public static String defaultApiUrl = defaultHost + defaultApiType + defaultApiMethod + defaultQueryString;
	
	public static String apiMethod_GetLeagueStandingsBySeason = "GetLeagueStandingsBySeason";
	
	public static String xmlTag_TeamLeagueStanding = "TeamLeagueStanding";
	public static String xmlTag_TeamName = "Team";
	public static String xmlTag_TeamId = "Team_Id";
	public static String xmlTag_Played = "Played";
	public static String xmlTag_PlayedAtHome = "PlayedAtHome";
	public static String xmlTag_PlayedAway = "PlayedAway";
	public static String xmlTag_Won = "Won";
	public static String xmlTag_Draw = "Draw";
	public static String xmlTag_Lost = "Lost";
	public static String xmlTag_NumberOfShots = "NumberOfShots";
	public static String xmlTag_YellowCards = "YellowCards";
	public static String xmlTag_RedCards = "RedCards";
	public static String xmlTag_GoalsFor = "Goals_For";
	public static String xmlTag_GoalsAgainst = "Goals_Against";
	public static String xmlTag_GoalDifference = "Goal_Difference";
	public static String xmlTag_Points = "Points";
	
	public static String xmlteam_xmlTag_TeamLeagueSchedule = "schedule"
	public static String xmlteam_xmlTag_GameEvent = "sports-event"
	public static String xmlteam_xmlTag_EventMetadata = "event-metadata"
	
	public static String gameType_Soccer = "Soccer";
	
	/**
	 * 
	 */
	public GameDataConstantsXmlSoccer() 
	{
		
	}

}
