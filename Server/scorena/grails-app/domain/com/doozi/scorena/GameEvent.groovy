package com.doozi.scorena

class GameEvent {
	
	int homeScore
	int awayScore
	
	static belongsTo = [game: Game]
    static constraints = {
    }
}
