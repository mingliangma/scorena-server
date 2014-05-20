package com.doozi.scorena

import java.io.Serializable;

class UserLeagueStats implements Serializable {
	int netGain
	int numGames
	String gameResult
	String league
	int accountId
	
	static mapping = {
		table 'user_league_stats'
		version false
		id composite: ['accountId', 'league', 'gameResult']
	}
    static constraints = {
    }
}
