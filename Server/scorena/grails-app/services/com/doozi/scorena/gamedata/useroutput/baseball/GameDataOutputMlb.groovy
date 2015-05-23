package com.doozi.scorena.gamedata.useroutput.baseball

import java.util.Map;

import com.doozi.scorena.gamedata.useroutput.IGameDataOutput
import com.doozi.scorena.gamedata.useroutput.basketball.GameDataEventStatsNba;

class GameDataOutputMlb implements IGameDataOutput {
	private Map _originalGameData = [:]
	private Map<String, GameDataEventMlb> _eventListMlb = [:]
	
	public GameDataOutputMlb(originalGameData){
		_originalGameData = originalGameData
	}

}
