/**
 * 
 */
package com.doozi.scorena.gamedata.testdriver;

import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlSoccer;
import com.doozi.scorena.gamedata.manager.GameDataAdapter;
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
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
		GameDataInputStatsNba gameDataInputStatsNba = new GameDataInputStatsNba();
		GameDataSoccerOutput gameDataSoccerOutput = GameDataAdapter.get_gameDataAdapterInstance().retrieveGameData(gameDataInputStatsNba);
		printTeamStandingXmlSoccer(gameDataSoccerOutput);
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
		for (GameDataTeamXmlSoccer gameDataTeamXmlSoccer : gameDataSoccerOutput.get_teamListXmlSoccer().values())
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
