/**
 * 
 */
package com.doozi.scorena.gamedata.helper

/**
 * @author Heng
 *
 */
class GameDataConstantsXmlTeam extends GameDataConstants {

	public static String defaultApiUrl = "http://www.xmlteam.com/samples/infostrada/schedule-upcoming.xml";
	public static String defaultApiMethod = "getLeagueScheduleBySeason";
	
	public static String xmlTag_TeamLeagueSchedule = "schedule";
	public static String xmlTag_sportsEvent = "sports-event";
	public static String xmlTag_EventMetadata = "event-metadata";
	
	public static String apiMethod_GetLeagueScheduleBySeason = "getLeagueScheduleBySeason";
	
	public static String gameType_Soccer = "Soccer";
	
	/**
	 * 
	 */
	public GameDataConstantsXmlTeam() 
	{
		
	}

}
