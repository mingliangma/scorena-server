package com.doozi.scorena.gamedata.userinput

import com.doozi.scorena.gamedata.manager.IGameDataAdapter
import com.doozi.scorena.gamedata.helper.GameDataConstantsMlb
import groovy.time.TimeCategory


class GameDataInputMlb implements IGameDataInput {
	
	private String _year
	private String _month
	private String _day
	
	public String _apiUrl;
	
	public GameDataInputMlb(int offset){
		Date gameDate = new Date()
		use( TimeCategory ) {
			gameDate = gameDate - 10.hours // adjust to EST (-5 hours) and -5
		}
		
		gameDate = gameDate.plus(offset)
		
		println "gameDate="+gameDate
		_year = gameDate.format('yyyy')
		_month = gameDate.format('MM')
		_day = gameDate.format('dd')
		
		println "_year="+_year
		_apiUrl = getApiUrl(_year, _month, _day)
	}
	
	
	String getApiUrl(String year, String month, String day){
		return GameDataConstantsMlb.defaultHost + GameDataConstantsMlb.defaultUrlPath + GameDataConstantsMlb.YEAR_PREFIX + year +
		GameDataConstantsMlb.MONTH_PREFIX+ month + GameDataConstantsMlb.DAY_PREFIX+ day + GameDataConstantsMlb.defaultScoreFileName

	}
}
