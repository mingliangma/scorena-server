package com.doozi.scorena.admin

import com.doozi.scorena.Account
import com.doozi.scorena.Question
import com.doozi.scorena.utils.*
import grails.transaction.Transactional

//@Transactional
class SimulateBetService {
	def betService
	def gameService
	
	def simulateBetUpcoming(){
		
		Random random = new Random()
		def accounts = Account.findAllByAccountType(AccountType.TEST)
		def upcomingGames = gameService.listUpcomingGames()
		for (int i=0; i < upcomingGames.size(); i++){
			def upcomingGame = upcomingGames.get(i)			
			def questions = Question.findAllByEventKey(upcomingGame.gameId)
			
			for (Question q: questions){
				if (random.nextInt(3) == 1){
					continue
				}
				long questionId = q.id
				
				for (Account account: accounts){
					if (random.nextInt(30) != 1){
						continue
					}
					int _wager =  (random.nextInt(6)+1)*20
					int _pick
					
					if (random.nextInt(2)==0){
						_pick=1
					}else{
						_pick=2
					}
					
					def result = betService.createBetTrans(_wager,_pick, account.userId, questionId)
				}
			}
		}
	}
}
