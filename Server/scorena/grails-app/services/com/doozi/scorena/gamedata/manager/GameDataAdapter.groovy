/**
 * 
 */
package com.doozi.scorena.gamedata.manager;

import com.doozi.scorena.gamedata.userinput.GameDataInputDeluxe;
import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlSoccer;
import com.doozi.scorena.gamedata.userinput.GameDataInputXmlTeam;
import com.doozi.scorena.gamedata.userinput.IGameDataInput;
import com.doozi.scorena.gamedata.useroutput.IGameDataOutput
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataSoccerOutput;

/**
 * @author Heng
 *
 */
public class GameDataAdapter implements IGameDataAdapter {

	private static final GameDataAdapter _gameDataAdapterInstance = new GameDataAdapter();
	private GameDataManagerXmlSoccer _gameDataManagerXmlSoccer = GameDataManagerXmlSoccer.get_gameDataManagerXmlSoccerInstance();
	private GameDataManagerXmlTeam _gameDataManagerXmlTeam = GameDataManagerXmlTeam.get_gameDataManagerXmlTeamInstance();
	private GameDataManagerDeluxe _gameDataManagerDeluxe = GameDataManagerDeluxe.get_gameDataManagerDeluxeInstance();
	private GameDataManagerStatsNba _gameDataManagerStatsNba = GameDataManagerDeluxe.get_gameDataManagerDeluxeInstance();
	
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
	
	public IGameDataOutput retrieveGameData(IGameDataInput gameDataInput) throws Exception
	{
		if (gameDataInput instanceof GameDataInputXmlSoccer)
		{
			return _gameDataManagerXmlSoccer.retrieveGameData((GameDataInputXmlSoccer)gameDataInput);
		}
		else if (gameDataInput instanceof GameDataInputXmlTeam)
		{
			return _gameDataManagerXmlTeam.retrieveGameData((GameDataInputXmlTeam)gameDataInput);
		}
		else if (gameDataInput instanceof GameDataInputDeluxe)
		{
			return _gameDataManagerDeluxe.retrieveGameData((GameDataInputDeluxe)gameDataInput);
		}
		else if (gameDataInput instanceof GameDataInputStatsNba)
		{
			return _gameDataManagerStatsNba.retrieveGameData((GameDataInputStatsNba)gameDataInput);
		}
		else
		{
			return null;
		}
	}
}
