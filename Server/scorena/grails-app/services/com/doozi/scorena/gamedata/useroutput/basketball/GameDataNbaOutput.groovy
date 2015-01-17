package com.doozi.scorena.gamedata.useroutput.basketball;

import java.util.LinkedHashMap;

import com.doozi.scorena.gamedata.useroutput.IGameDataOutput
import com.doozi.scorena.gamedata.useroutput.soccer.GameDataTeamXmlSoccer;

public class GameDataNbaOutput implements IGameDataOutput{

	private Map _originalGameData = [:];
	private Map<String, GameDataEventStatsNba> _eventListStatsNba = [:]
	
	public GameDataNbaOutput(originalGameData){
		_originalGameData = originalGameData
	}
}
