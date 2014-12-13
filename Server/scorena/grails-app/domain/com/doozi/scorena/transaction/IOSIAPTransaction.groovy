package com.doozi.scorena.transaction

import java.util.Date;

class IOSIAPTransaction extends AbstractTransaction{
	// quantity of product
	int quantity;
	// product Id
	String productId;	
	// transaction Id
	String transactionId	
	Date purchaseTime
	
    static constraints = {
		account (unique: ['class','transactionId'])
    }
}
