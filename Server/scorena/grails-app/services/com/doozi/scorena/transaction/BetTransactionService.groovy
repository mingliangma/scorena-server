package com.doozi.scorena.transaction

import java.util.Date;

import com.doozi.scorena.Account;
import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction

import org.hibernate.criterion.CriteriaSpecification

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional


class BetTransactionService {
	def sportsDataService
	def gameService
	def customGameService	
	def helperService
	def pushService
	
	Map createBetTrans(int playerWager, int playerPick, String userId, long quesitonId) {
		return createBetTrans( playerWager, playerPick, userId, quesitonId, new Date(), true)
	}
	
	@Transactional
	Map createBetTrans(int playerWager, int playerPick, String userId, long quesitonId, Date transactionDate, boolean toValidate) {
		
		Account playerAccount = Account.findByUserId(userId)
		Question question = Question.findById(quesitonId)
		Map game = gameService.getGame(question.eventKey)
		if (toValidate){
			//Find the bet transaction that associated with the given userId and questionId 
			BetTransaction betTrans = BetTransaction.find("from BetTransaction as b where (b.question.id=? and b.account.id=?)", question.id, playerAccount.id)
			
			Map validationResult =  validateBetTrans(playerWager, playerPick, playerAccount, question, betTrans, game)		
			
			if (validationResult!=[:]){
				return validationResult
			}
		}
		
		BetTransaction newBetTransaction = new BetTransaction(transactionAmount: playerWager, createdAt: transactionDate, 
			pick: playerPick, eventKey: question.eventKey, league: sportsDataService.getLeagueCodeFromEventKey(question.eventKey), gameStartTime:helperService.parseDateFromString(game.date))
		
		//String awayTeam = game.away.teamname
		//String homeTeam = game.home.teamname
		
		
		
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
		
		def rest = new RestBuilder()
		// gets the users decvice installation ID by username
		String objectID = pushService.getInstallationByUsername(rest, playerAccount.username)
		
		// preps event key for pars channel. parse does not allow for  '.' in a channel name, replase it with a "_"
		String parse_channel = question.eventKey.replace(".","_")
		
		// Registers user device into push channel for game event key
		def test = pushService.updateGameChannel(rest, objectID, parse_channel)

		return [:]
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
		 List payoutTransactions = PayoutTransaction.findAll("from PayoutTransaction as b where b.account.userId=? and b.createdAt>?", [userId, earliestTransDateThatUserCanBetOnAUpcomingGame])
		 List betTransactions = BetTransaction.findAll("from BetTransaction as b where b.account.userId=? and b.createdAt>?", [userId, earliestTransDateThatUserCanBetOnAUpcomingGame])
		 
		 for (BetTransaction betTrans: betTransactions){
			 boolean transactionPaid = false
			 for (PayoutTransaction payoutTrans: payoutTransactions){
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
	
	List<BetTransaction> listBetTransByUserIdAndGameIds(def userId, List gameIds){
		return BetTransaction.executeQuery("from BetTransaction as b where b.account.userId=:userId and b.eventKey in :gameIds", [userId:userId, gameIds:gameIds])
	}
	
	List<BetTransaction> listBetTransByGameIds(List gameIds){
		return BetTransaction.executeQuery("from BetTransaction as b where b.eventKey in :gameIds", [gameIds:gameIds])
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
	private Map validateBetTrans(int playerWager, int playerPick, Account account, Question question, BetTransaction betTrans, Map game){
		if (!account){
			return [code:202, error: "the userId does not exsist"]
		}
		
		if (account.currentBalance < playerWager){
			return [code:202, error: "The user does not have enough coins to make a bet"]
		}
		
		if (playerWager < 0){
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
		
		if (game.gameStatus != sportsDataService.PREEVENT_NAME){
			
			return [code:202, error: "the match is already started. All pool is closed"]
		}
		
		
		return [:]
	}

}
