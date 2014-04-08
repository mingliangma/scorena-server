/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlTeam;
import com.doozi.scorena.gamedata.userinput.IGameDataInput;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;

/**
 * @author Heng
 *
 */
public class GameDataAdapter implements IGameDataAdapter {

	private static final GameDataAdapter _gameDataAdapterInstance = new GameDataAdapter();
	private GameDataManagerXmlSoccer _gameDataManagerXmlSoccer = GameDataManagerXmlSoccer.get_gameDataManagerXmlSoccerInstance();
	private GameDataManagerXmlTeam _gameDataManagerXmlTeam = GameDataManagerXmlTeam.get_gameDataManagerXmlTeamInstance();
	
	/**
	 * 
	 */
	private GameDataAdapter() 
	{
		
	}
	
	public static GameDataAdapter get_gameDataAdapterInstance() 
	{
		return _gameDataAdapterInstance;
	}
	
	public GameDataOutput retrieveGameData(IGameDataInput gameDataInput) throws Exception
	{
		if (gameDataInput instanceof GameDataInputXmlSoccer)
		{
			return _gameDataManagerXmlSoccer.retrieveGameData((GameDataInputXmlSoccer)gameDataInput);
		}
		else if (gameDataInput instanceof GameDataInputXmlTeam)
		{
			return _gameDataManagerXmlTeam.retrieveGameData((GameDataInputXmlTeam)gameDataInput);
		}
		else
		{
			return null;
		}
	}

}
