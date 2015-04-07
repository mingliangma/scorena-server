package com.doozi.scorena.admin

import com.doozi.scorena.Account
import com.doozi.scorena.Question
import com.doozi.scorena.utils.*
import org.springframework.transaction.annotation.Transactional

//@Transactional
class SimulateBetService {
	def betTransactionService
	def gameService
	
	def simulateBetUpcoming(){
		log.info "simulateBetUpcoming() begins..."
		
		Random random = new Random()
//		def accounts = Account.findAllByAccountType(AccountType.TEST)
		def accounts = Account.findAll("from Account as a where a.accountType=? and a.currentBalance>?", [AccountType.TEST, 100])
		def upcomingGames = gameService.listUpcomingGamesData("all", "all")
		int betCounter = 0
		for (int i=0; i < upcomingGames.size(); i++){
			if (random.nextInt(4) == 1){
				continue
			}
			def upcomingGame = upcomingGames.get(i)			
			def questions = Question.findAllByEventKey(upcomingGame.gameId)
			
			for (Question q: questions){

				long questionId = q.id
				betCounter =+ simulateBetOnAQuestion(q, accounts, random)
			}
		}
		
		log.info "simulateBetUpcoming(): ends"
		return betCounter
	}
	
	def simulateBetOnAQuestion(Question q, def accounts, Random random){
		int pick
		if (random.nextInt(2)==0){
			pick=1
		}else{
			pick=2
		}
		return simulateBetOnAQuestion(q, accounts, random, pick, false)
	}
	
	def simulateBetOnAQuestion(Question q, def accounts, Random random, int pick, boolean isAllTestUsers){
		log.info "simulateBetOnAQuestion() starts with questionId="+q.id
		long questionId = q.id
		int betCounter = 0
		for (Account account: accounts){
			if (!isAllTestUsers){
				if (random.nextInt(2) != 1){
					continue
				}
			}
			
			int _wager =  (random.nextInt(6)+1)*20
			
			if (account.currentBalance < _wager ){
				continue
			}	
			
			betTransactionService.createBetTrans(_wager,pick, account.userId, questionId)	
			betCounter++
		}
		log.info "simulateBetOnAQuestion() ends"
		return betCounter
	}
}
