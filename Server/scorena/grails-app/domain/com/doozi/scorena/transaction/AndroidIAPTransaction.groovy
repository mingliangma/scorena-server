package com.doozi.scorena.transaction

class AndroidIAPTransaction extends AbstractTransaction{
	String productId
	String orderId
	int quantity
	Date purchaseTime
	
    static constraints = {
		account (unique: ['orderId'])
    }
}
