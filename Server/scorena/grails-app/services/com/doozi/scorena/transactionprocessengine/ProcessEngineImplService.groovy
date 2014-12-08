package com.doozi.scorena.transactionprocessengine


import java.util.List;

import com.doozi.scorena.*
import com.doozi.scorena.utils.*
import com.doozi.scorena.gameengine.PoolInfo
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.processengine.*

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional


class ProcessEngineImplService {
	def questionService
	def gameService
	def betTransactionService
	def customQuestionResultService
	def sportsDataService
	def poolInfoService
	def questionPoolUtilService
	def payoutTansactionService
	def scoreService
	def questionUserInfoService
	def helperService
	def pushService
	
	private def payoutCleared(Question q){
		def result = payoutTansactionService.getPayoutTransByQuestion(q)
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
	
	
	
//	/**
//	 * deprecated
//	 * @return
//	 */
//	def processUnpaidPayout(){
//		println "ProcessEngineImplService::processUnpaidPayout(): starts"
//		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(PayoutTransactionProcessStatus.CUSOTM_QUESTION_UNPROCESSED)
//		
//		def gameRecordsProcessed = 0
//		for (GameProcessRecord gameProcessRecord: gameRecords){
//			
//			println "processing game: "+gameProcessRecord.eventKey
//
//			def fixed = false
//			def questions = questionService.listQuestions(gameProcessRecord.eventKey)
//			
//			for (Question q:questions){
//				
//				// non-custom questions are processed
//				if (!isCustomQuestion(q.questionContent.questionType)){
//					println "quetion "+q.questionContent+" is type " + q.questionContent.questionType
//					continue
//				}
//				
//				// Check if the payout are already exist. 
//				def clearTransResult = payoutCleared(q)
//				println "QuesitonID = "+q.id+" - clearTransResult: "+clearTransResult
//				if (clearTransResult){
//					continue
//				}
//				
//				// check if custom question result exists. If it does not exist, the question does not process.
//				if (!customQuestionResultService.recordExist(q.id)){
//					println "QuesitonID = "+q.id+" - custom question result does not exists"
//					continue
//				}
//								
//				processPayout(gameProcessRecord, q)
//				fixed = true
//			}
//			
//			if (fixed){
//				if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.CUSOTM_QUESTION_UNPROCESSED){
//					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.COMPLETED
//				}else if (gameProcessRecord.transProcessStatus==PayoutTransactionProcessStatus.ERROR_WITH_UNPROCCESSED_CUSTOM_QUESTION){
//					gameProcessRecord.transProcessStatus = PayoutTransactionProcessStatus.ERROR
//				}			
//				gameProcessRecord.lastUpdate = new Date()
//				gameProcessRecord.save(flush: true)
//				gameRecordsProcessed++
//			}
//		}
//				
//		println "ProcessEngineImplService::processUnpaidPayout(): ends"
//		return gameRecordsProcessed
//	}
	
	@Transactional
	def processNewGameScore(){
		
		println "processNewGameScore() starts"
		//find all game process record that haven't processed
		def gameRecords = GameProcessRecord.findAll("from GameProcessRecord as g where g.scoreProcessStatus = ? and g.transProcessStatus = ?", [ScoreProcessStatusEnum.NOT_PROCESSED, TransactionProcessStatusEnum.PROCESSED])
		for (GameProcessRecord gameProcessRecord: gameRecords){
			List<PayoutTransaction> pTransactionList= PayoutTransaction.findAll("from PayoutTransaction as p where p.eventKey=? order by p.account.id",[gameProcessRecord.eventKey])
			scoreService.processQuestionScore(pTransactionList, gameProcessRecord)
			scoreService.processGoldSilverBronzeScore(pTransactionList, gameProcessRecord)
			gameProcessRecord.scoreProcessStatus = ScoreProcessStatusEnum.PROCESSED
		}
		
		println "processNewGameScore() ends"
	}
	
	
	/**
	 * process new finished game payout. If the custom question result does not exist, mark Transaction Process Status to CUSOTM_QUESTION_UNPROCESSED and skip.  
	 * 
	 * @return
	 */
	def processNewGamePayout(){
		println "ProcessEngineImplService::processGamePayout(): starts"
		
		//find all game process record that haven't processed
		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(TransactionProcessStatusEnum.NOT_PROCESSED)		
		def totalpayout = 0
		def gameRecordsProcessed = 0
		boolean payoutErrorExist = false
		
		
		for (GameProcessRecord gameProcessRecord: gameRecords){
		
			List<Question> questions = questionService.listQuestions(gameProcessRecord.eventKey)
			Map game = gameService.getGame(gameProcessRecord.eventKey)
			
			if (game==[:]){
				gameProcessRecord.transProcessStatus = TransactionProcessStatusEnum.ERROR
				gameProcessRecord.lastUpdate = new Date()
				gameProcessRecord.save(flush: true)
				gameRecordsProcessed++
				continue
			}
			
			if (isReadyToProcess(questions, game) == true){
				int payoutCode=0
				for (Question q:questions){
					payoutCode = processPayout(q, game)
					if (payoutCode==-1){
						gameProcessRecord.transProcessStatus = TransactionProcessStatusEnum.ERROR
						break
					}
				}
				if (payoutCode == 0)
					gameProcessRecord.transProcessStatus = TransactionProcessStatusEnum.PROCESSED
				
				gameProcessRecord.lastUpdate = new Date()
				gameProcessRecord.save(flush: true)
				gameRecordsProcessed++
			}
		}
				
		println "ProcessEngineImplService::processGamePayout(): ends"
		return gameRecordsProcessed
	}
	
	private boolean isReadyToProcess(List<Question> questions, Map game){
				
		if (game.gameStatus == null || game.gameStatus.trim() != sportsDataService.POSTEVENT_NAME){
			println "ProcessEngineImplService:isReadyToProcess():: returned false because game status is either null or preevent. gameStatus="+game.gameStatus"/"+sportsDataService.POSTEVENT_NAME
			return false
		}
		
		if (game.away.score == null || game.away.score == "" ||game.home.score == null || game.home.score == ""){
			println "ProcessEngineImplService:isReadyToProcess():: returned false because game score is not available. game="+game 			
			return false
		}
				
		for (Question q:questions){
			if (q.questionContent.questionType == QuestionContent.CUSTOM){
				if (!customQuestionResultService.recordExist(q.id)){
					println "ProcessEngineImplService:isReadyToProcess():: returned false because custom game record does not exist"
					return false
				}
			}
		}
		return true
		
	}
	
	


    /**
     * @param question
     * @param game
     * @return -1 on error, 0 on success
     */
		@Transactional
    private int processPayout(Question q, Map game) {
			
			println "ProcessEngineImplService::processPayout(): starts with eventKey="+game.gameId+ "questionId="+q.id
			String awayTeam = game.away.teamname
			String homeTeam = game.home.teamname
			int winnerPick = calculateWinningPick(game, q)
			def payoutMultipleOfWager			
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
				println "ERROR: invalid winner pick, winnerPick="+winnerPick
				return -1
			}
			
			List<BetTransaction> betTransactions = betTransactionService.listAllBetsByQId(q.id)
			
			ArrayList accountsList = new ArrayList()
			for (BetTransaction bet: betTransactions){
				
				//Payout = 0 for the lossing side
				//Payout > 1 for the winning, tie, and onePickHasNoBet
				int payout = 0
				if (winnerPick == 0 || bet.pick == winnerPick || onePickHasNoBet)
					payout =  Math.floor(bet.transactionAmount*payoutMultipleOfWager)
								
				int playResult = questionUserInfoService.getUserPickStatus(winnerPick, bet.pick)
				
				// adds bet account to list of payout accounts if it does not already contain the account
				if (!accountsList.contains(bet.account))
				{
					accountsList.add(bet.account)
				}
				
				def code = payoutTansactionService.createPayoutTrans(bet.account,q, payout, winnerPick, bet.transactionAmount, bet.pick, playResult, helperService.parseDateFromString(game.date))
				if (code==-1)
				{
					return -1
				}
			}
			def rest = new RestBuilder()
			
			// for each account in the payout accounts 
			for (Account user: accountsList)
			{
				def userPayout = PayoutTransaction.executeQuery("Select sum(profit) from PayoutTransaction p where  p.account.id =? and eventKey = ?", [ user.id,game.gameId])
				
				
				System.out.println(user.id + " - Profit: " + userPayout[0])
				String msg = ""
				
				if (userPayout[0] > 0)
				{
					msg = "Congratulations! You have won " + userPayout[0] +" Coins in game "+ awayTeam +" vs "+ homeTeam 
				}
				else
				{
					msg = "Sorry, You have lost "+userPayout[0]+" Coins in game "+ awayTeam +" vs "+ homeTeam 
				}
				
				// sends end of game push to user with amount of coins won or lost 
				def payoutPush = pushService.endOfGamePush(rest,game.gameId,user.userId,msg)
				
				
			}
			
			
			return 0
			
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
	def calculateWinningPick(Map game, Question question){
		
		int winnerPick = -1
		if (game.gameStatus.trim() != sportsDataService.POSTEVENT_NAME){
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
	
	private int getScoreGreaterThanWinnerPick(Map game, Question question, String indicator){
		
		if ((game.home.score.toInteger() + game.away.score.toInteger()) > new BigDecimal( indicator ) ){			
			return 1
		}else{
			return 2
		}
	}
	
	private int getWhoWinWinnerPick(Map game, Question question){

		if (game.home.score > game.away.score){
			if (game.home.teamname.trim() == question.pick1.trim()){
				return 1
			}else if(game.home.teamname.trim() == question.pick2.trim()){
				return 2
			}else{
				println "ERROR: invalid teamname"
				println "game: " + game
				return -1
			}	
		}else if(game.home.score < game.away.score){
			if (game.away.teamname.trim() == question.pick1.trim()){
				return 1
			}else if(game.away.teamname.trim() == question.pick2.trim()){
				return 2
			}else{
				println "ERROR: invalid teamname"
				println "game: " + game
				return -1
			}
		}else{
			return 0
		}
	}
}
