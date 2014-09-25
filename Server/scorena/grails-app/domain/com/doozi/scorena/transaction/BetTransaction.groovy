package com.doozi.scorena.transaction

import com.doozi.scorena.transaction.ScorenaTransaction
import com.doozi.scorena.*

class BetTransaction extends ScorenaTransaction{
	
	String eventKey
	int pick
	static belongsTo = [question: Question]
	
    static constraints = {
    }
}
