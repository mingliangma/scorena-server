package com.doozi.scorena.gameengine

import java.util.List;

import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.utils.*

import org.springframework.transaction.annotation.Transactional


class PoolInfoService {
	def betTransactionService
	
	public PoolInfo getQuestionPoolInfo(qId){
		PoolInfo questionPoolInfo = new PoolInfo()		
		List<BetTransaction> betTransList = betTransactionService.listAllBetsByQId(qId)
		
		int pick1BetAmount = 0
		int pick2BetAmount = 0
		int Pick1NumPeople = 0
		int Pick2NumPeople = 0
		
		int highestBet = 0
		int highestBetPick = 0
		String highestBetUserId =""
		
		for (BetTransaction bet: betTransList){
			if (bet.pick == Pick.PICK1){
				pick1BetAmount += bet.transactionAmount
				Pick1NumPeople++
			}else{
				pick2BetAmount += bet.transactionAmount
				Pick2NumPeople++
			}
			
			if (bet.transactionAmount > highestBet){
				highestBet = bet.transactionAmount
				highestBetUserId = bet.account.userId
				highestBetPick = bet.pick
			}
		}
		questionPoolInfo.setBetTransList(betTransList)
		questionPoolInfo.setPick1Amount(pick1BetAmount)
		questionPoolInfo.setPick2Amount(pick2BetAmount)
		questionPoolInfo.setPick1NumPeople(Pick1NumPeople)
		questionPoolInfo.setPick2NumPeople(Pick2NumPeople)
		questionPoolInfo.setHighestBetAmount(highestBet)
		questionPoolInfo.setHighestBetUserId(highestBetUserId)
		questionPoolInfo.setHighestBetPick(highestBetPick)
		
//		println "last updated at: "+betTransactionService.getLastUpdatedBetTransactionDateByQId(qId)
		
		return questionPoolInfo
	}
	
	
}
