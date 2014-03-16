package com.doozi


class Account {
	int currentBalance
	//String latestTransaction
	int previousBalance
	//Date time
	
	static belongsTo = [user: User]
	
    static constraints = {
		user unqiue: true
    }
}
