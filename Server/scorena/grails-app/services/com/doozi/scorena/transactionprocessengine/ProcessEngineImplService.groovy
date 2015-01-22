package com.doozi.scorena.transactionprocessengine


import java.util.Date;
import java.util.List;

import com.doozi.scorena.*
import com.doozi.scorena.utils.*
import com.doozi.scorena.gameengine.PoolInfo
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.processengine.*

import grails.plugins.rest.client.RestBuilder
import org.springframework.transaction.annotation.Transactional

@Transactional
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
	
	public static final int PROFIT_AMOUNT = 1
	public static final int USER_ID = 0
	
	private def payoutCleared(Question q){
		def result = payoutTansactionService.getPayoutTransByQuestion(q)
		if (result)
			true
		else
			false
	}
	
	@Transactional
	def processNewGamesScore(){
		log.info "processNewGameScore(): begins..."
		
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
		log.info "processNewGameScore(): ends..."
	}
	
	
	/**
	 * process new finished game payout. If the custom question result does not exist, mark Transaction Process Status to CUSOTM_QUESTION_UNPROCESSED and skip.  
	 * 
	 * @return
	 */
	@Transactional
	def processNewGamesPayout(){
		log.info "processNewGamePayout(): begins at "+new Date()
		
		//find all game process record that haven't processed
		def gameRecords = GameProcessRecord.findAllByTransProcessStatus(TransactionProcessStatusEnum.NOT_PROCESSED)		
		def totalpayout = 0
		def gameRecordsProcessed = 0
		boolean payoutErrorExist = false
		Map userTotalGamesProfit = [:]
		Map gameIdToGameInfoMap = [:]
		
		for (GameProcessRecord gameProcessRecord: gameRecords){

			//game=[leagueName:'', leagueCode:'', gameId:'':  type:'', gameStatus:'', date:'', away:[teamname: '', score:'', teamLogoUrl:''], home:[teamname: '', score:'', teamLogoUrl:'']]
			Map game = gameService.getGame(gameProcessRecord.eventKey)
			
			if (game == null || game==[:]){
				gameProcessRecord.transProcessStatus = TransactionProcessStatusEnum.ERROR
				gameProcessRecord.errorMessage = "ProcessEngineImplService::processGamePayout(): empty game data from eventkey="+gameProcessRecord.eventKey
				gameProcessRecord.lastUpdate = new Date()
				gameProcessRecord.save(flush: true)
				gameRecordsProcessed++
				continue
			}
			
			
			List<Question> questions = questionService.listQuestions(gameProcessRecord.eventKey)
			if (isReadyToProcess(questions, game) == true){
				boolean errorExists = false;
				String gameId = game.gameId
				
				gameIdToGameInfoMap.put(gameId, game)
				
				for (Question q:questions){			
					try{
						//userIdToProfitMap = [userId: questionProfit]
						Map userIdToProfitMap = processPayout(q, game)

						// userTotalGamesProfit = [gameId : [userId: game profit]]
						if (userTotalGamesProfit.containsKey(gameId)){
							String[] userIdKeys = userIdToProfitMap.keySet()						
							for(String userId: userIdKeys )	
							{
								// if map contains userID, add to profit
								if (userTotalGamesProfit[gameId].containsKey(userId)){
									userTotalGamesProfit[gameId][userId] += userIdToProfitMap[userId]
								}else { // does not contain userID, add entry
									userTotalGamesProfit[gameId].put(userId, userIdToProfitMap[userId])
								}
							}
						}else{
							userTotalGamesProfit.put(gameId, userIdToProfitMap)
						}
						
					}catch(Exception e){
						gameProcessRecord.transProcessStatus = TransactionProcessStatusEnum.ERROR
						gameProcessRecord.errorMessage = e.message
						gameProcessRecord.lastUpdate = new Date()
						gameProcessRecord.save()
						gameRecordsProcessed++
						errorExists = true;
						userTotalGamesProfit.remove(gameId)
						log.error "processNewGamePayout():: ERROR is ${e.message}"
						throw new RuntimeException("processNewGamePayout():: ERROR is ${e.message}")
						break
					}
				}
				
				if (!errorExists){
					gameProcessRecord.transProcessStatus = TransactionProcessStatusEnum.PROCESSED				
					gameProcessRecord.lastUpdate = new Date()
					gameProcessRecord.save()
					gameRecordsProcessed++
					
				}
			}
		}
		
//		sendEndGamePush(userTotalGamesProfit, gameIdToGameInfoMap)
		
		log.info "processGamePayout(): ends at "+new Date()
		return gameRecordsProcessed
	}
	
	@Transactional
	private boolean isReadyToProcess(List<Question> questions, Map game){
		log.info "isReadyToProcess(): begins..."		
		
		if (game.gameStatus == null || game.gameStatus.trim() != sportsDataService.POSTEVENT_NAME){
			println "ProcessEngineImplService:isReadyToProcess():: returned false because game status is either null or preevent. gameStatus="+game.gameStatus"/"+sportsDataService.POSTEVENT_NAME
			log.info "isReadyToProcess(): returned false because game status is either null or preevent. gameStatus="+game.gameStatus"/"+sportsDataService.POSTEVENT_NAME
			return false
		}
		
		if (game.away.score == null || game.away.score == "" ||game.home.score == null || game.home.score == ""){
			println "ProcessEngineImplService:isReadyToProcess():: returned false because game score is not available. game="+game 			
			log.info "isReadyToProcess(): returned false because game score is not available. game="+game 			
			return false
		}
				
		for (Question q:questions){
			if (isCustomQuestion(q.questionContent.questionType)){
				if (!customQuestionResultService.recordExist(q.id)){
					println "ProcessEngineImplService:isReadyToProcess():: returned false because custom game record does not exist"
					log.info "isReadyToProcess(): returned false because custom game record does not exist"
					return false
				}
			}
		}
		log.info "isReadyToProcess(): ends..."
		return true
		
	}
	@Transactional
	private boolean isCustomQuestion(String questionType){
		if (questionType == QuestionContent.CUSTOM || questionType.startsWith(QuestionContent.AUTOCUSTOM_PREFIX)){
			return true
		}
		return false
	}


    /**
     * @param question
     * @param game
     * @return returns user profit Map with userId as Key, question profit as Value
     */
	@Transactional
    private Map processPayout(Question q, Map game) throws Exception{
		log.info "processPayout(): begins with eventKey = ${game.gameId}, questionId = ${q.id}"	
		
			println "ProcessEngineImplService::processPayout(): starts with eventKey="+game.gameId+ "questionId="+q.id
			int winnerPick = calculateWinningPick(game, q)
			def payoutMultipleOfWager
			def userIdToProfitMap = [:]
			boolean processSuccess = true
			boolean onePickHasNoBet = false
			List<BetTransaction> betTransactions = betTransactionService.listAllBetsByQId(q.id)
			PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(betTransactions)	
								
				
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
				log.error "processPayout(): invalid winner pick, winnerPick = ${winnerPick}"
				return [:]
			}
			
			
		
		//	def rest = new RestBuilder()
			for (BetTransaction bet: betTransactions){
				
				//Payout = 0 for the lossing side
				//Payout > 1 for the winning, tie, and onePickHasNoBet
				int payout = 0
				if (winnerPick == 0 || bet.pick == winnerPick || onePickHasNoBet)
					payout =  Math.floor(bet.transactionAmount*payoutMultipleOfWager)

									
				int playResult = questionUserInfoService.getUserPickStatus(winnerPick, bet.pick)				
				int profit = payout - bet.transactionAmount
				
				Account account = bet.account
				
				payoutTansactionService.createPayoutTrans(account,q , payout, winnerPick, bet.transactionAmount, bet.pick, playResult, helperService.parseDateFromString(game.date))
				userIdToProfitMap.put(account.userId, profit)
				
			}
			
			log.info "processPayout(): ends"
			return userIdToProfitMap
    }
	
	private void sendEndGamePush(Map userTotalGameProfit, Map gameIdToGameInfoMap)
	{
		log.info "sendEndGamePush(): begins and "+gameIdToGameInfoMap.size()+" games will be pushed"
		def rest = new RestBuilder()

		//userTotalGamesProfit = [gameId : [userId: game profit]]
		Set gameIds =  userTotalGameProfit.keySet()
		for (String gameId: gameIds){
			Map userAGameProfit = userTotalGameProfit[gameId]
			String awayTeam = gameIdToGameInfoMap[gameId].away.teamname
			String homeTeam = gameIdToGameInfoMap[gameId].home.teamname
			String[] userIdKeys = userAGameProfit.keySet()
	
			for (String userID: userIdKeys )
			{
				int gameProfit = userAGameProfit[userID]
					String msg = ""
					if ( gameProfit > 0)
					{
						msg = "Congratulations! You have won " + gameProfit +" Coins in game "+ awayTeam +" vs "+ homeTeam
					}
					
					else if (gameProfit == 0)
					{
						msg = "Sorry, you did not win any Coins in game "+ awayTeam +" vs "+ homeTeam
					}
					
					else
					{
						msg = "Sorry, You have lost "+ Math.abs(gameProfit) +" Coins in game "+ awayTeam +" vs "+ homeTeam
					}
					
					// sends end of game push to user with amount of coins won or lost
					def payoutPush = pushService.endOfGamePush(rest,gameId ,userID, msg)
				 
			}
		}
		log.info "sendEndGamePush(): ends"
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
	def calculateWinningPick(Map game, Question question){
		log.info "calculateWinningPick(): begins..."
		
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
			case QuestionContent.SCOREGREATERTHAN_SOCCER:
				winnerPick = getScoreGreaterThanWinnerPick(game, question, questionContent.indicator1)
				break;
			case QuestionContent.SCOREGREATERTHAN_BASKETBALL:
				winnerPick = getScoreGreaterThanWinnerPick(game, question, questionContent.indicator1)
				break;
			case QuestionContent.CUSTOM:
				winnerPick = getCustomQuestionWinnerPick(question)
				break;
			case QuestionContent.HIGHERFIELDGOAL_BASKETBALL:
				winnerPick = getFGPctWinnerPick(game, question)
				break;
			case QuestionContent.AUTOCUSTOM_SOCCER1:
				winnerPick = getCustomQuestionWinnerPick(question)
				break;
		}
		
		log.info "calculateWinningPick(): ends with winnerPick = ${winnerPick}"
		
		return winnerPick
	}
	
	@Transactional
	private int getCustomQuestionWinnerPick(Question question){
		def customQuestionResult = customQuestionResultService.getCustomQuestionResult(question.id)
		if (!customQuestionResult)
			return -1
			
		int winnerPick = customQuestionResult.winnerPick
		if (winnerPick!=1 && winnerPick!=2 && winnerPick!=0){
			println "getCustomQuestionWinnerPick() ERROR: invalid winner pick"
			log.error "getCustomQuestionWinnerPick(): invalid winner pic"
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
	private int getFGPctWinnerPick(Map game, Question question){
		
		def homeFGP = new BigDecimal(game.home.fieldGoalPct)
		def awayFGP = new BigDecimal(game.away.fieldGoalPct)
		
		log.info "getFGPctWinnerPick(): homeFGP="+homeFGP
		log.info "getFGPctWinnerPick(): awayFGP="+awayFGP
		
		if (homeFGP > awayFGP ){
			return 1
		}else if (homeFGP < awayFGP ){
			return 2
		}else{
			return 0
		}
	}
	
	@Transactional
	private int getWhoWinWinnerPick(Map game, Question question){
		
		int homeScore = game.home.score.toInteger()
		int awayScore = game.away.score.toInteger()

		if (homeScore > awayScore){
			if (game.home.teamname.trim() == question.pick1.trim()){
				return 1
			}else if(game.home.teamname.trim() == question.pick2.trim()){
				return 2
			}else{
				println "ERROR: invalid teamname"
				println "game: " + game
				log.error "getWhoWinWinnerPic(): invalid teamname, game: ${game}"
				return -1
			}	
		}else if(homeScore < awayScore){
			if (game.away.teamname.trim() == question.pick1.trim()){
				return 1
			}else if(game.away.teamname.trim() == question.pick2.trim()){
				return 2
			}else{
				println "ERROR: invalid teamname"
				println "game: " + game
				log.error "getWhoWinWinnerPic(): invalid teamname, game: ${game}"
				return -1
			}
		}else{
			return 0
		}
	}
}
