package com.doozi.scorena.gameengine

import java.util.Date;

import com.doozi.scorena.Account;
import com.doozi.scorena.BetResult;
import com.doozi.scorena.PoolTransaction;
import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction

import org.hibernate.criterion.CriteriaSpecification

import grails.transaction.Transactional

@Transactional
class BetService {
	def sportsDataService
	def customGameService
	
	def getPayoutTransByQuestion(Question q){
		def result = PoolTransaction.find("from PoolTransaction as t where (t.transactionType=1 and t.question.id=?)",[q.id])
	}
	
	
	def savePayoutTrans(Account playerAccount, Question q, int payout, int winnerPick, int potAmoutToBePaid, int numPlayersToBePaid, int wager){
		def potAmoutToBePaidAfter = potAmoutToBePaid - payout
		def numPlayersToBePaidAfter = numPlayersToBePaid - 1
		int result = 0
		if (potAmoutToBePaidAfter < 0 || numPlayersToBePaidAfter < 0){
			println "ERROR: Total payout amount is nagative"
			result = -1
		} 
//		Random random = new Random()
//		def createdAt = new Date() - (random.nextInt(8) + 3)
		
		def bet = new PoolTransaction(transactionAmount: payout, transactionType:PoolTransaction.PAYOUT, createdAt: new Date(), pick: winnerPick, pick1Amount:potAmoutToBePaidAfter, pick1NumPeople:numPlayersToBePaidAfter,
			pick2Amount:wager, pick2NumPeople:0, eventKey: q.eventKey)
		
//		def bet = new PoolTransaction(transactionAmount: payout, transactionType:PoolTransaction.PAYOUT, createdAt: createdAt, pick: winnerPick, pick1Amount:potAmoutToBePaidAfter, pick1NumPeople:numPlayersToBePaidAfter,
//			pick2Amount:wager, pick2NumPeople:0)
		
		playerAccount.addToBet(bet)
		q.addToBet(bet)
		
		if (!playerAccount.save(failOnError:true)){
			println "ERROR: payout transaction failed to be added to player account"
			result = -1
		}
		
		if (!q.save(failOnError:true)){
			println "ERROR: payout transaction failed to be added to question"
			result = -1
		}
		
		return result
	}
	
//	def saveBetTrans(int _wager, Date _time, int _pick, String userId, long quesitonId) {
//		def account = Account.findByUserId(userId)
//		if (!account){
//			return [code:202, message: "the userId does not exsist"]
//		}
//		
//		if (account.currentBalance < _wager){
//			return [code:202, message: "The user does not have enough coins to make a bet"]
//		}
//		
//		if (_wager <= 0){
//			return [code:202, message: "The user cannot bet negative amount"]
//		}
//		
//		if (_pick < 1 || _pick > 2){
//			return [code:202, message: "the pick is not available"]
//		}
//		
//		Question question = Question.findById(quesitonId)
//		if (!question){
//			return [code:202, message: "the questionId does not exsist"]
//		}
//		
//		def game = sportsDataService.getGame(question.eventKey)
//		if (game!=[]){
//			if (game.gameStatus != sportsDataService.PREEVENT){
//				return [code:202, message: "the match is already started. All pool is closed"]
//			}
//		}else{
//			def customGame = customGameService.getGame(question.eventKey)
//			if (customGame.gameStatus != sportsDataService.PREEVENT){
//				return [code:202, message: "the match is already started. All pool is closed"]
//			}
//		}
//		return saveBetTrans(_wager, _time, _pick, account, question)
//		
//	}
	
	Map createBetTrans(int playerWager, int playerPick, String userId, long quesitonId) {
		createBetTrans( playerWager, playerPick, userId, quesitonId, new Date(), true)
	}
	
