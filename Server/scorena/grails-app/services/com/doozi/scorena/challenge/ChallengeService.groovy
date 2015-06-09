package com.doozi.scorena.challenge

import java.util.Map;

import org.springframework.transaction.annotation.Transactional

import com.doozi.scorena.Account
import com.doozi.scorena.enums.*

@Transactional
class ChallengeService {

    def createChallenge(String challengerUserId, String challengeeUserId, String gameId) {
		log.info "createChallenge() begins with challenger=${challengerUserId}, challengee=${challengeeUserId}, "+
		"gameId=${gameId}" 
		Account challengeeAccount = Account.findByUserId(challengeeUserId)
		
		if (!challengeeAccount){
			throw new RuntimeException("challengeeAccount does not exist")
		}
		
		Date now = new Date()
		Challenge challenge = new Challenge(
				challengeStatus: ChallengeStatusEnum.INVITED,
				challengerUserId: challengerUserId,
				challengeeUserId: challengeeUserId,
				createdAt: now,
				updatedAt: now,
				eventKey: gameId,
				isChallengerWin: ChallengerWinEnum.PENDING
			)
		
		if (!challenge.save()){
			challenge.errors.each {
				println it
			}
		}
		
		Map result = [challengeId: challenge.id,
			challengeeUserId: challengeeAccount.userId,
			challengeeDisplayName: challengeeAccount.displayName,
			challengeePictureUrl: challengeeAccount.pictureUrl,
			challengeeAvatarCode: challengeeAccount.avatarCode,
			eventKey: gameId]
		return result
    }
	
	def acceptChallenge(Challenge challenge){
		challenge.challengeStatus = ChallengeStatusEnum.ACCEPTED
		if (!challenge.save()){
			challenge.errors.each {
				println it
			}
			return [error: "challenge updated failed"]
		}else{
			return [:]
		}
	}
	
	def listGameChallenges(String gameId, String userId){
		List challenges = constructGameChallengesResponse(gameId, userId)
		return challenges
	}
	
	public List constructGameChallengesResponse(String gameId, String userId){
		log.info "constructChallengesResponse() begin with gameId=${gameId}, userId=${userId}"
		List challangeResponse = []
		
		List<Challenge> challenges = Challenge.findAll("from Challenge where eventKey=(:gameId) and "+
			"(challengeeUserId=(:userId) or challengerUserId=(:userId))",
			[gameId:gameId, userId:userId])
		
		if (challenges){
			
			List<Map> invitationReceived = []
			List<Map> invitationSent = []
			List<Map> active =[]
			
			for (Challenge challenge: challenges){
				if (challenge.challengeStatus == ChallengeStatusEnum.INVITED){
					if (challenge.challengerUserId == userId){
						Account challengeeAccount = Account.findByUserId(challenge.challengeeUserId)
						Map challengeMap = [userId: challengeeAccount.userId, name: challengeeAccount.displayName,
							pictureURL: challengeeAccount.pictureUrl, avatarCode:challengeeAccount.avatarCode,
							isChallengerWin: challenge.isChallengerWin.toString(), challengeStatus:"INVITATION_SENT"]
						
						invitationSent.add(challengeMap)
					}else{
						Account challengerAccount = Account.findByUserId(challenge.challengerUserId)
						Map challengeMap = [userId: challengerAccount.userId, name: challengerAccount.displayName,
							pictureURL: challengerAccount.pictureUrl, avatarCode:challengerAccount.avatarCode,
							isChallengerWin: challenge.isChallengerWin.toString(), challengeStatus:"INVITATION_RECEIVED"]
						
						invitationReceived.add(challengeMap)
					}
				}else{ // active challenge
					
					String opponentUserId = ""
					if (challenge.challengerUserId == userId)
						opponentUserId=challenge.challengeeUserId
					else{
						opponentUserId=challenge.challengerUserId
					}
					
					Account opponentAccount = Account.findByUserId(opponentUserId)
					Map challengeMap = [userId: opponentAccount.userId, name: opponentAccount.displayName,
						pictureURL: opponentAccount.pictureUrl, avatarCode:opponentAccount.avatarCode,
						isChallengerWin: challenge.isChallengerWin.toString(), challengeStatus:"ACTIVE"]
					
					active.add(challengeMap)
					
				}
			}
			challangeResponse.addAll(invitationReceived)
			challangeResponse.addAll(active)
			challangeResponse.addAll(invitationSent)
		}
		return challangeResponse
	}
	
	public Map constructGameListChallengesResponse(String gameId, String userId){
		log.info "constructChallengesResponse() begin with gameId=${gameId}, userId=${userId}"
		int invitationReceivedCounter = 0
		int invitationSentCounter = 0
		int activeCounter = 0
		List<Map> invitationReceived = []
		Map challangeResponse = [invitationReceivedCounter:invitationReceivedCounter,
			invitationSentCounter:invitationSentCounter, activeCounter:activeCounter,
			invitationReceived:invitationReceived]
		
		List<Challenge> challenges = Challenge.findAll("from Challenge where eventKey=(:gameId) and "+
			"(challengeeUserId=(:userId) or challengerUserId=(:userId))",
			[gameId:gameId, userId:userId])
		
		if (challenges){

			for (Challenge challenge: challenges){
				if (challenge.challengeStatus == ChallengeStatusEnum.INVITED){
					if (challenge.challengerUserId == userId){
						invitationSentCounter++
					}else{
						if (invitationReceived.size()<1){
							Account challengerAccount = Account.findByUserId(challenge.challengerUserId)
							Map challengeMap = [userId: challengerAccount.userId, name: challengerAccount.displayName,
								pictureURL: challengerAccount.pictureUrl, avatarCode:challengerAccount.avatarCode,
								isChallengerWin: challenge.isChallengerWin.toString(), challengeStatus:"INVITATION_RECEIVED"]
							
							invitationReceived.add(challengeMap)
						}
						invitationReceivedCounter++
					}
				}else{ // active challenge					
					activeCounter++					
				}
			}
		}
		
		challangeResponse.invitationReceivedCounter = invitationReceivedCounter
		challangeResponse.invitationSentCounter = invitationSentCounter
		challangeResponse.activeCounter = activeCounter
		return challangeResponse
	}
}
