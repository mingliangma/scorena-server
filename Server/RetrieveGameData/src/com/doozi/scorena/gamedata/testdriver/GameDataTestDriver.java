/**
 * 
 */
package com.doozi.scorena.gamedata.testdriver;

import com.doozi.scorena.gamedata.helper.GameDataConstants;
import com.doozi.scorena.gamedata.manager.GameDataManager;
import com.doozi.scorena.gamedata.userinput.GameDataInput;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;
import com.doozi.scorena.gamedata.useroutput.GameDataSoccer;
import com.doozi.scorena.gamedata.useroutput.GameDataTeam;

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

	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception 
	{
		GameDataManager gameDataManagerInstance = GameDataManager.get_gameDataManagerInstance();
		GameDataInput gameDataInput = new GameDataInput();
		GameDataOutput gameDataOutput = gameDataManagerInstance.retrieveGameData(gameDataInput);
		//System.out.println(gameDataOutput.get_originalGameData());
		
		for (GameDataTeam gameDataTeam : gameDataOutput.get_teamList().values())
		{
			System.out.println("************************************");
			System.out.println("Ranking: " + gameDataTeam.get_ranking());
			System.out.println("Team ID: " + gameDataTeam.get_teamId());
			System.out.println("Team Name: " + gameDataTeam.get_teamName());
			System.out.println("Played: " + gameDataTeam.get_played());
			System.out.println("Played At Home: " + gameDataTeam.get_playedAtHome());
			System.out.println("Played Away: " + gameDataTeam.get_playedAway());
			System.out.println("Won: " + gameDataTeam.get_won());
			System.out.println("Draw: " + gameDataTeam.get_draw());
			System.out.println("Lost: " + gameDataTeam.get_lost());
			System.out.println("Points: " + gameDataTeam.get_points());
			
			if (gameDataTeam.get_teamType().equalsIgnoreCase(GameDataConstants.gameType_Soccer))
			{
				GameDataSoccer gameDataSoccer = gameDataTeam.get_gameDataSoccer();
				System.out.println("Number of Shots: " + gameDataSoccer.get_numberOfShots());
				System.out.println("Yellow Cards: " + gameDataSoccer.get_yellowCards());
				System.out.println("Red Cards: " + gameDataSoccer.get_redCards());
				System.out.println("Goals For: " + gameDataSoccer.get_goalsFor());
				System.out.println("Goals Against: " + gameDataSoccer.get_goalsAgainst());
				System.out.println("Goal Difference: " + gameDataSoccer.get_goalDifference());
			}
		}
	}
}
