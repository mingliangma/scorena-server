package com.doozi.scorena.transaction

import java.util.Date;

import com.doozi.scorena.Account;
import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.utils.*

import org.hibernate.criterion.CriteriaSpecification

import grails.plugins.rest.client.RestBuilder
import org.springframework.transaction.annotation.Transactional


class BetTransactionService {
	def sportsDataService
	def gameService
	def customGameService	
	def helperService
	def pushService
	def userService
	
	Map createBetTrans(int playerWager, int playerPick, String userId, long quesitonId) {
		
		return createBetTrans( playerWager, playerPick, userId, quesitonId, new Date(), new RestBuilder(), true)
	}
	
	@Transactional
	Map createBetTrans(int playerWager, int playerPick, String userId, long quesitonId, Date transactionDate, def rest, boolean toValidate) {
		log.info "createBetTrans(): begins, playerWager="+playerWager + " playerPick="+playerPick +" userId="+userId + " quesitonId="+quesitonId
		
		try{
			Question question = Question.get(quesitonId)
			Map game = gameService.getGame(question.eventKey)
//			Account playerAccount = Account.findByUserId(userId)
			Account lockedAccount = Account.findByUserId(userId, [lock: true])
			if (toValidate){
				//Find the bet transaction that associated with the given userId and questionId 
				BetTransaction betTrans = BetTransaction.find("from BetTransaction as b where (b.question.id=? and b.account.id=?)", question.id, lockedAccount.id)
				
				Map validationResult =  validateBetTrans(playerWager, playerPick, lockedAccount, question, betTrans, game)		
				
				if (validationResult!=[:]){
					return validationResult
				}
			}
			BetTransaction newBetTransaction = new BetTransaction(transactionAmount: playerWager, createdAt: transactionDate, 
				pick: playerPick, eventKey: question.eventKey, league: sportsDataService.getLeagueCodeFromEventKey(question.eventKey), 
				gameStartTime:helperService.parseDateFromString(game.date))

//			println "BetTransactionService: createBetTrans():: Acquiring Account lock for userId=" + userId
//			Account lockedAccount = Account.findByUserId(userId, [lock: true])
//			println "BetTransactionService: createBetTrans():: SUCCESSFULLY acquired Account lock for userId=" + userId
			lockedAccount.addToTrans(newBetTransaction)	
			lockedAccount.previousBalance = lockedAccount.currentBalance
			lockedAccount.currentBalance -= playerWager
			question.addToBetTrans(newBetTransaction)

			if (!newBetTransaction.validate()) {
				newBetTransaction.errors.each {
					println it
				}
				BetTransaction.withSession { session ->
				    session.clear()
				}
				log.error "createBetTrans(): the bet transaction already exsists"
				return [code:202, error: "the bet transaction already exsists"]
			}
			
			if (!lockedAccount.validate()) {
				String errorMessage = ""
				lockedAccount.errors.each {
					println it
					errorMessage += it
				}
				BetTransaction.withSession { session ->
					session.clear()
				}
				log.error "createBetTrans(): createBetTran error: ${errorMessage}"
				return [code:202, error: "createBetTran error: "+errorMessage]
			}
			lockedAccount.save(flush: true)					
			question.save(flush: true)
			
			log.info "createBetTran successful: userId=${userId} currentBalance=${lockedAccount.currentBalance} currentBalance=${lockedAccount.previousBalance}" 

			if (lockedAccount.accountType == AccountType.USER || lockedAccount.accountType == AccountType.FACEBOOK_USER){
			
				// gets the users decvice installation ID by username
				List objectIDs = pushService.getInstallationByUserID(lockedAccount.userId)
			
				
				if ( objectIDs != null || objectIDs != "" )
				{
					// preps event key for pars channel. parse does not allow for  '.' in a channel name, replaces it with a "_"
					String parse_channel = question.eventKey.replace(".","_")
					
					
					for (String objectId: objectIDs)
					{
					// Registers user device into push channel for game event key
					def test = pushService.updateGameChannel(rest, objectId, parse_channel)
					}
				}
			}
		}catch(org.springframework.dao.CannotAcquireLockException e){
			println "createBetTrans(): CannotAcquireLockException ERROR: "+e.message
			log.error "createBetTrans(): ${e.message}"
			BetTransaction.withSession { session ->
				session.clear()
			}
			log.error "createBetTrans(): Your Bet failed to process, Please try again"
			return [code:202, error: "Your Bet failed to process, Please try again"]
		}catch(org.hibernate.AssertionFailure e2){
			println "createBetTrans(): AssertionFailure ERROR: "+e2.message
			log.error "createBetTrans(): AssertionFailure ERROR: ${e2.message}"
			BetTransaction.withSession { session ->
				session.clear()
			}
			log.error "createBetTrans(): the bet transaction already exsists"
			return [code:202, error: "the bet transaction already exsists"]
		}catch(Exception e3){
			println "BetTransactionService: createBetTrans():: ERROR register user device to push notification channcel. Message: "+e3.message
			log.error "createBetTrans(): register user device to push notification channcel. Message: ${e3.message}"
			BetTransaction.withSession { session ->
				session.clear()
			}
			return [code:202, error: e3.message]
		}
		log.info "createBetTrans(): ends"
		return [:]
	}	
	
