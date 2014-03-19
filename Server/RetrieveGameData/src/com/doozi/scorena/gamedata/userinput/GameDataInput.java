/**
 * 
 */
package com.doozi.scorena.gamedata.userinput;

import com.doozi.scorena.gamedata.helper.GameDataConstants;

/**
 * @author hengkuang
 *
 */
public class GameDataInput {

	private String _apiKeyValue;
	private String _apiUrl;
	private String _leagueName;
	private String _seasonDate;
	private String _queryString;
	private String _host;
	private String _apiType;
	private String _apiMethod;
	private String _gameType;
	
	/**
	 * 
	 */
	public GameDataInput() 
	{
		_apiUrl = GameDataConstants.defaultApiUrl;
		_apiMethod = GameDataConstants.defaultApiMethod;
		_apiKeyValue = GameDataConstants.defaultApiType;
		_leagueName = GameDataConstants.defaultLeagueName;
		_seasonDate = GameDataConstants.defaultSessionDate;
		_queryString = GameDataConstants.defaultQueryString;
		_host = GameDataConstants.defaultHost;
		_apiType = GameDataConstants.defaultApiType;
		_gameType = GameDataConstants.gameType_Soccer;
	}
	
	public GameDataInput(String apiUrl, String apiMethod, String apiKeyValue, String leagueName, String seasonDate, 
			String queryString, String host, String apiType, String gameType)
	{
		_apiUrl = apiUrl;
		_apiMethod = apiMethod;
		_apiKeyValue = apiKeyValue;
		_leagueName = leagueName;
		_seasonDate = seasonDate;
		_queryString = queryString;
		_host = host;
		_apiType = apiType;
		_gameType = gameType;
	}

	/**
	 * @return the _apiKeyValue
	 */
	public String get_apiKeyValue() {
		return _apiKeyValue;
	}

	/**
	 * @param _apiKeyValue the _apiKeyValue to set
	 */
	public void set_apiKeyValue(String _apiKeyValue) {
		this._apiKeyValue = _apiKeyValue;
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
	 * @return the _leagueName
	 */
	public String get_leagueName() {
		return _leagueName;
	}

	/**
	 * @param _leagueName the _leagueName to set
	 */
	public void set_leagueName(String _leagueName) {
		this._leagueName = _leagueName;
	}

	/**
	 * @return the _seasonDate
	 */
	public String get_seasonDate() {
		return _seasonDate;
	}

	/**
	 * @param _seasonDate the _seasonDate to set
	 */
	public void set_seasonDate(String _seasonDate) {
		this._seasonDate = _seasonDate;
	}

	/**
	 * @return the _queryString
	 */
	public String get_queryString() {
		return _queryString;
	}

	/**
	 * @param _queryString the _queryString to set
	 */
	public void set_queryString(String _queryString) {
		this._queryString = _queryString;
	}

	/**
	 * @return the _host
	 */
	public String get_host() {
		return _host;
	}

	/**
	 * @param _host the _host to set
	 */
	public void set_host(String _host) {
		this._host = _host;
	}

	/**
	 * @return the _apiType
	 */
	public String get_apiType() {
		return _apiType;
	}

	/**
	 * @param _apiType the _apiType to set
	 */
	public void set_apiType(String _apiType) {
		this._apiType = _apiType;
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

	public String get_gameType() {
		return _gameType;
	}

	public void set_gameType(String _gameType) {
		this._gameType = _gameType;
	}

}
