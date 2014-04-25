package com.doozi.scorena


//transacitonType:
//		0: buy in
//		1: payout
class PoolTransaction {
	int transactionAmount
	int transactionType
	Date createdAt
	int pick
	
	int pick1Amount
	int pick2Amount
	int pick1NumPeople
	int pick2NumPeople
	
	public static final int BUYIN = 0
	public static final int PAYOUT = 1
	
	static hasOne = [betResult: BetResult]
	static belongsTo = [question: Question, account: Account]
	//static belongsTo = [game: Game, question: Question, account: Account]
	
    static constraints = {
		betResult nullable: true,unique: true
    }
	
	static mapping = {
		sort 'id'
	}
	

}
