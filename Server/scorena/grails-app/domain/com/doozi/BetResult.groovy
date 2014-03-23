package com.doozi

class BetResult {
	int gameResult
	int payOut
	
	static belongsTo = [bet: BetTransaction]
	
    static constraints = {
    }
}
