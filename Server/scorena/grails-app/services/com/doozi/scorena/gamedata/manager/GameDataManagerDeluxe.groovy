/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

import com.doozi.scorena.gamedata.userinput.GameDataInputDeluxe;
import com.doozi.scorena.gamedata.useroutput.GameDataOutput;

/**
 * @author Heng
 *
 */
public class GameDataManagerDeluxe implements IGameDataManager {

	private static final GameDataManagerDeluxe _gameDataManagerDeluxeInstance = new GameDataManagerDeluxe();
	
	/**
	 * 
	 */
	private GameDataManagerDeluxe() 
	{
		
	}
	
	public static GameDataManagerDeluxe get_gameDataManagerDeluxeInstance() 
	{
		return _gameDataManagerDeluxeInstance;
	}
	
	public GameDataOutput retrieveGameData(GameDataInputDeluxe gameDataInputDeluxe)
	{
		return null;
	}

}
