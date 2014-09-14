package com.doozi.scorena.gameengine

import java.util.Map;

import com.doozi.scorena.PoolTransaction

import grails.transaction.Transactional

@Transactional
class QuestionUserInfoService {
	def betService
	def processEngineImplService
	def questionUserInfoService
	def questionPoolUtilService
	Map getQuestionsUserInfo(String userId, long questionId, int winnerPick){
		
		boolean placedBet
		int userPick
		int userPickStatus
		int userWager
		int questionWinningAmount
		
		PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(questionId, userId)
		
		if (!userBet){
			placedBet = false
			userPick = -1
			userPickStatus = -1
			userWager = 0
			questionWinningAmount = 0
		}else{
			placedBet = true
			userPick=userBet.pick
			userPickStatus = getUserPickStatus(winnerPick, userBet.pick)
			userWager = userBet.transactionAmount
			questionWinningAmount = getProfitInQuestion(userBet) 
		}

		return [placedBet:placedBet, userPickStatus:userPickStatus, userPick:userPick, questionWinningAmount:questionWinningAmount, userWager:userWager]
	}
	
	int getProfitInQuestion(PoolTransaction bet){		
		return getProfitInQuestion(bet.question.eventKey, bet)
	}
	
	int getProfitInQuestion(String gameId, PoolTransaction bet){
		int profitAmount = 0
		int winnerPick = processEngineImplService.getWinningPick(gameId, bet.question)
		if (questionUserInfoService.getUserPickStatus(winnerPick, bet.pick) == 1){
			if (bet.pick == 1){
				profitAmount = bet.transactionAmount * (questionPoolUtilService.calculatePick1PayoutMultiple(bet.question.id)-1)
			}else if (bet.pick == 2){
				profitAmount = bet.transactionAmount * (questionPoolUtilService.calculatePick2PayoutMultiple(bet.question.id)-1)
			}
			
		}else if (questionUserInfoService.getUserPickStatus(winnerPick, bet.pick) == 2){
			profitAmount = bet.transactionAmount * -1
		}
		return profitAmount
	}
	
	/**		
			return user's winning or lost status if game result is available.
				winnerPick = -1, the game has not finished yet, the winnerPick is not available
				winnerPick = 0, the game is tied
			  	winnerPick = 1, pick 1 won the game
			  	winnerPick = 2, pick 2 won the game
			
				userPickStatus = 0, it's a tie, return coins back to user
				userPickStatus = 1, user won
				userPickStatus = 2, user lost
				
	 * @param wicnnerPick
	 * @param userPick
	 * @return
	 */
	public int getUserPickStatus(int winnerPick, int userPick){
		int userPickStatus = -1
		
		if (winnerPick == -1){
			userPickStatus = -1
		}else if (winnerPick == 0){
			userPickStatus = 0
		}else if (winnerPick == userPick){
			userPickStatus = 1
		}else{
			userPickStatus = 2
		}
		return userPickStatus
	}
	
	Map getPostEventQuestionUserInfo(String userId, long questionId, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple,
		pick1WinningPayoutPercentage,pick2WinningPayoutPercentage){
		
		def userWinningAmount = 0
		def userPayoutPercent = 0
		def userBetAmount = 0
		def userPick =-1
		
		PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(questionId, userId)
		
		if (userBet!= null){
			if (userBet.pick==1){
				userWinningAmount = Math.floor(userBet.transactionAmount * pick1WinningPayoutMultiple)
				userPayoutPercent = pick1WinningPayoutPercentage
			}else{
				userWinningAmount = Math.floor(userBet.transactionAmount * pick2WinningPayoutMultiple)
				userPayoutPercent = pick2WinningPayoutPercentage
			}
			
			userBetAmount=userBet.transactionAmount
			userPick=userBet.pick
		}
		return [userWinningAmount:userWinningAmount, userPayoutPercent:userPayoutPercent, userWager:userBetAmount, userPick:userPick]
	
	}
		
	Map getPreEventQuestionUserInfo(String userId, long questionId){
		
		def userBetAmount = 0
		def userPick =-1
		PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(questionId, userId)
		if (userBet){
			userBetAmount=userBet.transactionAmount
			userPick=userBet.pick
		}
		return [userWager:userBetAmount, userPick:userPick]
		
	}
	

}
