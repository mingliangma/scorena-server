/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

/**
 * @author Heng
 *
 */
public class GameDataManager implements IGameDataManager {

	private static final GameDataManager _gameDataManagerInstance = new GameDataManager();
	
	/**
	 * 
	 */
	protected GameDataManager() 
	{
		
	}
	
	public static GameDataManager get_gameDataManagerInstance() 
	{
		return _gameDataManagerInstance;
	}

}
