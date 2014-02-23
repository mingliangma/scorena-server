package com.doozi

class BetTransaction {
	int wager
	Date time
	boolean pick
	
	static hasOne = [betResult: BetResult]
	static belongsTo = [game: Game, question: Question, user: User]
	
    static constraints = {
		betResult unique: true
    }
}
