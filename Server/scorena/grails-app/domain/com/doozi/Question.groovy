package com.doozi

class Question {
	String pick1
	String pick2
	
	Pool betPool
	QuestionContent content
	static hasMany = [game: Game, bet: BetTransaction]
	static belongsTo = Game

    static constraints = {
    }
}
