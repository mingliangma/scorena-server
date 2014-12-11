package com.doozi.scorena.gameengine

import java.util.List;

import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.utils.*

import org.springframework.transaction.annotation.Transactional


class PoolInfoService {
	def betTransactionService
	
	public PoolInfo getQuestionPoolInfo(qId){
		return getQuestionPoolInfo(qId, [])
	}
	
	public PoolInfo getQuestionPoolInfo(qId, List<Map> userFriendsList){
		PoolInfo questionPoolInfo = new PoolInfo()		
		List<BetTransaction> betTransList = betTransactionService.listAllBetsByQId(qId)
		
		Map userFriendsMap = [:]
		for (Map friendProfile : userFriendsList) {
			userFriendsMap.put(friendProfile.userId, friendProfile)
		}
		
		int pick1BetAmount = 0
		int pick2BetAmount = 0
		int Pick1NumPeople = 0
		int Pick2NumPeople = 0
		
		int highestBet
		int highestBetPick
		String highestBetUserId = null
		
		String highestFriendPickUserId=""
		int highestFriendBetAmount = 0
		int highestFriendBetPick = 0
		boolean friendExist = false
		
		for (BetTransaction bet: betTransList){
			if (bet.pick == Pick.PICK1){
				pick1BetAmount += bet.transactionAmount
				Pick1NumPeople++
			}else{
				pick2BetAmount += bet.transactionAmount
				Pick2NumPeople++
			}
			
			if (userFriendsMap.containsKey(bet.account.userId)){
				if (bet.transactionAmount > highestFriendBetAmount){
					highestFriendPickUserId = bet.account.userId
					highestFriendBetPick = bet.pick
					highestFriendBetAmount = bet.transactionAmount
					friendExist = true
				}
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
		if (highestBetUserId != null){
			questionPoolInfo.setHighestBetAmount(highestBet)
			questionPoolInfo.setHighestBetUserId(highestBetUserId)
			questionPoolInfo.setHighestBetPick(highestBetPick)
		}
		if (friendExist){
			questionPoolInfo.setFriendBetAmount(highestFriendBetAmount)
			questionPoolInfo.setFriendBetPick(highestFriendBetPick)
			questionPoolInfo.setFriendBetUserId(highestFriendPickUserId)
			questionPoolInfo.setFriendPictureUrl(userFriendsMap.get(highestFriendPickUserId).pictureURL)
			questionPoolInfo.setFriendsExist(true)
		}
		
//		println "last updated at: "+betTransactionService.getLastUpdatedBetTransactionDateByQId(qId)
		
		return questionPoolInfo
	}
	
	
}
