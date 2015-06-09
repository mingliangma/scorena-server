package com.doozi.scorena.challenge
import com.doozi.scorena.enums.*

class Challenge {
	ChallengeStatusEnum challengeStatus
	Date createdAt
	Date updatedAt
	
	String challengerUserId
	String challengeeUserId
	String eventKey
	
	ChallengerWinEnum isChallengerWin
	
	
    static constraints = {
    }
	
	static mapping = {
		isChallengerWin defaultValue: ChallengerWinEnum.PENDING
	 }
}
