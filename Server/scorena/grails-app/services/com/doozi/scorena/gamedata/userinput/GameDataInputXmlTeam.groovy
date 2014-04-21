/**
 * 
 */
package com.doozi.scorena.gamedata.userinput;

import com.doozi.scorena.gamedata.helper.GameDataConstantsXmlTeam;

/**
 * @author Heng
 *
 */
public class GameDataInputXmlTeam implements IGameDataInput {

	private String _apiUrl;
	private String _apiMethod;
	private String _gameType;
	
	/**
	 * 
	 */
	public GameDataInputXmlTeam() 
	{
		initializeGameDataInputForXmlTeam(GameDataConstantsXmlTeam.defaultApiUrl, 
				GameDataConstantsXmlTeam.defaultApiMethod, GameDataConstantsXmlTeam.gameType_Soccer);
	}
	
	public GameDataInputXmlTeam(String apiUrl, String apiMethod, String gameType)
	{
		initializeGameDataInputForXmlTeam(apiUrl, apiMethod, gameType);
	}
	
	public void initializeGameDataInputForXmlTeam(String apiUrl, String apiMethod, String gameType)
	{
		_apiUrl = apiUrl;
		_apiMethod = apiMethod;
		_gameType = gameType;
	}

	/**
	 * @return the _apiUrl
	 */
	public String get_apiUrl() {
		return _apiUrl;
	}

	/**
	 * @param _apiUrl the _apiUrl to set
	 */
	public void set_apiUrl(String _apiUrl) {
		this._apiUrl = _apiUrl;
	}

	/**
	 * @return the _apiMethod
	 */
	public String get_apiMethod() {
		return _apiMethod;
	}

	/**
	 * @param _apiMethod the _apiMethod to set
	 */
	public void set_apiMethod(String _apiMethod) {
		this._apiMethod = _apiMethod;
	}

	/**
	 * @return the _gameType
	 */
	public String get_gameType() {
		return _gameType;
	}

	/**
	 * @param _gameType the _gameType to set
	 */
	public void set_gameType(String _gameType) {
		this._gameType = _gameType;
	}
}
