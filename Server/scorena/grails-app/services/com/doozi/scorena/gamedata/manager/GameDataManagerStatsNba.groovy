package com.doozi.scorena.gamedata.manager

import com.doozi.scorena.gamedata.userinput.GameDataInputStatsNba
import com.doozi.scorena.gamedata.useroutput.basketball.*

import grails.plugins.rest.client.RestBuilder

class GameDataManagerStatsNba implements IGameDataManager {
	private static final GameDataManagerStatsNba _gameDataManagerStatsNbaInstance = new GameDataManagerStatsNba()
	
	public static GameDataManagerStatsNba get_gameDataManagerStatsNbaInstance()
	{
		return _gameDataManagerStatsNbaInstance
	}
	
	public GameDataNbaOutput retrieveGameData(GameDataInputStatsNba gameDataInputStatsNba) throws Exception
	{
		RestBuilder rest = new RestBuilder()
		def resp = rest.get(gameDataInputStatsNba._apiUrl)
		log.info "retrieveGameData(): ends with resp = ${resp}"
		return resp
	}
}
