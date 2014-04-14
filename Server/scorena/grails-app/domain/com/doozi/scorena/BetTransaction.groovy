package com.doozi.scorena

class BetTransaction {
	int wager
	Date createdAt
	int pick
	int pick1Amount
	int pick2Amount
	int pick1NumPeople
	int pick2NumPeople
	
	
	static hasOne = [betResult: BetResult]
	static belongsTo = [game: Game, question: Question, account: Account]
	
    static constraints = {
		betResult nullable: true,unique: true
    }
}
