package com.doozi.scorena.transaction

import java.util.Date;
import com.doozi.scorena.*

abstract class AbstractTransaction {
	int transactionAmount
	Date createdAt	
	
	static belongsTo = [account: Account]
	
    static constraints = {
    }
	
	static mapping = {
//		account joinTable: [name: 'TRANS_ACCOUNT', key: 'account_id']
	}
}
