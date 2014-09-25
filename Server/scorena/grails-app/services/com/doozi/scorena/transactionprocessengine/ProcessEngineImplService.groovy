package com.doozi.scorena.transactionprocessengine


import java.util.List;

import com.doozi.scorena.*
import com.doozi.scorena.gameengine.PoolInfo
import com.doozi.scorena.transaction.BetTransaction

import grails.transaction.Transactional


class ProcessEngineImplService {
	def questionService
	def gameService
	def betService
	def customQuestionResultService
	def sportsDataService
	def poolInfoService
	
	def payoutCleared(Question q){
		def result = betService.getPayoutTransByQuestion(q)
		if (result)
			true
		else
			false
	}
	
	private Boolean isCustomQuestion(String qContentType){		
		if (qContentType == QuestionContent.CUSTOM){
			return true
		}else if (qContentType == QuestionContent.CUSTOMTEAM0){
			return true
		}else if (qContentType == QuestionContent.CUSTOMTEAM1){
			return true
		}
		return false
	}
	
	def processUnpaidPayout(){
		println "ProcessEngineImplService::processUnpaidPayout(): starts"
		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(3)
		
		def totalpayout = 0
		def gameRecordsProcessed = 0
		
		for (GameProcessRecord gameProcessRecord: gameRecords){
			
			println "processing game: "+gameProcessRecord.eventKey

			//def customQuestionsResultNotExist = false
			def fixed = false
			def questions = questionService.listQuestions(gameProcessRecord.eventKey)
			
			for (Question q:questions){
				
				// non-custom questions are not processed
				if (!isCustomQuestion(q.questionContent.questionType)){
					println "quetion "+q.questionContent+" is type " + q.questionContent.questionType
					continue
				}
				
				// Check if the payout are already exist. 
				def clearTransResult = payoutCleared(q)
				println "QuesitonID = "+q.id+" - clearTransResult: "+clearTransResult
				if (clearTransResult){
					continue
				}
				
				// check if custom question result exists
				if (!customQuestionResultService.recordExist(q.id)){
					println "QuesitonID = "+q.id+" - custom question result does not exists"
					continue
				}
								
				processPayout(gameProcessRecord, q)
				fixed = true
			}
			
			if (fixed){
				if (gameProcessRecord.transProcessStatus==3){
					gameProcessRecord.transProcessStatus = 2
				}else if (gameProcessRecord.transProcessStatus==-2){
					gameProcessRecord.transProcessStatus = -1
				}			
				gameProcessRecord.lastUpdate = new Date()
				gameProcessRecord.save(flush: true)
				gameRecordsProcessed++
			}
		}
				
		println "ProcessEngineImplService::processUnpaidPayout(): ends"
		return gameRecordsProcessed
	}
	
