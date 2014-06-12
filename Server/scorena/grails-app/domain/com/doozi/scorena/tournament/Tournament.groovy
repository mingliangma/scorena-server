package com.doozi.scorena.tournament

class Tournament {
	String title
	String content
	String type
	String prize
	int status
	String sport
	Date startDate
	Date expireDate
	
	static hasMany = [enrollment: Enrollment]
	
    static constraints = {
    }
}
