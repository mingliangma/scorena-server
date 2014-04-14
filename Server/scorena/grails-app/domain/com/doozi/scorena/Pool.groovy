package com.doozi.scorena

class Pool {
	
	
	int minBet
	
	
	static belongsTo = [question: Question]
		
    static constraints = {
		question unique: true
    }
}
