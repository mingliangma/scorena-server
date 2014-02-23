package com.doozi

class Pool {
	
	int pick1Amount
	int pick2Amount
	int pick1NumPeople
	int pick2NumPeople
	
	static belongsTo = [question: Question]
		
    static constraints = {
		question unique: true
    }
}
