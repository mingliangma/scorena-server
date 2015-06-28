package com.doozi.scorena.challenge

import java.util.Map;

import org.springframework.transaction.annotation.Transactional

import com.doozi.scorena.Account
import com.doozi.scorena.enums.*
import com.doozi.scorena.utils.*

@Transactional
class ChallengeService {
	def gameUserInfoService
	def gameService
	def notificationService
	
	def createChallenge(String challengerUserId, String challengeeUserId, String gameId) {
		log.info "createChallenge() begins with challenger=${challengerUserId}, challengee=${challengeeUserId}, "+
		"gameId=${gameId}" 
		Account challengeeAccount = Account.findByUserId(challengeeUserId)
		
		if (!challengeeAccount){
			throw new RuntimeException("challengeeAccount does not exist")
		}
		
		Challenge challenge = Challenge.find("from Challenge where "+
			"(challengerUserId = (:challengerUserId) and challengeeUserId = (:challengeeUserId) and eventKey = (:gameId)) " + 
			"or (challengerUserId = (:challengeeUserId) and challengeeUserId = (:challengerUserId) and eventKey = (:gameId))",
			[challengeeUserId:challengeeUserId, challengerUserId:challengerUserId, gameId:gameId])
		
		if (challenge){
			Map existingResult = [challengeId: challenge.id,
			challengeeUserId: challengeeAccount.userId,
			challengeeDisplayName: challengeeAccount.displayName,
			challengeePictureUrl: challengeeAccount.pictureUrl,
			challengeeAvatarCode: challengeeAccount.avatarCode,
			challengeeAccountType: challengeeAccount.accountType,
			eventKey: gameId]
			return existingResult
		}else{
			return createChallengeRow(challengerUserId, challengeeAccount, gameId)
		}
	}
	
	def createBotChallengeWithRandomPlayer(){
		
		long maxBotAccountId = 30
		List<Account> botAccount = Account.findAllByIdLessThanEquals(maxBotAccountId)
		
		log.info"createBotChallengeWithRandomPlayer() botAccount size="+botAccount.size()
		
		Random r = new Random()
		int randomAccountIndex = r.nextInt(maxBotAccountId.toInteger())+1		
		Account challengerBotAccount =  botAccount.get(randomAccountIndex)
		
		//TODO: fix not invite playing game
		List<Map> games = gameService.listUpcomingGamesData("all", "MLB")		
		
		Map game = [:]
		for(Map gameRecord: games){
			if (gameRecord.gameStatus == EventTypeEnum.PREEVENT.toString()){
				game = gameRecord
			}
		}
		int challengeCount = 0
		if (game==[:]){			
			return challengeCount
		}
		
		Date date = new Date() - 45
		List<Account> accountList = Account.findAll("from Account as a where a.id in "+
			"( select bet.account.id from BetTransaction as bet where bet.createdAt > (:date)) "+
		"and a.userId not in "+
			"(select c.challengeeUserId from Challenge as c where "+
			"c.challengerUserId=(:challengerUserId) and c.eventKey=(:gameId))",
		[gameId: game.gameId, challengerUserId:challengerBotAccount.userId, date: date])
		
		println "accountList size: "+accountList.size()
		for(Account challengeeAccount: accountList){
			Map challengeResult = createChallengeRow(challengerBotAccount.userId, challengeeAccount, game.gameId)
			notificationService.challengeInvitationNotification(challengeResult.challengeId.toLong(),
				challengeResult.challengeeUserId, challengerBotAccount.displayName, game.gameId)			
			challengeCount++
		}
		return challengeCount
	}
	
