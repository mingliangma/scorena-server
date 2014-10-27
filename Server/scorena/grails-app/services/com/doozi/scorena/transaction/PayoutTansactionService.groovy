package com.doozi.scorena.transaction

import com.doozi.scorena.Account;
import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction

import grails.transaction.Transactional


class PayoutTansactionService {	
	def sportsDataService
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
	 * @return -1 on error, 0 on success
	 */
	@Transactional
	def createPayoutTrans(Account playerAccount, Question q, int payout, int winnerPick, int wager, int userPick, int playResult, Date gameStartTime){		
//		println "PayoutTansactionService::createPayoutTrans(): qId="+q.id + ",accountId=" + playerAccount.id+", payout="+payout
		
		PayoutTransaction payoutTransaction = new PayoutTransaction(transactionAmount: payout, createdAt: new Date(), winnerPick: winnerPick, pick: userPick, initialWager:wager, 
			eventKey: q.eventKey, playResult: playResult, league: sportsDataService.getLeagueCodeFromEventKey(q.eventKey), profit: payout-wager, gameStartTime:gameStartTime)
		
		def newBalance = playerAccount.currentBalance + payout
		playerAccount.previousBalance = playerAccount.currentBalance
		playerAccount.currentBalance = newBalance
				
		playerAccount.addToTrans(payoutTransaction)
		q.addToPayoutTrans(payoutTransaction)
		int result = 0
		if (!playerAccount.save()){
			    playerAccount.errors.each {
			        println it
			    }
			result = -1
		}
		
		if (!q.save()){
			    q.errors.each {
			        println it
			    }
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
