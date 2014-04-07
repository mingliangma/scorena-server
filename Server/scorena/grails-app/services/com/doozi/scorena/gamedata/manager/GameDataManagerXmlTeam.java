/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

import com.doozi.scorena.gamedata.userinput.GameDataInputXmlTeam;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;

/**
 * @author Heng
 *
 */
public class GameDataManagerXmlTeam extends GameDataManager {

	private static final GameDataManagerXmlTeam _gameDataManagerXmlTeamInstance = new GameDataManagerXmlTeam();
	
	/**
	 * 
	 */
	private GameDataManagerXmlTeam() 
	{
		
	}
	
	public static GameDataManagerXmlTeam get_gameDataManagerXmlTeamInstance() 
	{
		return _gameDataManagerXmlTeamInstance;
	}
	
	public GameDataOutput retrieveGameData(GameDataInputXmlTeam gameDataInputXmlTeam)
	{
		return null;
	}

}