	def createChallengeWithRandomPlayer(String challengerUserId, String gameId){
		log.info "createChallengeWithRandomPlayer() begins with challenger=${challengerUserId}, "+
		"gameId=${gameId}"
		
		List<Account> accountList = Account.findAll("from Account as a where a.id in "+
				"( select bet.account.id from BetTransaction as bet where bet.eventKey = (:gameId)) "+
			"and a.userId not in "+
				"(select c.challengeeUserId from Challenge as c where "+
				"c.challengerUserId=(:challengerUserId) and c.eventKey=(:gameId))", 
			[gameId: gameId, challengerUserId:challengerUserId])
		
		if (accountList.size() == 0){
			Date date = new Date() - 14
			accountList = Account.findAll("from Account as a where a.id in "+
				"( select bet.account.id from BetTransaction as bet where bet.createdAt > (:date)) "+
			"and a.userId not in "+
				"(select c.challengeeUserId from Challenge as c where "+
				"c.challengerUserId=(:challengerUserId) and c.eventKey=(:gameId))",
			[gameId: gameId, challengerUserId:challengerUserId, date: date])
		}
		
		if (accountList.size() == 0){
			Date date = new Date() - 45
			accountList = Account.findAll("from Account as a where a.id in "+
				"( select bet.account.id from BetTransaction as bet where bet.createdAt > (:date)) "+
			"and a.userId not in "+
				"(select c.challengeeUserId from Challenge as c where "+
				"c.challengerUserId=(:challengerUserId) and c.eventKey=(:gameId))",
			[gameId: gameId, challengerUserId:challengerUserId, date: date])
		}
		
		if (accountList.size() == 0){			
			return [code: 502, error: "No more users to challenge"]
		}
		Random r = new Random()
		int accountIndex = r.nextInt(accountList.size())
		Account account = accountList.get(accountIndex)
		Map challengeResult = createChallengeRow(challengerUserId, account, gameId)
		
		return challengeResult
	}
	
    Map createChallengeRow(String challengerUserId, Account challengeeAccount, String gameId) {
		
		Date now = new Date()
		Challenge challenge = new Challenge(
				challengeStatus: ChallengeStatusEnum.INVITED,
				challengerUserId: challengerUserId,
				challengeeUserId: challengeeAccount.userId,
				createdAt: now,
				updatedAt: now,
				eventKey: gameId,
				challengerResultStatus: ChallengeResultStatusEnum.PENDING
			)
		
		if (!challenge.save()){
			challenge.errors.each {
				println it
			}
			throw new RuntimeException("challenge saved failed")
		}
		
		Map result = [challengeId: challenge.id,
			challengeeUserId: challengeeAccount.userId,
			challengeeDisplayName: challengeeAccount.displayName,
			challengeePictureUrl: challengeeAccount.pictureUrl,
			challengeeAvatarCode: challengeeAccount.avatarCode,
			challengeeAccountType: challengeeAccount.accountType,
			eventKey: gameId]
		
		return result
    }	
	
	def acceptChallenge(long challengeId){
		
		return acceptChallenge(Challenge.get(challengeId))
	}
	
	def acceptChallenge(Challenge challenge){
		log.info "acceptChallenge() begins"
		challenge.challengeStatus = ChallengeStatusEnum.ACCEPTED
		challenge.updatedAt = new Date()
		if (!challenge.save()){
			challenge.errors.each {
				println it
			}
			return [error: "challenge updated failed"]
		}else{
			return [:]
		}
	}
	
	def ignoreChallenge(Challenge challenge){
		log.info "acceptChallenge() begins"
		challenge.challengeStatus = ChallengeStatusEnum.DECLINED
		challenge.updatedAt = new Date()
		if (!challenge.save()){
			challenge.errors.each {
				println it
			}
			return [error: "challenge updated failed"]
		}else{
			return [:]
		}
	}
	
	int processChallenge(Map userTotalGamesProfit){
		log.info "startProcessEngine(): begins with game size: ${userTotalGamesProfit.size()}"
		int challengeUpdatecounter = 0
		Date now = new Date()
		
		userTotalGamesProfit.each{String gameId, Map<String, String> userGameProfitMap ->
			
			List<Challenge> challengeList = Challenge.findAllByEventKeyAndChallengeStatus(gameId, ChallengeStatusEnum.ACCEPTED)
			
			for (Challenge challenge: challengeList){
				String challengerUserId = challenge.challengerUserId
				String challengeeUserId = challenge.challengeeUserId
								
//				int challengerGameProfit = gameUserInfoService.getUserGameProfit(gameId, challengerUserId)
//				int challengeeGameProfit = gameUserInfoService.getUserGameProfit(gameId, challengeeUserId)
				
				int challengerGameProfit = 0
				int challengeeGameProfit = 0
				
				if (userGameProfitMap.get(challengerUserId))
					challengerGameProfit = userGameProfitMap.get(challengerUserId)
					
				if (userGameProfitMap.get(challengeeUserId))
					challengeeGameProfit = userGameProfitMap.get(challengeeUserId)
				
				if (challengerGameProfit > challengeeGameProfit){
					challenge.challengerResultStatus = ChallengeResultStatusEnum.WIN
				}else if (challengerGameProfit < challengeeGameProfit){
					challenge.challengerResultStatus = ChallengeResultStatusEnum.LOSS
				}else{
					challenge.challengerResultStatus = ChallengeResultStatusEnum.TIE
				}
				challenge.updatedAt = now
				
				if (!challenge.save()){
					challenge.errors.each {
						println it
					}
				}else{
					challengeUpdatecounter++
				}
			}
		}
		return challengeUpdatecounter
	}
	
