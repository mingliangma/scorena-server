package com.doozi.scorena.sportsdata

class GameBaseballHomeRun {
	
	String eventKey
	String playerId
	String nameDisplayRoster
	String firstName
	String lastName
	String playerNumber
	
	String teamCode
	int homeRunYtd
	int homeRun
	int inning
	int runners
	
    static constraints = {
		eventKey (unique: ['playerId'])
    }
	
	static mapping = {
		datasource 'sportsData'
	}
	
	
}