	def processNewGamePayout(){
		println "ProcessEngineImplService::processGamePayout(): starts"
		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(0)		
		def totalpayout = 0
		def gameRecordsProcessed = 0
		
		for (GameProcessRecord gameProcessRecord: gameRecords){

			gameProcessRecord.transProcessStatus = 1			
			def customQuestionsResultExists = true
			def questions = questionService.listQuestions(gameProcessRecord.eventKey)
			
			for (Question q:questions){
				if (q.questionContent.questionType == QuestionContent.CUSTOM){
					if (!customQuestionResultService.recordExist(q.id)){
						customQuestionsResultExists=false
						continue
					}
				}
				processPayout(gameProcessRecord, q)
			}
			
			if (!customQuestionsResultExists){
				if (gameProcessRecord.transProcessStatus==1){
					gameProcessRecord.transProcessStatus = 3
				}else if (gameProcessRecord.transProcessStatus==-1){
					gameProcessRecord.transProcessStatus = -2
				}
			}else{
				if (gameProcessRecord.transProcessStatus==1){	
						gameProcessRecord.transProcessStatus = 2
				}
			}
			gameProcessRecord.lastUpdate = new Date()
			gameProcessRecord.save(flush: true)
			gameRecordsProcessed++
		}
				
		println "ProcessEngineImplService::processGamePayout(): ends"
		return gameRecordsProcessed
	}
	
	
//	@Transactional
    def processPayout(GameProcessRecord gameProcessRecord, Question q) {
			
			println "ProcessEngineImplService::processPayout(): starts with eventKey="+gameProcessRecord.eventKey+ "questionId="+q.id
			
			def eventKey = gameProcessRecord.eventKey
			int winnerPick = getWinningPick(eventKey, q)
			def payoutMultipleOfWager
			
			boolean processSuccess = true
			
			if (winnerPick == -1){
				println "ERROR: invalid winner pick"
				return
			}
			
//			def lastTransaction = betService.getLatestBetByQuestionId(q.id.toString())
			PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(q.id)
						
			
			def denominatorPick1Mult = questionPoolInfo.getPick1Amount()
			def denominatorPick2Mult = questionPoolInfo.getPick2Amount()
			
		
			if (denominatorPick1Mult==0)
				denominatorPick1Mult=1
				
			if (denominatorPick2Mult==0)
				denominatorPick2Mult=1
				
			if (winnerPick == 1){
				payoutMultipleOfWager = (questionPoolInfo.getPick1Amount() + questionPoolInfo.getPick2Amount()) / denominatorPick1Mult
			}else if (winnerPick ==2){
				payoutMultipleOfWager = (questionPoolInfo.getPick1Amount() + questionPoolInfo.getPick2Amount()) / denominatorPick2Mult
			}else{
				payoutMultipleOfWager = 1
			}
			
			int potAmoutToBePaid = questionPoolInfo.getPick1Amount() + questionPoolInfo.getPick2Amount()
			int numPlayersToBePaid = questionPoolInfo.getPick1NumPeople() + questionPoolInfo.getPick2NumPeople()
			
			boolean onePickHasNoBet = false
			if (questionPoolInfo.getPick1NumPeople() == 0 ^ questionPoolInfo.getPick2NumPeople() == 0) {
				onePickHasNoBet = true
				payoutMultipleOfWager = 1
			}
			
			List<BetTransaction> betTransactions = betService.listAllBetsByQId(q.id)
			
			int totalWager = 0
			int totalpayout = 0

			for (BetTransaction bet: betTransactions){
				Account account = bet.account
				int payout = 0
				if (winnerPick == 0 || bet.pick == winnerPick || onePickHasNoBet)
					payout =  Math.floor(bet.transactionAmount*payoutMultipleOfWager)
					
				def newBalance = account.currentBalance + payout

				account.currentBalance = newBalance
				def code = betService.savePayoutTrans(account,q, payout, winnerPick, potAmoutToBePaid, numPlayersToBePaid, bet.transactionAmount)
				if (code==-1){
					processSuccess=false
				}
				potAmoutToBePaid = potAmoutToBePaid-payout
				numPlayersToBePaid = numPlayersToBePaid - 1
			}
			if (processSuccess != true){
				if (gameProcessRecord.transProcessStatus==1)			
					gameProcessRecord.transProcessStatus = -1				
			}
			
			println "ProcessEngineImplService::processPayout(): ends"
    }
	
	/**
	 * @param eventKey
	 * @param question
	 * @return 
	 * 			- 1 if pick1 wins
	 * 			- 2 if pick2 wins
	 * 			- 0 if ties
	 * 			- -1 if not winning result
	 */
	@Transactional
	def getWinningPick(String eventKey, Question question){
		
		int winnerPick = -1
		Map game = gameService.getGame(eventKey)
		if (game.gameStatus.trim() != sportsDataService.POSTEVENT){
			return winnerPick
		}
		
		QuestionContent questionContent = question.questionContent
		String questionType = questionContent.questionType

		switch ( questionType ) {
			case QuestionContent.WHOWIN:
				winnerPick = getWhoWinWinnerPick(game, question)
				break;
			case QuestionContent.SCOREGREATERTHAN:
                winnerPick = getScoreGreaterThanWinnerPick(game, question, questionContent.indicator1)
				break;
			case QuestionContent.CUSTOM:
				winnerPick = getCustomQuestionWinnerPick(question)
				break;
			case QuestionContent.CUSTOMTEAM0:
				winnerPick = getCustomQuestionWinnerPick(question)
				break;
			case QuestionContent.CUSTOMTEAM1:
				winnerPick = getCustomQuestionWinnerPick(question)
				break;
		}
		return winnerPick
	}
	
	private int getCustomQuestionWinnerPick(Question question){
		def customQuestionResult = customQuestionResultService.getCustomQuestionResult(question.id)
		if (!customQuestionResult)
			return -1
			
		int winnerPick = customQuestionResult.winnerPick
		if (winnerPick!=1 && winnerPick!=2 && winnerPick!=0){
			println "getCustomQuestionWinnerPick() ERROR: invalid winner pick"
			return -1
		}
		return winnerPick
	}
	
	@Transactional
	private int getScoreGreaterThanWinnerPick(Map game, Question question, String indicator){
		
		if ((game.home.score.toInteger() + game.away.score.toInteger()) > new BigDecimal( indicator ) ){			
			return 1
		}else{
			return 2
		}
	}
	
	@Transactional
	private int getWhoWinWinnerPick(Map game, Question question){

		if (game.home.score > game.away.score){
			if (game.home.teamname.trim() == question.pick1.trim()){
				return 1
			}else if(game.home.teamname.trim() == question.pick2.trim()){
				return 2
			}else{
				println "ERROR: invalid teamname"
				return -1
			}	
		}else if(game.home.score < game.away.score){
			if (game.away.teamname.trim() == question.pick1.trim()){
				return 1
			}else if(game.away.teamname.trim() == question.pick2.trim()){
				return 2
			}else{
				println "ERROR: invalid teamname"
				return -1
			}
		}else{
			return 0
		}
	}
}
