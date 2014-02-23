package com.doozi

class GameResult {
	int homeScore
	int awayScore
	
	static belongsTo = [match: Game]
    static constraints = {
    }
}
