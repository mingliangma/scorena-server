package com.doozi.scorena.transaction
import com.doozi.scorena.Question
import com.doozi.scorena.transaction.ScorenaTransaction

class PayoutTransaction extends ScorenaTransaction{
		
	String eventKey
	int pick
	
	String winnerPick
	int initialWager
	
	static belongsTo = [question: Question]
	
    static constraints = {
    }
}
