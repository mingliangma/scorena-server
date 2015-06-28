package com.doozi.scorena.challenge
import com.doozi.scorena.enums.*

class Challenge {
	ChallengeStatusEnum challengeStatus
	Date createdAt
	Date updatedAt
	
	String challengerUserId
	String challengeeUserId
	String eventKey
	
	ChallengeResultStatusEnum challengerResultStatus
	
	
    static constraints = {
    }
	
	static mapping = {
		challengerResultStatus defaultValue: ChallengeResultStatusEnum.PENDING
	 }
}