	int processChallengeInit(List<String> gameIdList){
		log.info "startProcessEngine(): begins with game size: ${gameIdList.size()}"
		int challengeUpdatecounter = 0
		Date now = new Date()
		for (String gameId: gameIdList){
			List<Challenge> challengeList = Challenge.findAllByEventKeyAndChallengeStatus(gameId, ChallengeStatusEnum.ACCEPTED)
			
			for (Challenge challenge: challengeList){
				String challengerUserId = challenge.challengerUserId
				String challengeeUserId = challenge.challengeeUserId
								
				int challengerGameProfit = gameUserInfoService.getUserGameProfit(gameId, challengerUserId)
				int challengeeGameProfit = gameUserInfoService.getUserGameProfit(gameId, challengeeUserId)
				
				
				if (challengerGameProfit > challengeeGameProfit){
					challenge.challengerResultStatus = ChallengeResultStatusEnum.WIN
				}else if (challengerGameProfit < challengeeGameProfit){
					challenge.challengerResultStatus = ChallengeResultStatusEnum.LOSS
				}else{
					challenge.challengerResultStatus = ChallengeResultStatusEnum.TIE
				}
				challenge.updatedAt = now
				
				
				if (!challenge.save()){
					challenge.errors.each {
						println it
					}
				}else{
					challengeUpdatecounter++
				}
			}
		}
		return challengeUpdatecounter
	}
	
	def listGameChallenges(String gameId, String userId){
		List challenges = constructGameChallengesResponse(gameId, userId)
		return challenges
	}
	
	public List constructGameChallengesResponse(String gameId, String userId){
		log.info "constructChallengesResponse() begin with gameId=${gameId}, userId=${userId}"
		List challangeResponse = []
		
		List<Challenge> challenges = Challenge.findAll("from Challenge where eventKey=(:gameId) and "+
			"(challengeeUserId=(:userId) or challengerUserId=(:userId)) and challengeStatus != (:challengeStatus)",
			[gameId:gameId, userId:userId, challengeStatus:ChallengeStatusEnum.DECLINED])
		
		if (challenges){
			
			List<Map> invitationReceived = []
			List<Map> invitationSent = []
			List<Map> active =[]
			
			for (Challenge challenge: challenges){
				if (challenge.challengeStatus == ChallengeStatusEnum.INVITED){
					if (challenge.challengerUserId == userId){
						Account challengeeAccount = Account.findByUserId(challenge.challengeeUserId)
						Map challengeMap = [opponentUserId: challengeeAccount.userId, opponentName: challengeeAccount.displayName,
							opponentPictureURL: challengeeAccount.pictureUrl, opponentAvatarCode:challengeeAccount.avatarCode,
							currentUserResultStatus: ChallengeResultStatusEnum.PENDING.toString(), challengeStatus:"INVITATION_SENT", 
							challengeId: challenge.id]
						
						invitationSent.add(challengeMap)
					}else{
						Account challengerAccount = Account.findByUserId(challenge.challengerUserId)
						Map challengeMap = [opponentUserId: challengerAccount.userId, opponentName: challengerAccount.displayName,
							opponentPictureURL: challengerAccount.pictureUrl, opponentAvatarCode:challengerAccount.avatarCode,
							currentUserResultStatus: ChallengeResultStatusEnum.PENDING.toString(), challengeStatus:"INVITATION_RECEIVED",
							challengeId: challenge.id]
						
						invitationReceived.add(challengeMap)
					}
				}else{ // active challenge
					
					String opponentUserId = ""
					if (challenge.challengerUserId == userId){
						opponentUserId=challenge.challengeeUserId
					}else{
						opponentUserId=challenge.challengerUserId
					}					
					Account opponentAccount = Account.findByUserId(opponentUserId)
					
					ChallengeResultStatusEnum currentUserResultStatus = getChallengeResultStatus(challenge, userId)
					
					Map challengeMap = [opponentUserId: opponentAccount.userId, opponentName: opponentAccount.displayName,
						opponentPictureURL: opponentAccount.pictureUrl, opponentAvatarCode:opponentAccount.avatarCode,
						currentUserResultStatus: currentUserResultStatus.toString(), challengeStatus:"ACTIVE",
						challengeId: challenge.id]
					
					active.add(challengeMap)
					
				}
			}
			challangeResponse.addAll(invitationReceived)
			challangeResponse.addAll(active)
			challangeResponse.addAll(invitationSent)
		}
		return challangeResponse
	}
	
