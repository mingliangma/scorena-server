package com.doozi.scorena

class Question {
	
	String eventId
	String pick1
	String pick2
	String content
//	int sport
//	int type
	
	
	
	static hasOne = [pool: Pool]
	static hasMany = [bet: BetTransaction]
	//static belongsTo = [game: Game]

    static constraints = {
    }
}
