package com.doozi.scorena
import com.doozi.scorena.transaction.*;
class Question {
	
	String eventKey
	String pick1
	String pick2
	
	static hasOne = [pool: Pool]
	static hasMany = [betTrans: BetTransaction, payoutTrans: PayoutTransaction]
	static belongsTo = [questionContent:QuestionContent]
	
	static mapping = {
		//bet lazy: false
	}
}
