package com.doozi.scorena.sportsdata

import java.io.Serializable;
import java.util.Date;

class NbaGames implements Serializable{
	String eventKey
	String fullName
	String teamKey
	String eventStatus
	String alignment
	String score
	Date startDateTime
	Date lastUpdate
	
	static mapping = {
		datasource 'sportsData'
		cache false
	}
	
	static constraints = {
		eventKey unique: true
		score nullable: true		
	}

}
