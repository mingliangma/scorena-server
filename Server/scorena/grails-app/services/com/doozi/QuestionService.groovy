package com.doozi

import grails.transaction.Transactional

@Transactional
class QuestionService {

	def listQuestions(gameId) {
		def Games = Game.findById(gameId)
		
		def all = Games.collect {Game game ->
			[	
				question: game.question.collect{ Question q ->
					[
						id: q.id,
						content: q.content,
						pick1: q.pick1,
						pick2: q.pick2,
						pool: [
							pick1Amount: q.pool.pick1Amount,
							pick1NumPeople: q.pool.pick1NumPeople,
							pick2Amount: q.pool.pick2Amount,							
							pick2NumPeople: q.pool.pick1NumPeople
						]					
					]
				}
			]
		  }
		return all
	}
	
	def getQuestion(qId){
		def question = Question.findById(qId)
		
		def all = question.collect { Question q -> [
				
				content: q.content,
				pick1: q.pick1,
				pick2: q.pick2,
				pool: [
					pick1Amount: q.pool.pick1Amount,
					pick1NumPeople: q.pool.pick1NumPeople,
					pick2Amount: q.pool.pick2Amount,
					pick2NumPeople: q.pool.pick1NumPeople],
				betters: getBetters(q.bet)
				
			]			
		}
	}
	
	def getBetters(betTransactions){
		def homeBettersArr = []
		def awayBettersArr = []
		
		
		for (BetTransaction betTrans: betTransactions){
			if (betTrans.pick==0){
				homeBettersArr.add([
					name:betTrans.account.username,
					wager:betTrans.wager])
			}else{
				awayBettersArr.add( [
				name:betTrans.account.username,
				wager:betTrans.wager])
			}		
		}
		def betters=[
			homeBetters: homeBettersArr,
			awayBetters: awayBettersArr			
		]
		
		return betters
	}
}

