package com.doozi.scorena.sportsdata

class NbaTeams {
	
	String scorenaTeamId
	String statsNbaTeamId
	String teamName
	String teamCityName
	String teamAbbreviation
	
	static mapping = {
		datasource 'sportsData'
	}
    static constraints = {
    }
}
