package com.doozi.scorena.transaction
import java.util.Date;

import com.doozi.scorena.Question
import com.doozi.scorena.transaction.AbstractTransaction

class PayoutTransaction extends AbstractTransaction{
		
	String eventKey
	LeagueTypeEnum league
	int pick	
	int winnerPick
	int initialWager
	int playResult
	int profit
	Date gameStartTime
	
	static belongsTo = [question: Question]
	
    static constraints = {
		account (unique: ['class','question'])
    }
}
