package com.doozi.scorena.sportsdata

import java.util.Date;

class PastEplView implements Serializable{

	String eventKey	
	String fullName
	String teamKey
	String eventStatus
	String alignment
	String score
	Date startDateTime
	Date lastUpdate
	
    static constraints = {
    }
	
	static mapping = {
		datasource 'sportsData'
		table 'past_epl'
		version false		
		eventKey column : "event_key"
		id composite: ['eventKey', 'teamKey']
		//id generator: 'assigned', name: 'eventKey'
		
	}
}