	BetTransaction getBetByQuestionIdAndUserId(def qId, def userId){
		return BetTransaction.find("from BetTransaction as b where b.question.id=? and b.account.userId=?", [qId, userId])
	}
	
	List<BetTransaction> listAllFriendsBetsByQId(def qId, List friendUserIds){
		if (friendUserIds.size()>0)
			return BetTransaction.executeQuery("from BetTransaction as b where b.question.id=:qId and b.account.userId in :friendUserIds", [qId:qId, friendUserIds:friendUserIds],   [max: 20])
		else
			return []
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
		log.info "listUnpaidBetsByUserId(): begins with userId = ${userId}"
		 
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
		 
		 log.info "listUnpaidBetsByUserId(): ends with betsThatNotPayoutYet = ${betsThatNotPayoutYet}"
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
		log.info "validateBetTrans(): begins with playerWager=${playerWager} playerPick=${playerPick} balance=${account.currentBalance} questionId=${question.id}"
		
		if (!account){
			log.error "validateBetTrans(): the userId does not exsist"
			return [code:202, error: "the userId does not exsist"]
		}
		
		if (account.currentBalance < playerWager){
			def result = [code:202, error: "The user does not have enough coins to make a bet. Username="+account.username+" user balance="+account.currentBalance
				+ " user wager=" + playerWager]
			log.error "validateBetTrans(): ${result}"
			
			return [code:202, error: "The user does not have enough coins to make a bet. Username="+account.username+" user balance="+account.currentBalance
				+ " user wager=" + playerWager]
		}
		
		if (playerWager < 0){
			log.error "validateBetTrans(): The user cannot bet negative amount"
			return [code:202, error: "The user cannot bet negative amount"]
		}
		
		if (playerPick < 1 || playerPick > 2){
			log.error "validateBetTrans(): the pick is not available"
			return [code:202, error: "the pick is not available"]
		}
		
		if (!question){
			log.error "validateBetTrans(): the questionId does not exsiste"
			return [code:202, error: "the questionId does not exsist"]
		}
		
		if (betTrans){
			log.error "validateBetTrans(): the bet transaction already exsistse"
			return [code:202, error: "the bet transaction already exsists"]
		}
		
		if (game.gameStatus != sportsDataService.PREEVENT_NAME){
			log.error "validateBetTrans(): the match is already started. All pool is closed"
			return [code:202, error: "the match is already started. All pool is closed"]
		}
		
		if (account.id == 33 || account.id == 52 || account.id==47){
			int wagerInBet = userService.getUserInWagerCoins(account.userId)
			log.info "validateBetTrans(): wagerInBet="+wagerInBet
			if ((wagerInBet + account.currentBalance) * 0.1 <  playerWager){
				log.error "validateBetTrans(): user wager exceed 10% of the user's asset"
				return [code:202, error: "user wager exceed 10% of the user's asset"]
			}
		}
		
		log.info "validateBetTrans(): ends..."
		return [:]
	}

}
