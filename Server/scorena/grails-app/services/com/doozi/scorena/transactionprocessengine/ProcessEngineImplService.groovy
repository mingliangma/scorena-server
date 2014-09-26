package com.doozi.scorena.transactionprocessengine


import java.util.List;

import com.doozi.scorena.*
import com.doozi.scorena.utils.*
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
	def questionPoolUtilService
	def payoutService
	
	def payoutCleared(Question q){
		def result = payoutService.getPayoutTransByQuestion(q)
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
		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(PayoutTransactionProcessStatus.CUSOTM_QUESTION_UNPROCESSED)
		
		def gameRecordsProcessed = 0
		for (GameProcessRecord gameProcessRecord: gameRecords){
			
			println "processing game: "+gameProcessRecord.eventKey

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
				
				// check if custom question result exists. If it does not exist, the question does not process.
				if (!customQuestionResultService.recordExist(q.id)){
					println "QuesitonID = "+q.id+" - custom question result does not exists"
					continue
				}
								
				processPayout(gameProcessRecord, q)
				fixed = true
			}
			
			if (fixed){
				if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.CUSOTM_QUESTION_UNPROCESSED){
					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.COMPLETED
				}else if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.ERROR_WITH_UNPROCCESSED_CUSTOM_QUESTION){
					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.ERROR
				}			
				gameProcessRecord.lastUpdate = new Date()
				gameProcessRecord.save(flush: true)
				gameRecordsProcessed++
			}
		}
				
		println "ProcessEngineImplService::processUnpaidPayout(): ends"
		return gameRecordsProcessed
	}
	
	/**
	 * process new finished game payout. If the custom question result does not exist, mark Transaction Process Status to CUSOTM_QUESTION_UNPROCESSED and skip.  
	 * 
	 * @return
	 */
	def processNewGamePayout(){
		println "ProcessEngineImplService::processGamePayout(): starts"
		
		//find all game process record that haven't processed
		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(PayoutTransactionProcessStatus.NOT_PROCESSED)		
		def totalpayout = 0
		def gameRecordsProcessed = 0
		
		
		
		for (GameProcessRecord gameProcessRecord: gameRecords){
			gameProcessRecord.transProcessStatus = 1			
			def customQuestionsResultExists = true
			def questions = questionService.listQuestions(gameProcessRecord.eventKey)
			
			for (Question q:questions){
				
				//skip the custom questions that do not have a game result record. later this game process record is marked
				// as CUSOTM_QUESTION_UNPROCESSED
				if (q.questionContent.questionType == QuestionContent.CUSTOM){
					if (!customQuestionResultService.recordExist(q.id)){
						customQuestionsResultExists=false
						continue
					}
				}
				processPayout(gameProcessRecord, q)
			}
			
			//if there are unprocessed custom questions
			if (!customQuestionsResultExists){
				if (gameProcessRecord.transProcessStatus== PayoutTransactionProcessStatus.IN_PROCESS){
					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.CUSOTM_QUESTION_UNPROCESSED
				}else if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.ERROR){
					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.ERROR_WITH_UNPROCCESSED_CUSTOM_QUESTION
				}
			}
			//if all questions are processed, the game process record is marked as COMPLETED
			else{
				if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.IN_PROCESS){	
						gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.COMPLETED
				}
			}
			gameProcessRecord.lastUpdate = new Date()
			gameProcessRecord.save(flush: true)
			gameRecordsProcessed++
		}
				
		println "ProcessEngineImplService::processGamePayout(): ends"
		return gameRecordsProcessed
	}
	
	
	@Transactional
    def processPayout(GameProcessRecord gameProcessRecord, Question q) {
			
			println "ProcessEngineImplService::processPayout(): starts with eventKey="+gameProcessRecord.eventKey+ "questionId="+q.id
			
			def eventKey = gameProcessRecord.eventKey
			int winnerPick = getWinningPick(eventKey, q)
			int payoutMultipleOfWager			
			boolean processSuccess = true
			boolean onePickHasNoBet = false
			PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(q.id)						
				
			//Calculate the payout multiple for the winning side. If there is one side that has no one bet on, the variable "onePickHasNoBet" is set to true,
			//and later, we give the coins back to the users
			if (winnerPick == WinnerPick.PICK1_WON){
				if (questionPoolInfo.getPick1NumPeople() == 0 ^ questionPoolInfo.getPick2NumPeople() == 0) {
					onePickHasNoBet = true
					payoutMultipleOfWager = 1
				}else{
					payoutMultipleOfWager = questionPoolUtilService.calculatePick1PayoutMultiple(questionPoolInfo)
				}
			}else if (winnerPick == WinnerPick.PICK2_WON){
				if (questionPoolInfo.getPick1NumPeople() == 0 ^ questionPoolInfo.getPick2NumPeople() == 0) {
					onePickHasNoBet = true
					payoutMultipleOfWager = 1
				}else{
					payoutMultipleOfWager = questionPoolUtilService.calculatePick2PayoutMultiple(questionPoolInfo)
				}
			}else if (winnerPick == WinnerPick.PICK_TIE) {
				payoutMultipleOfWager = 1
			}else{
				println "ERROR: invalid winner pick"
				return
			}
			
			List<BetTransaction> betTransactions = betService.listAllBetsByQId(q.id)

			for (BetTransaction bet: betTransactions){
				
				//Payout = 0 for the lossing side
				//Payout > 1 for the winning, tie, and onePickHasNoBet
				int payout = 0
				if (winnerPick == 0 || bet.pick == winnerPick || onePickHasNoBet)
					payout =  Math.floor(bet.transactionAmount*payoutMultipleOfWager)
				
				Account account = bet.account
				def newBalance = account.currentBalance + payout
				account.previousBalance = account.currentBalance
				account.currentBalance = newBalance
				
				
				def code = payoutService.createPayoutTrans(account,q, payout, winnerPick, bet.transactionAmount, bet.pick)
				if (code==-1){
					processSuccess=false
				}
			}
			if (processSuccess != true){
				if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.IN_PROCESS)			
					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.ERROR		
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
