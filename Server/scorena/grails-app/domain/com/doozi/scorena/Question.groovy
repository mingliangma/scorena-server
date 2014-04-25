package com.doozi.scorena

class Question {
	
	String eventKey
	String pick1
	String pick2
	
	static hasOne = [pool: Pool]
	static hasMany = [bet: PoolTransaction]
	static belongsTo = [questionContent:QuestionContent]

    static constraints = {
    }
}
