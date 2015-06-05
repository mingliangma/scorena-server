package com.doozi.scorena.sportsdata

import java.util.Date;

import com.doozi.scorena.transaction.LeagueTypeEnum;

abstract class GameAbstract {

	String eventKey
	String fullName
	String teamKey
	String eventStatus
	String alignment
	String score
	Date startDateTime
	Date lastUpdate
	LeagueTypeEnum league
	boolean autoQuestionCreation
	
    static constraints = {
		score nullable: true
		
    }
	
	static mapping = {
		tablePerHierarchy false
		autoQuestionCreation  defaultValue: false
		datasource 'sportsData'
	}
}
