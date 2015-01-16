package com.doozi.scorena.processengine

class CustomQuestionResult {
	String eventKey
	int questionId
	int winnerPick
	
    static constraints = {
		questionId unique: true
    }
}
