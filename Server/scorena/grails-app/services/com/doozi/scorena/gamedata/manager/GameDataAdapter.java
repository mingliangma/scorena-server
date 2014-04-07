/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;

/**
 * @author Heng
 *
 */
public class GameDataAdapter implements IGameDataAdapter {

	private static final GameDataAdapter _gameDataAdapterInstance = new GameDataAdapter();
	//private Game
	
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
	
	public GameDataOutput retrieveGameData(GameDataInputXmlSoccer gameDataInput) throws Exception
	{
		return null;
	}

}
