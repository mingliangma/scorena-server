package com.doozi.scorena.transaction

import com.doozi.scorena.Account;
import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction

import org.springframework.transaction.annotation.Transactional



class PayoutTansactionService {	
	def sportsDataService
	def questionUserInfoService
	def userService
	
	/**
	 * 
	 * Create a payout transaction
	 * @param playerAccount
	 * @param q
	 * @param payout
	 * @param winnerPick
	 * @param wager
	 * @param userPick
	 * @return -1 on error, 0 on success
	 */
	@Transactional
	def createPayoutTrans(Account playerAccount, Question q, int payout, int winnerPick, int wager, int userPick, int playResult, Date gameStartTime){		
		log.info "createPayoutTrans(): begins with qId = ${q.id}, userId = ${playerAccount.id}, payout = ${payout}"
		
		try{
			PayoutTransaction payoutTransaction = new PayoutTransaction(transactionAmount: payout, createdAt: new Date(), winnerPick: winnerPick, pick: userPick, initialWager:wager, 
				eventKey: q.eventKey, playResult: playResult, league: sportsDataService.getLeagueCodeFromEventKey(q.eventKey), profit: payout-wager, gameStartTime:gameStartTime)
			
				
//			println "PayoutTansactionService: createPayoutTrans():: Acquiring Account lock for userId=" + playerAccount.userId
			Account lockedAccount = Account.findByUserId(playerAccount.userId, [lock: true])
//			println "PayoutTansactionService: createPayoutTrans():: SUCCESSFULLY acquired Account lock for userId=" + playerAccount.userId
			lockedAccount.addToTrans(payoutTransaction)
			lockedAccount.previousBalance = playerAccount.currentBalance
			lockedAccount.currentBalance += payout
			q.addToPayoutTrans(payoutTransaction)
			
			if (!payoutTransaction.validate()) {
				payoutTransaction.errors.each {
					println it
				}
				PayoutTransaction.withSession { session ->
					session.clear()
				}
				log.error "createPayoutTrans(): the bet transaction already exsists"
				return [code:202, error: "the bet transaction already exsists"]
			}
			
			if (!lockedAccount.validate()) {
				String errorMessage = ""
				lockedAccount.errors.each {
					println it
					errorMessage += it
				}
				PayoutTransaction.withSession { session ->
					session.clear()
				}
				log.error "createPayoutTrans(): createBetTran error: ${errorMessage}"
				return [code:202, error: "createBetTran error: "+errorMessage]
			}
			
			lockedAccount.save(flush: true)
			q.save(flush: true)
			return [:]
			
		}catch(org.springframework.dao.CannotAcquireLockException e){
			log.error "createPayoutTrans(): CannotAcquireLockException ERROR: ${e.message}"
			throw e
		}
	}
	
	def getPayoutTransByQuestion(Question q){
		def result = PayoutTransaction.find("from PayoutTransaction as t where (t.question.id=?)",[q.id])
	}
	
	List<PayoutTransaction> listPayoutTransByGameId(String eventKey){
		def result = PayoutTransaction.findAll("from PayoutTransaction as t where (t.eventKey=?)",[eventKey])
	}
	
	List<PayoutTransaction> listPayoutTransByGameIdAndUserId(String eventKey, String userId){
		def result = PayoutTransaction.findAll("from PayoutTransaction as t where (t.eventKey=? and t.account.userId=?)",[eventKey, userId])
	}
	
	def listPayoutTransByUserId(def userId){
		return listPayoutTransByUserId(userId, 0)
	}
	
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
			return PayoutTransaction.findAll("from PayoutTransaction as b where b.account.userId=?", [userId])
		if (periodOption==1)
			return PayoutTransaction.findAll("from PayoutTransaction as b where b.account.userId=? and (date between DATE_FORMAT(NOW() ,'%Y-%m-01') AND NOW())", [userId])
		if (periodOption==2)
			return PayoutTransaction.findAll("from PayoutTransaction as b where b.account.userId=? and (date between subdate(now(), INTERVAL weekday(now()) DAY) AND NOW())", [userId])
	}
}
