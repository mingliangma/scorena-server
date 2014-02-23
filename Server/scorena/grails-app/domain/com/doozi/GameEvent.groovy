package com.doozi

class GameEvent {
	
	int homeScore
	int awayScore
	
	static belongsTo = [match: Game]
    static constraints = {
    }
}
