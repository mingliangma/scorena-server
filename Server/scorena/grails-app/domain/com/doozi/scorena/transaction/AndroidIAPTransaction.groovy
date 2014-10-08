package com.doozi.scorena.transaction

class AndroidIAPTransaction extends ScorenaTransaction{
	String productId
	String orderId
	int quantity
	Date purchaseTime
	
    static constraints = {
    }
}
