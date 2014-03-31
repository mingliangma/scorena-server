package com.doozi.scorena

class BetResult {
	int gameResult
	int payOut
	
	static belongsTo = [bet: BetTransaction]
	
    static constraints = {
    }
}