	Map createBetTrans(int playerWager, int playerPick, String userId, long quesitonId, Date transactionDate, boolean toValidate) {
		
		Account playerAccount = Account.findByUserId(userId)
		Question question = Question.findById(quesitonId)
		
		if (toValidate){
			//Find the bet transaction that associated with the given userId and questionId 
			BetTransaction betTrans = BetTransaction.find("from BetTransaction as b where (b.question.id=? and b.account.id=?)", question.id, playerAccount.id)
			
			Map validationResult =  validateBetTrans(playerWager, playerPick, playerAccount, question, betTrans)		
			
			if (validationResult!=[:]){
				return validationResult
			}
		}
		
		BetTransaction newBetTransaction = new BetTransaction(transactionAmount: playerWager, createdAt: transactionDate, pick: playerPick, eventKey: question.eventKey)
		
		
		playerAccount.addToTrans(newBetTransaction)
		question.addToBetTrans(newBetTransaction)

		
		playerAccount.currentBalance -= playerWager
		
		if (!playerAccount.save(failOnError:true)){
			System.out.println("---------------account save failed")
			return [code:202, error: "account data is not saved"]
		}
		
		if (!question.save(failOnError:true)){
			System.out.println("---------------q save failed")
			return [code:202, error: "Question data is not saved"]
		}		

		return [:]
	}
	
	
	

	
//    def saveBetTrans(int playerWager, Date betCreatedAt, int playerPick, Account playerAccount, Question q) {
//		int pick1amount
//		int pick2amount 
//		int pick1num 
//		int pick2num 
//		def lastBet
//		
//		def b = PoolTransaction.find("from PoolTransaction as b where (b.question.id=? and b.account.id=? and b.transactionType=?)", q.id, playerAccount.id, PoolTransaction.BUYIN)
//		if (b){
//			return [code:202, message: "the bet transaction already exsists"]
//			
//		}
//		
//		if (q.bet == null || q.bet.size() == 0){
//			if (playerPick==1){
//				 pick1amount = playerWager
//				 pick1num = 1
//				 pick2amount = 0			 
//				 pick2num = 0
//			}else{
//				pick1amount = 0
//				pick1num = 0
//				pick2amount = playerWager
//				pick2num = 1
//			}
//			
//		}else{
//		
//			def questionId = q.id.toString()
//			lastBet = getLatestBetByQuestionId(questionId)
//		
//			if (playerPick==1){
//				 pick1amount = lastBet.pick1Amount + playerWager
//				 pick1num = lastBet.pick1NumPeople + 1
//				 pick2amount = lastBet.pick2Amount			 
//				 pick2num = lastBet.pick2NumPeople
//			}else{
//				pick1amount = lastBet.pick1Amount	
//				pick1num = lastBet.pick1NumPeople
//				pick2amount = lastBet.pick2Amount + playerWager
//				pick2num = lastBet.pick2NumPeople + 1
//			}
//				
//		}	
//		def bet = new PoolTransaction(transactionAmount: playerWager, transactionType:PoolTransaction.BUYIN, createdAt: betCreatedAt, pick: playerPick, pick1Amount:pick1amount, pick1NumPeople:pick1num,
//			pick2Amount:pick2amount, pick2NumPeople:pick2num, eventKey: q.eventKey)
//		
//		
//		playerAccount.addToBet(bet)
//		q.addToBet(bet)
//
//		
//		playerAccount.currentBalance -= playerWager
//		
//		if (!playerAccount.save(failOnError:true)){
//			System.out.println("---------------account save failed")
//			return [code:202, message: "account data is not saved"]
//		}
//		
//		if (!q.save(failOnError:true)){
//			System.out.println("---------------q save failed")
//			return [code:202, message: "Question data is not saved"]
//		}
//		
//		return [code:201]
//    }
	

	/**
	 * 
	 * 
	 * @param qId
	 * @return the transaction information that was made the last in a question
	 */
//	def getLatestBetByQuestionId(def qId){
//		def lastBets = BetTransaction.findAll("from BetTransaction as b WHERE id = (select max(id) from b where question_id=?)", [qId])
//		
//		if (lastBets.size()==0){
//		
//			def lastUpdate=new Date()
//			return [pick1Amount:0,pick2Amount:0,pick1NumPeople:0,pick2NumPeople:0, lastUpdate:lastUpdate]
//		}
//				
//		return lastBets.get(0)
//				
//	}
	
	def listPayoutTransByUserId(def userId){
		return listPayoutTransByUserId(userId, 0)
	}
	
	//periodOption:
	//	0=all
	//	1=monthly
	//	2=weekly
	/**
	 * get all the payout transactions (transactionType=1) for a given user and in a perid of time
	 * 		0=all
	 *		1=monthly
	 *		2=weekly
	 * @param userId
	 * @param periodOption
	 * @return
	 */
	def listPayoutTransByUserId(def userId, def periodOption){
		if (periodOption==0)
			return PoolTransaction.findAll("from PoolTransaction as b where b.account.userId=? and b.transactionType=?", [userId, 1])
		if (periodOption==1)
			return PoolTransaction.findAll("from PoolTransaction as b where b.account.userId=? and b.transactionType=? and (date between DATE_FORMAT(NOW() ,'%Y-%m-01') AND NOW())", [userId, 1])
		if (periodOption==2)
			return PoolTransaction.findAll("from PoolTransaction as b where b.account.userId=? and b.transactionType=? and (date between subdate(now(), INTERVAL weekday(now()) DAY) AND NOW())", [userId, 1])
	}
	
