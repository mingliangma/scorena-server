package com.doozi.scorena

class Pool {
	
	int pick1Amount
	int pick2Amount
	int pick1NumPeople
	int pick2NumPeople
	int minBet
	
	
	static belongsTo = [question: Question]
		
    static constraints = {
		question unique: true
    }
}
