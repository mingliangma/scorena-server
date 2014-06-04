package com.doozi.scorena


class Account {
	String userId
	String username
	int currentBalance	
	int previousBalance
	
	
	//static belongsTo = [user: User]
	static hasMany = [bet: PoolTransaction]
	static constraints = {
		userId unqiue: true
		bet nullable: true
	}
	
	static mapping = {	
	}
 
}