	BetTransaction getBetByQuestionIdAndUserId(def qId, def userId){
		return BetTransaction.find("from BetTransaction as b where b.question.id=? and b.account.userId=?", [qId, userId])
	}
	
	List<BetTransaction> listAllBetsByQId(def qId){
		return BetTransaction.findAll("from BetTransaction as b where b.question.id=?", [qId], [cache: true])
	}
	
	List<BetTransaction> listAllBetsByPickAndQId(def qId, int pick){
		return BetTransaction.findAll("from BetTransaction as b where b.question.id=? and b.pick=?", [qId, pick])
	}
	
	/**
	 * List all the bets transaction that the system hasn't process for the payout yet with given gameId and userId 
	 * 
	 * @param gameId
	 * @param userId
	 * @return betsThatNotPayoutYet
	 */
	List<BetTransaction> listBetsByUserIdAndGameId(String gameId, String userId){
		List<BetTransaction> bt = BetTransaction.findAll("from BetTransaction as b where b.account.userId=? and b.eventKey=?", [userId, gameId])
		return bt
	}
	
	/**
	 * List all the bets transaction that the system hasn't process for the payout yet with given userId 
	 * @param userId
	 * @return
	 */
	def listUnpaidBetsByUserId(String userId){
		 
		 List betsThatNotPayoutYet = []
		 def earliestTransDateThatUserCanBetOnAUpcomingGame = new Date() - sportsDataService.UPCOMING_DATE_RANGE - 2;
		 List payoutTransactions = PoolTransaction.findAll("from PoolTransaction as b where b.transactionType=? and b.account.userId=? and b.createdAt>?", [1, userId, earliestTransDateThatUserCanBetOnAUpcomingGame])
		 List betTransactions = BetTransaction.findAll("from BetTransaction as b where b.account.userId=? and b.createdAt>?", [userId, earliestTransDateThatUserCanBetOnAUpcomingGame])
		 
		 for (PoolTransaction betTrans: betTransactions){
			 boolean transactionPaid = false
			 for (PoolTransaction payoutTrans: payoutTransactions){
				 if (betTrans.eventKey == payoutTrans.eventKey){
					 transactionPaid = true
					 break
				 }
			 }
			 
			 if (!transactionPaid){
				 betsThatNotPayoutYet.add(betTrans)
			 }
		 }
		 
		 return betsThatNotPayoutYet
	}
	
	
	/**
	 * List all the event keys that user has bet on
	 * @param userId
	 * @return
	 * 
	 * TODO: upcoming games: return only the transaction date < game start date
	 * 		 past games: return only the SportsDataService.PAST_DATE_RANGE > transaction date > game start date
	 */
	def listDistinctBetEventKeyByUserId(def userId){
		return BetTransaction.executeQuery("SELECT DISTINCT eventKey from BetTransaction as b where b.account.userId=?", [userId])
	}
	
	def getLastUpdatedBetTransactionDateByQId(def qId){
		def c = BetTransaction.createCriteria()
		def lastUpdatedBetTransDate = c.get{
			projections{
				max "createdAt"
			}
		}
		
		return lastUpdatedBetTransDate
	}
	
	
	/**
	 * Validate Bet Transaction
	 * 
	 * @param playerWager
	 * @param betCreatedAt
	 * @param playerPick
	 * @param account
	 * @param question
	 * @param betTrans
	 * @return
	 */
	private Map validateBetTrans(int playerWager, int playerPick, Account account, Question question, BetTransaction betTrans){
		if (!account){
			return [code:202, error: "the userId does not exsist"]
		}
		
		if (account.currentBalance < playerWager){
			return [code:202, error: "The user does not have enough coins to make a bet"]
		}
		
		if (playerWager <= 0){
			return [code:202, error: "The user cannot bet negative amount"]
		}
		
		if (playerPick < 1 || playerPick > 2){
			return [code:202, error: "the pick is not available"]
		}
		
		if (!question){
			return [code:202, error: "the questionId does not exsist"]
		}
		
		if (betTrans){
			return [code:202, error: "the bet transaction already exsists"]
		}
		
		def game = sportsDataService.getGame(question.eventKey)
		if (game!=[]){
			if (game.gameStatus != sportsDataService.PREEVENT){
				return [code:202, error: "the match is already started. All pool is closed"]
			}
		}else{
			def customGame = customGameService.getGame(question.eventKey)
			if (customGame.gameStatus != sportsDataService.PREEVENT){
				return [code:202, error: "the match is already started. All pool is closed"]
			}
		}
		
		return [:]
	}

}
