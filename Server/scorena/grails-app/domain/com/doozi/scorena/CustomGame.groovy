package com.doozi.scorena

import com.doozi.scorena.transaction.LeagueTypeEnum

class CustomGame {
	String fullName
	String teamKey
	String alignment
	String eventStatus
	String score
	String eventKey
	LeagueTypeEnum league
	Date startDateTime
	Date lastUpdate
	
	
    static constraints = {
		score nullable: true
    }
}
