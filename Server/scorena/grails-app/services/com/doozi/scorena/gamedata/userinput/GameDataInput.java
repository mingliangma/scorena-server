/**
 * 
 */
package com.doozi.scorena.gamedata.userinput;

/**
 * @author Heng
 *
 */
public class GameDataInput {

	private String _gameDataSource;
	
	/**
	 * 
	 */
	public GameDataInput(String gameDataSource) 
	{
		_gameDataSource = gameDataSource;
	}

	/**
	 * @return the _gameDataSource
	 */
	public String get_gameDataSource() {
		return _gameDataSource;
	}

	/**
	 * @param _gameDataSource the _gameDataSource to set
	 */
	public void set_gameDataSource(String _gameDataSource) {
		this._gameDataSource = _gameDataSource;
	}

}
