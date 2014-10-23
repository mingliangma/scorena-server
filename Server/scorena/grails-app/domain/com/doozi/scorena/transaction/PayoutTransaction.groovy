package com.doozi.scorena.transaction
import com.doozi.scorena.LeagueTypeEnum;
import com.doozi.scorena.Question
import com.doozi.scorena.transaction.ScorenaTransaction

class PayoutTransaction extends ScorenaTransaction{
		
	String eventKey
	LeagueTypeEnum league
	int pick	
	int winnerPick
	int initialWager
	int playResult
	
	static belongsTo = [question: Question]
	
    static constraints = {
    }
}
