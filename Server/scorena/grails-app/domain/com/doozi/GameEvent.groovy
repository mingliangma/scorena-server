package com.doozi

class GameEvent {
	
	int homeScore
	int awayScore
	
	static belongsTo = [game: Game]
    static constraints = {
    }
}
