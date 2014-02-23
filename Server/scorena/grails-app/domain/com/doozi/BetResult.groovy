package com.doozi

class BetResult {
	String gameResult
	int payOut
	
	static belongsTo = [bet: BetTransaction]
	
    static constraints = {
    }
}
