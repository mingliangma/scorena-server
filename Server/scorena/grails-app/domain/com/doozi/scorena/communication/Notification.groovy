package com.doozi.scorena.communication

import com.doozi.scorena.*

class Notification {
	
	String message
	NotificationTypeEnum notificationType	
	String userId // receiver's UserId
	Date createdAt
	
	String questionId
	String eventKey
	String tournamentId
	String challengeId
    static constraints = {
		eventKey nullable: true
		questionId nullable: true
		tournamentId nullable: true
		challengeId nullable: true
//		message nullable: true
//		notificationType nullable: true
//		userId nullable: true
//		createdAt nullable: true
    }
}
