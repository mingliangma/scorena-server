package com.doozi

class Game {
	String type
	String home
	String away
	String league
	String country
	Date date
	
	static hasOne = [matchEvent: GameEvent, matchResult:GameResult]
	static hasMany = [question: Question, bet: BetTransaction]
	
	
    static constraints = {
		matchEvent unique: true
		matchResult unique: true
    }
}
