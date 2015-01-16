package com.doozi.scorena.score

import java.util.Date;

import com.doozi.scorena.*
import com.doozi.scorena.transaction.LeagueTypeEnum;

class AbstractScore {
	
	int score
	Date createdAt
	String eventKey
	LeagueTypeEnum league
	Date gameStartTime
	
	static belongsTo = [account: Account]
	
    static constraints = {

    }
}
