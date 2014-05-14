package com.doozi.scorena


class Account {
	String userId
	String username
	int currentBalance	
	int previousBalance
	
	
	//static belongsTo = [user: User]
	static hasMany = [bet: PoolTransaction]
	static constraints = {
		username (unqiue: true)
		bet nullable: true
	}
	static mapping = {
		bet lazy: false
	}
 
}
