package com.doozi.scorena.sportsdata

class GameBaseballHomeRun {
	
	String playerId
	String NameDisplayRoster
	String firstName
	String lastName
	String playerNumber
	
	String teamCode
	String homeRunYtd
	String homeRun
	String inning
	String runners
	
	static belongsTo = [gameBaseball: GameBaseball]
    static constraints = {
    }
	
	static mapping = {
		datasource 'sportsData'
	}
}
