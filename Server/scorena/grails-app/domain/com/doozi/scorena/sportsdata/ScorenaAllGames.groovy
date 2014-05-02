package com.doozi.scorena.sportsdata

import java.io.Serializable;
import java.util.Date;

class ScorenaAllGames  implements Serializable{
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
		table 'scorena_all_games'
		version false
		eventKey column : "event_key"
		id composite: ['eventKey', 'teamKey']		
	}
	
    static constraints = {
    }
	
	
}
