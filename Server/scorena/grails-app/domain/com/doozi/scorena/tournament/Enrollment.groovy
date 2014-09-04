package com.doozi.scorena.tournament
import com.doozi.scorena.Account

class Enrollment {
	Date enrollmentDate
	
	static belongsTo = [tournament: Tournament, account: Account]
	
    static constraints = {
    }
}
