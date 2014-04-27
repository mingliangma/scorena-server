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
	String score
	
    static constraints = {}
	
	static mapping = 
	{
		datasource 'sportsData'
		table 'upcoming_realtime_epl'
		version false		
		eventKey column : "event_key"
		id composite: ['eventKey', 'teamKey']
	}
}
