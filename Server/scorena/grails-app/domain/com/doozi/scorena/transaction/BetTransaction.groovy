package com.doozi.scorena.transaction

import com.doozi.scorena.transaction.AbstractTransaction
import com.doozi.scorena.*

class BetTransaction extends AbstractTransaction{
	
	String eventKey
	LeagueTypeEnum league
	int pick
	Date gameStartTime
	static belongsTo = [question: Question]
	
    static constraints = {
		account (unique: ['class','question'])
    }
}
