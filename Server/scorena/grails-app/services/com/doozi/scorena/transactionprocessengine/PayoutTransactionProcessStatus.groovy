package com.doozi.scorena.transactionprocessengine


//0 = not processed
//1 = in process
//2 = process completed
//3 = process completed with unprocessed custom questions
//-1 = process completed with Error
//-2 = process completed with Error and unprocessed custom questions

class PayoutTransactionProcessStatus {
	public static final int NOT_PROCESSED = 0
	public static final int IN_PROCESS = 1
	public static final int COMPLETED = 2
	public static final int CUSOTM_QUESTION_UNPROCESSED = 3
	public static final int ERROR = -1
	public static final int ERROR_WITH_UNPROCCESSED_CUSTOM_QUESTION = -2
	
}
