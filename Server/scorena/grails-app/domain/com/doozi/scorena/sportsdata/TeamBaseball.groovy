package com.doozi.scorena.sportsdata

class TeamBaseball {
	
	String teamId //home_team_id
	String teamCityName //home_team_city
	String teamAbbreviation //home_name_abbrev
	String clubName //home_team_name
	String win //home_win
	String loss // home_loss
	String mlbLeague
	String teamLogoUrl
	
    static constraints = {
		win nullable: true
		loss nullable: true
		teamId unique: true
    }
	
	static mapping = {
		datasource 'sportsData'
	}
}
