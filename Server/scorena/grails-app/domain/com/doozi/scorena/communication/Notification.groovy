package com.doozi.scorena.communication

import com.doozi.scorena.*

class Notification {
	
	String message
	NotificationTypeEnum notificationType	
	String userId
	Date createdAt
	
	String questionId
	String eventKey
	String tournamentId
    static constraints = {
		eventKey nullable: true
		questionId nullable: true
		tournamentId nullable: true
//		message nullable: true
//		notificationType nullable: true
//		userId nullable: true
//		createdAt nullable: true
    }
}
