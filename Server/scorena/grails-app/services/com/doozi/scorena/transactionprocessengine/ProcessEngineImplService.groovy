package com.doozi.scorena.transactionprocessengine


import com.doozi.scorena.*
import grails.transaction.Transactional


class ProcessEngineImplService {
	def questionService
	def gameService
	def betService
	def customQuestionResultService
	
	def payoutCleared(Question q){
		def result = betService.getPayoutTransByQuestion(q)
		if (result)
			true
		else
			false
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
				
				if (payoutCleared(q)){
					continue
				}
				
				if (q.questionContent.questionType == QuestionContent.CUSTOM){
					if (!customQuestionResultService.recordExist(q.id)){
						//customQuestionsResultNotExist=true
						continue
					}
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
			println "processing game: "+gameProcessRecord.eventKey
			gameProcessRecord.transProcessStatus = 1
			def customQuestionsResultNotExist = false
			def questions = questionService.listQuestions(gameProcessRecord.eventKey)
			for (Question q:questions){
				if (q.questionContent.questionType == QuestionContent.CUSTOM){
					if (!customQuestionResultService.recordExist(q.id)){
						customQuestionsResultNotExist=true
						continue
					}
				}
				processPayout(gameProcessRecord, q)
			}
			if (customQuestionsResultNotExist){
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
	
	@Transactional
    def processPayout(GameProcessRecord gameProcessRecord, Question q) {
			
			println "ProcessEngineImplService::processPayout(): starts with eventKey="+gameProcessRecord.eventKey+ "questionId="+q.id
			
			def eventKey = gameProcessRecord.eventKey
			int winnerPick = getWinningPick(eventKey, q)
			def payoutMultipleOfWager
			def betTransactions
			boolean processSuccess = true
			
			if (winnerPick == -1){
				println "ERROR: invalid winner pick"
				return
			}
			
			def lastTransaction = betService.getLatestBetByQuestionId(q.id.toString())
						
			
			def denominatorPick1Mult = lastTransaction.pick1Amount
			def denominatorPick2Mult = lastTransaction.pick2Amount
			
		
			if (denominatorPick1Mult==0)
				denominatorPick1Mult=1
				
			if (denominatorPick2Mult==0)
				denominatorPick2Mult=1
				
			if (winnerPick == 1){
				payoutMultipleOfWager = (lastTransaction.pick1Amount + lastTransaction.pick2Amount) / denominatorPick1Mult
			}else if (winnerPick ==2){
				payoutMultipleOfWager = (lastTransaction.pick1Amount + lastTransaction.pick2Amount) / denominatorPick2Mult
			}else{
				payoutMultipleOfWager = 1
			}
			
			int potAmoutToBePaid = lastTransaction.pick1Amount + lastTransaction.pick2Amount
			int numPlayersToBePaid = lastTransaction.pick1NumPeople + lastTransaction.pick2NumPeople
			
			betTransactions = betService.listAllBets(q.id)
			
			int totalWager = 0
			int totalpayout = 0

			for (PoolTransaction bet: betTransactions){
				Account account = bet.account
				int payout = 0
				if (winnerPick == 0 || bet.pick == winnerPick)
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
	
	//returns pick1 or pick2
	@Transactional
	def getWinningPick(eventKey, Question question){
		
		int winnerPick = -1
		def questionContent = question.questionContent
		def questionType = questionContent.questionType

		switch ( questionType ) {
			case QuestionContent.WHOWIN:
				winnerPick = getWhoWinWinnerPick(eventKey, question)
				break;
			case QuestionContent.SCOREGREATERTHAN:
                winnerPick = getScoreGreaterThanWinnerPick(eventKey, question, questionContent.indicator1)
				break;
			case QuestionContent.CUSTOM:
				winnerPick = getCustomQuestionWinnerPick(eventKey, question)
				break;
		}
	}
	
	int getCustomQuestionWinnerPick(eventKey, question){
		def customQuestionResult = customQuestionResultService.getCustomQuestionResult(question.id, eventKey)
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
	int getScoreGreaterThanWinnerPick(eventKey, question, indicator){
		def game = gameService.getGame(eventKey)
		if ((game.home.score.toInteger() + game.away.score.toInteger()) > new BigDecimal( indicator ) ){			
			return 1
		}else{
			return 2
		}
	}
	
	@Transactional
	int getWhoWinWinnerPick(eventKey, Question question){
		def game = gameService.getGame(eventKey)
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
