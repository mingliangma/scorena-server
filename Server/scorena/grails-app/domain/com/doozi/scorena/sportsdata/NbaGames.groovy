package com.doozi.scorena.sportsdata

import java.io.Serializable;
import java.util.Date;

class NbaGames implements Serializable{
	String eventKey
	String statsNbaGameId
	String fullName
	String teamKey
	String eventStatus
	String alignment
	String score
	String fieldGoalsPercentage
	String freeThrowPercentage
	String threePointersPercentage
	String assists
	String rebounds
	String turnovers
	Date startDateTime
	Date lastUpdate
	
	static mapping = {
		datasource 'sportsData'
		cache false
	}
	
	static constraints = {
		score nullable: true		
	}

}
