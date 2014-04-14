package com.doozi.scorena.sportsdata

import java.util.Date;

class UpcomingChampView {
	String eventKey
	String fullName
	String teamKey
	String eventStatus
	String alignment
	Date startDateTime
	Date lastUpdate

	
	static mapping = {
		datasource 'sportsData'
		table 'upcoming_7_champ'
		version false
		eventKey column : "event_key"
		id generator: 'assigned', name: 'eventKey'
		
	}
    static constraints = {
    }
}
