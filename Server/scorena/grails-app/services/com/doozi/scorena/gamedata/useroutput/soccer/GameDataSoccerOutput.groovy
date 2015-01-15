/**
 * 
 */
package com.doozi.scorena.gamedata.useroutput.soccer;

import com.doozi.scorena.gamedata.useroutput.IGameDataOutput
import java.util.LinkedHashMap;

/**
 * @author hengkuang
 *
 */
public class GameDataSoccerOutput implements IGameDataOutput{

	private LinkedHashMap<String, GameDataTeamXmlSoccer> _teamListXmlSoccer = new LinkedHashMap<String, GameDataTeamXmlSoccer>();
	private String _originalGameData;
	
	public GameDataSoccerOutput(String originalGameData)
	{
		_originalGameData = originalGameData;
	}
	
	/**
	 * 
	 */
	public GameDataSoccerOutput(LinkedHashMap<String, GameDataTeamXmlSoccer> teamList) 
	{
		_teamList = teamList;
	}

	/**
	 * @return the _teamListXmlSoccer
	 */
	public LinkedHashMap<String, GameDataTeamXmlSoccer> get_teamListXmlSoccer() {
		return _teamListXmlSoccer;
	}

	/**
	 * @param _teamListXmlSoccer the _teamListXmlSoccer to set
	 */
	public void set_teamListXmlSoccer(LinkedHashMap<String, GameDataTeamXmlSoccer> _teamListXmlSoccer) {
		this._teamListXmlSoccer = _teamListXmlSoccer;
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
