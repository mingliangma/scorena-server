package com.doozi.scorena.sportsdata

import java.io.Serializable;

class UpcomingEplView implements Serializable{
	String eventKey	
	String fullName
	String teamKey
	String eventStatus
	String alignment
	Date startDateTime
	Date lastUpdate
	
    static constraints = {
    }
	
	static mapping = {
		datasource 'sportsData'
		table 'upcoming_7_epl'
		version false		
		eventKey column : "event_key"
		id composite: ['eventKey', 'teamKey']
		
	}
}
