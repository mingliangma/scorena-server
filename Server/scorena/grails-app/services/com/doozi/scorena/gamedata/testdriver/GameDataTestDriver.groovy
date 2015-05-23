/**
 * 
 */
package com.doozi.scorena.gamedata.testdriver;

import com.doozi.scorena.CustomGame
import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlSoccer;
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.userinput.GameDataInputMlb
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataEventMlb
import com.doozi.scorena.gamedata.useroutput.baseball.GameDataOutputMlb
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataEventStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataLastMeetingNba
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataNbaOutput
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataSoccerOutput;
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataSoccerXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataTeamXmlSoccer;

/**
 * @author hengkuang
 *
 */
public class GameDataTestDriver {

	/**
	 * 
	 */
	public GameDataTestDriver() 
	{
		
	}

	public static void main(String[] args) throws Exception
	{		
		GameDataInputMlb gameDataInputMlb = new GameDataInputMlb(1)		
		GameDataOutputMlb gameDataOutputMlb = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputMlb);
		gameDataOutputMlb._eventListMlb.each{
			GameDataEventMlb g = it.value
			println "_gameId=" + g._gameId
			println "_gameEventStatusId=" + g._gameEventStatusId
			println "_gameEventStatusText=" + g._gameEventStatusText
			println "_homeTeamId=" + g._homeTeamId
			println "_awayTeamId=" + g._awayTeamId
			println "_gameDate=" + g._gameDate
			println "_gameDateTime" + g._gameDateTime
			println "================================================="
		}
	}
	
	public testMlbGameData(){
//		GameDataInputMlb gameDataInputMlb = new GameDataInputMlb()
//		GameDataOutputMlb GameDataOutputMlb = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputMlb)
	}
	
	public testGameData(){
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
	}
	
	
	/**
	 * @param args
	 * @throws Exception 
	 */
//	public static void main1(String[] args) throws Exception 
//	{
//		GameDataInputXmlSoccer gameDataInputXmlSoccer = new GameDataInputXmlSoccer();
//		GameDataOutput gameDataOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputXmlSoccer);
//		printTeamStandingXmlSoccer(gameDataOutput);
//	}
	
	public static void printTeamStandingXmlSoccer(GameDataSoccerOutput gameDataOutput)
	{
		for (GameDataTeamXmlSoccer gameDataTeamXmlSoccer : gameDataOutput.get_teamListXmlSoccer().values())
		{
			System.out.println("************************************");
			System.out.println("Ranking: " + gameDataTeamXmlSoccer.get_ranking());
			System.out.println("Team ID: " + gameDataTeamXmlSoccer.get_teamId());
			System.out.println("Team Name: " + gameDataTeamXmlSoccer.get_teamName());
			System.out.println("Played: " + gameDataTeamXmlSoccer.get_played());
			System.out.println("Played At Home: " + gameDataTeamXmlSoccer.get_playedAtHome());
			System.out.println("Played Away: " + gameDataTeamXmlSoccer.get_playedAway());
			System.out.println("Won: " + gameDataTeamXmlSoccer.get_won());
			System.out.println("Draw: " + gameDataTeamXmlSoccer.get_draw());
			System.out.println("Lost: " + gameDataTeamXmlSoccer.get_lost());
			System.out.println("Points: " + gameDataTeamXmlSoccer.get_points());
			
			if (gameDataTeamXmlSoccer.get_teamType().equalsIgnoreCase(GameDataConstantsXmlSoccer.gameType_Soccer))
			{
				GameDataSoccerXmlSoccer gameDataSoccerXmlSoccer = gameDataTeamXmlSoccer.get_gameDataSoccer();
				System.out.println("Number of Shots: " + gameDataSoccerXmlSoccer.get_numberOfShots());
				System.out.println("Yellow Cards: " + gameDataSoccerXmlSoccer.get_yellowCards());
				System.out.println("Red Cards: " + gameDataSoccerXmlSoccer.get_redCards());
				System.out.println("Goals For: " + gameDataSoccerXmlSoccer.get_goalsFor());
				System.out.println("Goals Against: " + gameDataSoccerXmlSoccer.get_goalsAgainst());
				System.out.println("Goal Difference: " + gameDataSoccerXmlSoccer.get_goalDifference());
			}
		}
	}
}
