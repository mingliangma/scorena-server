package com.doozi.scorena.sportsdata

class PickLogo {
	String pickName
	String pickLogoUrl
	String logoType
	
	static constraints = {
		logoType nullable: true
	}
	
	static mapping = {
		datasource 'sportsData'
	}
}
