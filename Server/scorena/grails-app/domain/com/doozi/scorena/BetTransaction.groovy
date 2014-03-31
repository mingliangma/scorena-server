package com.doozi.scorena

class BetTransaction {
	int wager
	Date createdAt
	int pick
	
	
	static hasOne = [betResult: BetResult]
	static belongsTo = [game: Game, question: Question, account: Account]
	
    static constraints = {
		betResult nullable: true,unique: true
    }
}
