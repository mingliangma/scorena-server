package com.doozi


class Account {
	int currentBalance
	//String latestTransaction
	int previousBalance
	//Date time
	
	static belongTo = [user: User]
	
    static constraints = {
    }
}
