package com.doozi.scorena.gameengine

import java.util.Map;

import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.utils.*
import org.springframework.transaction.annotation.Transactional


@Transactional
class QuestionUserInfoService {
	def betTransactionService
	def processEngineImplService
	def questionUserInfoService
	def questionPoolUtilService
	def poolInfoService
	
	Map getQuestionsUserInfo(PayoutTransaction payout, BetTransaction userBet){
		log.info "getQuestionsUserInfo(): begins with payout = ${payout}, userBet = ${userBet}"
		
		boolean placedBet
		int userPick
		int userPickStatus
		int userWager
		int questionWinningAmount
		boolean isGameProcessed
				
		if (!userBet){ // user did not make a bet
			placedBet = false
			userPick = -1
			userPickStatus = -1
			userWager = -1
			questionWinningAmount = -1
		}else if (userBet && !payout){ //user made a bet but game has not processed yet 
			placedBet = true
			userPick=userBet.pick
			userPickStatus = -1
			userWager = userBet.transactionAmount
			questionWinningAmount = -1
		}else { // user made a bet and the game is processed 
			placedBet = true
			userPick=userBet.pick
			userPickStatus = payout.playResult
			userWager = userBet.transactionAmount
			questionWinningAmount = payout.profit
		}
		
		def questionsUserInfoResult = [placedBet:placedBet, userPickStatus:userPickStatus, userPick:userPick, questionWinningAmount:questionWinningAmount, userWager:userWager]
		log.info "getQuestionsUserInfo(): ends with questionsUserInfoResult= ${questionsUserInfoResult}"

		return [placedBet:placedBet, userPickStatus:userPickStatus, userPick:userPick, questionWinningAmount:questionWinningAmount, userWager:userWager]
	}
	
	int getProfitInQuestion(int winnerPick, BetTransaction bet){	
		log.info "getProfitInQuestion(): begins with winnerPick = ${winnerPick}, bet = ${bet}"	
				
		int profitAmount = 0
		
		PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(bet.question.id)		
		def pick1PayoutMultiple = questionPoolUtilService.calculatePick1PayoutMultiple(questionPoolInfo)
		def pick2PayoutMultiple = questionPoolUtilService.calculatePick2PayoutMultiple(questionPoolInfo)
		
		if (questionUserInfoService.getUserPickStatus(winnerPick, bet.pick) == PickStatus.USER_WON){
			if (bet.pick == Pick.PICK1){
				profitAmount = bet.transactionAmount * (pick1PayoutMultiple-1)
			}else if (bet.pick == Pick.PICK2){
				profitAmount = bet.transactionAmount * (pick2PayoutMultiple-1)
			}
			
		}else if (questionUserInfoService.getUserPickStatus(winnerPick, bet.pick) == PickStatus.USER_LOST){
			profitAmount = bet.transactionAmount * -1
		}
		
		log.info "getProfitInQuestion(): ends with profitAmount = ${profitAmount}"
		
		return profitAmount
	
	}
	
	int getProfitInQuestion(Map game, BetTransaction bet){
		int winnerPick = processEngineImplService.calculateWinningPick(game, bet.question)
		return getProfitInQuestion(winnerPick, bet)

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
		
		if (winnerPick == WinnerPick.GAME_NOT_FINISHED){
			userPickStatus = PickStatus.GAME_NOT_FINISHED
		}else if (winnerPick == WinnerPick.PICK_TIE){
			userPickStatus = PickStatus.USER_TIE
		}else if (winnerPick == userPick){
			userPickStatus = PickStatus.USER_WON
		}else{
			userPickStatus = PickStatus.USER_LOST
		}
		return userPickStatus
	}
	
	Map getPostEventQuestionUserInfo(String userId, long questionId, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple,
		pick1WinningPayoutPercentage,pick2WinningPayoutPercentage){
		
		def userWinningAmount = -1
		def userPayoutPercent = -1
		def userBetAmount = 0
		def userPick =-1
		
		BetTransaction userBet = betTransactionService.getBetByQuestionIdAndUserId(questionId, userId)
		
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
		log.info "getPreEventQuestionUserInfo(): begins with userId = ${userId}, questionId = ${questionId}"
		
		def userBetAmount = 0
		def userPick =-1
		BetTransaction userBet = betTransactionService.getBetByQuestionIdAndUserId(questionId, userId)
		if (userBet){
			userBetAmount=userBet.transactionAmount
			userPick=userBet.pick
		}
		
		def preEventQuestionUserInfoResult = [userWager:userBetAmount, userPick:userPick]
		log.info "getPreEventQuestionUserInfo(): ends with preEventQuestionUserInfoResul = ${preEventQuestionUserInfoResul}"
		
		return [userWager:userBetAmount, userPick:userPick]
		
	}
	

}
