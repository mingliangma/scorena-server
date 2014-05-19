package com.doozi.scorena

class CustomGame {
	String fullName
	String teamKey
	String alignment
	String eventStatus
	String score
	String eventKey
	Date startDateTime
	Date lastUpdate
	
	
    static constraints = {
		score nullable: true
    }
}
