package com.doozi.scorena.gameengine

import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.utils.*
import grails.transaction.Transactional


class PoolInfoService {
	def betService
	
	public PoolInfo getQuestionPoolInfo(qId){
		PoolInfo questionPoolInfo = new PoolInfo()
		List<BetTransaction> pick1BetTransList = betService.listAllBetsByPickAndQId(qId, Pick.PICK1)
		List<BetTransaction> pick2BetTransList = betService.listAllBetsByPickAndQId(qId, Pick.PICK2)
		
		int pick1BetAmount = 0
		int pick2BetAmount = 0
		
		for (BetTransaction pick1bt: pick1BetTransList){
			pick1BetAmount += pick1bt.transactionAmount
		}
		
		for (BetTransaction pick2bt: pick2BetTransList){
			pick2BetAmount += pick2bt.transactionAmount
		}
		
		questionPoolInfo.setPick1Amount(pick1BetAmount)
		questionPoolInfo.setPick2Amount(pick2BetAmount)
		questionPoolInfo.setPick1NumPeople(pick1BetTransList.size())
		questionPoolInfo.setPick2NumPeople(pick2BetTransList.size())
		println "last updated at: "+betService.getLastUpdatedBetTransactionDateByQId(qId)
		
		return questionPoolInfo
	}
}
