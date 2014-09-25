package com.doozi.scorena.transaction

import java.util.Date;
import com.doozi.scorena.*

abstract class ScorenaTransaction {
	int transactionAmount
	Date createdAt	
	
	static belongsTo = [account: Account]
	
    static constraints = {
    }
}
