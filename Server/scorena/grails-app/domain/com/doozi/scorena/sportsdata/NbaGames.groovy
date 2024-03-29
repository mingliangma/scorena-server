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
	
	String statsNbaGameId
	String fieldGoalsPercentage
	String freeThrowPercentage
	String threePointersPercentage
	String assists
	String rebounds
	String turnovers
	static mapping = {
		datasource 'sportsData'
		cache false
	}
	
	static constraints = {
		score nullable: true	
		fieldGoalsPercentage nullable: true
		freeThrowPercentage nullable: true
		threePointersPercentage nullable: true
		assists nullable: true
		rebounds nullable: true
		turnovers nullable: true
	}

}
