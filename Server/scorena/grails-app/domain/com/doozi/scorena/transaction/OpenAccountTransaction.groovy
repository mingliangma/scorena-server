package com.doozi.scorena.transaction

class OpenAccountTransaction extends AbstractTransaction{
	
	int accountType
    static constraints = {
		account (unique: ['class', 'accountType'])
    }
}
