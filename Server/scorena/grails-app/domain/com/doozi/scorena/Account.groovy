package com.doozi.scorena
import com.doozi.scorena.tournament.*

class Account {
	String userId
	String username
	int currentBalance	
	int previousBalance
	
	
	//static belongsTo = [user: User]
	static hasMany = [bet: PoolTransaction, enrollment: Enrollment]

	static constraints = {
		userId unqiue: true
		bet nullable: true
	}
	
	static mapping = {	
	}
 
}
