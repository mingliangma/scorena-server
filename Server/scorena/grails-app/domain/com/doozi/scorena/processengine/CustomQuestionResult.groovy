package com.doozi.scorena

class CustomQuestionResult {
	String eventKey
	int questionId
	int winnerPick
	
    static constraints = {
		questionId unique: true
    }
}
