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
		def accounts = Account.findAllByAccountType(AccountType.TEST)
		def upcomingGames = gameService.listUpcomingGamesData("all", "all")
		for (int i=0; i < upcomingGames.size(); i++){
			if (random.nextInt(4) == 1){
				continue
			}
			def upcomingGame = upcomingGames.get(i)			
			def questions = Question.findAllByEventKey(upcomingGame.gameId)
			
			for (Question q: questions){

				long questionId = q.id
				
				for (Account account: accounts){
					if (random.nextInt(5) != 1){
						continue
					}
					
					int _wager =  (random.nextInt(6)+1)*20
					int _pick
					
					if (random.nextInt(2)==0){
						_pick=1
					}else{
						_pick=2
					}
					
					def result = betTransactionService.createBetTrans(_wager,_pick, account.userId, questionId)
					if (result == [:])
						println "SimulateBetService:simulateBetUpcoming():: successfully bet. userId="+account.username+" questionId="+questionId
					else
						println "SimulateBetService:simulateBetUpcoming():: bet failed. "+result
						
				}
			}
		}
		
		log.info "simulateBetUpcoming(): ends"
	}
}