	private ChallengeResultStatusEnum getChallengeResultStatus(Challenge challenge, String currentUserId){
		ChallengeResultStatusEnum currentUserResultStatus = ChallengeResultStatusEnum.PENDING
		if (challenge.challengerResultStatus == ChallengeResultStatusEnum.PENDING || challenge.challengerResultStatus == ChallengeResultStatusEnum.TIE){
			currentUserResultStatus = challenge.challengerResultStatus
		}else if (challenge.challengerResultStatus == ChallengeResultStatusEnum.WIN || challenge.challengerResultStatus == ChallengeResultStatusEnum.LOSS){
			if (currentUserId == challenge.challengerUserId){
				currentUserResultStatus = challenge.challengerResultStatus
			}else{
				if (challenge.challengerResultStatus == ChallengeResultStatusEnum.WIN){
					currentUserResultStatus = ChallengeResultStatusEnum.LOSS
				}else{
					currentUserResultStatus = ChallengeResultStatusEnum.WIN
				}
			}
		}
		return currentUserResultStatus
	}
	
	public Map constructGameListChallengesResponse(String gameId, String userId){
		log.info "constructChallengesResponse() begin with gameId=${gameId}, userId=${userId}"
		int invitationReceivedCounter = 0
		int invitationSentCounter = 0
		int activeCounter = 0
		int winningCounter = 0
		int losingCounter = 0
		int tieCounter = 0
		List<Map> invitationReceived = []
		Map challangeResponse = [invitationReceivedCounter:invitationReceivedCounter,
			invitationSentCounter:invitationSentCounter, activeCounter:activeCounter,
			losingCounter:losingCounter,winningCounter:winningCounter, tieCounter:tieCounter,			
			invitationReceived:invitationReceived]
		
		List<Challenge> challenges = Challenge.findAll("from Challenge where eventKey=(:gameId) and "+
			"(challengeeUserId=(:userId) or challengerUserId=(:userId)) and challengeStatus != (:challengeStatus)",
			[gameId:gameId, userId:userId, challengeStatus:ChallengeStatusEnum.DECLINED])
		
		if (challenges){
			

			for (Challenge challenge: challenges){
				if (challenge.challengeStatus == ChallengeStatusEnum.INVITED){
					if (challenge.challengerUserId == userId){
						invitationSentCounter++
					}else{
						if (invitationReceived.size()<1){
							Account challengerAccount = Account.findByUserId(challenge.challengerUserId)
							ChallengeResultStatusEnum currentUserResultStatus = getChallengeResultStatus(challenge, userId)
							
							Map challengeMap = [opponentUserId: challengerAccount.userId, opponentName: challengerAccount.displayName,
								opponentPictureURL: challengerAccount.pictureUrl, opponentAvatarCode:challengerAccount.avatarCode,
								currentUserResultStatus: currentUserResultStatus.toString(), challengeStatus:"ACTIVE"]
							
							invitationReceived.add(challengeMap)
						}
						invitationReceivedCounter++
					}
				}else{ // active challenge										
					activeCounter++	
					ChallengeResultStatusEnum currentUserResultStatus = getChallengeResultStatus(challenge, userId)
					if (currentUserResultStatus == ChallengeResultStatusEnum.WIN){
						winningCounter++
					}else if (currentUserResultStatus == ChallengeResultStatusEnum.LOSS){
						losingCounter++
					}else if (currentUserResultStatus == ChallengeResultStatusEnum.TIE){
						tieCounter++
					}
				}
			}
		}
		
		challangeResponse.invitationReceivedCounter = invitationReceivedCounter
		challangeResponse.invitationSentCounter = invitationSentCounter
		challangeResponse.activeCounter = activeCounter
		challangeResponse.winningCounter = winningCounter
		challangeResponse.losingCounter = losingCounter
		challangeResponse.tieCounter = tieCounter
		return challangeResponse
	}
}
