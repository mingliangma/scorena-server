package com.doozi.scorena

class BetResult {
	int betResult
	int payOut
	
	static belongsTo = [bet: BetTransaction]
	
    static constraints = {
    }
}
