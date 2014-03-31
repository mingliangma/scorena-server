/**
 * 
 */
package com.doozi.scorena.gamedata.useroutput;

import java.util.LinkedHashMap;

/**
 * @author hengkuang
 *
 */
public class GameDataOutput {

	private LinkedHashMap<String, GameDataTeam> _teamList = new LinkedHashMap<String, GameDataTeam>();
	private String _originalGameData;
	
	public GameDataOutput(String originalGameData)
	{
		_originalGameData = originalGameData;
	}
	
	/**
	 * 
	 */
	public GameDataOutput(LinkedHashMap<String, GameDataTeam> teamList) 
	{
		_teamList = teamList;
	}

	/**
	 * @return the _teamList
	 */
	public LinkedHashMap<String, GameDataTeam> get_teamList() {
		return _teamList;
	}

	/**
	 * @param _teamList the _teamList to set
	 */
	public void set_teamList(LinkedHashMap<String, GameDataTeam> _teamList) {
		this._teamList = _teamList;
	}

	/**
	 * @return the _originalGameData
	 */
	public String get_originalGameData() {
		return _originalGameData;
	}

	/**
	 * @param _originalGameData the _originalGameData to set
	 */
	public void set_originalGameData(String _originalGameData) {
		this._originalGameData = _originalGameData;
	}

}
