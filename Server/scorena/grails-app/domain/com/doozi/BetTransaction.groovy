package com.doozi

class BetTransaction {
	int wager
	Date time
	int pick
	
	
	static hasOne = [betResult: BetResult]
	static belongsTo = [game: Game, question: Question, account: Account]
	
    static constraints = {
		betResult nullable: true,unique: true
    }
}
