package com.doozi.scorena.gameengine

import com.doozi.scorena.Account;
import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction

import grails.transaction.Transactional

@Transactional
class PayoutService {	
	
	def questionUserInfoService
	
	/**
	 * 
	 * Create a payout transaction
	 * @param playerAccount
	 * @param q
	 * @param payout
	 * @param winnerPick
	 * @param wager
	 * @param userPick
	 * @return
	 */
	def createPayoutTrans(Account playerAccount, Question q, int payout, int winnerPick, int wager, int userPick){
		
		int playResult = questionUserInfoService.getUserPickStatus(winnerPick, userPick)
		PayoutTransaction payoutTransaction = new PayoutTransaction(transactionAmount: payout, createdAt: new Date(), winnerPick: winnerPick, pick: userPick, initialWager:wager, eventKey: q.eventKey, playResult: playResult)
		
		playerAccount.addToTrans(payoutTransaction)
		q.addToPayoutTrans(payoutTransaction)
		int result = 0
		if (!playerAccount.merge()){
			println "ERROR: payout transaction failed to be added to player account"
			result = -1
		}
		
		if (!q.merge()){
			println "ERROR: payout transaction failed to be added to question"
			result = -1
		}
		
		return result
	}
	
	def getPayoutTransByQuestion(Question q){
		def result = PayoutTransaction.find("from PayoutTransaction as t where (t.question.id=?)",[q.id])
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
