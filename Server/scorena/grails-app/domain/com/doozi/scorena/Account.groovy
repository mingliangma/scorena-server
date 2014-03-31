package com.doozi.scorena


class Account {
	String userId
	String username
	int currentBalance
	
	int previousBalance
	
	
	//static belongsTo = [user: User]
	static hasMany = [bet: BetTransaction]
	static constraints = {
		username (unqiue: true)
		bet nullable: true
	}
 
}
