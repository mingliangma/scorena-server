package com.doozi.scorena.sportsdata

import java.util.Date;

class PastChampView {

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
		table 'past_champ'
		version false		
		eventKey column : "event_key"
		id generator: 'assigned', name: 'eventKey'
		
	}
}
