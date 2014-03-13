package com.doozi

class GameResult {
	int homeScore
	int awayScore
	
	static belongsTo = [game: Game]
    static constraints = {
    }
}
