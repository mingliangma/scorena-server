package com.doozi.scorena.controllerservice

import com.doozi.scorena.BetTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional

@Transactional
class QuestionService {
	def betService
	def listQuestions(eventId, userId) {
		//def game = Game.findById(gameId, [fetch:[question:"eager"]])
		def questions = Question.findAllByEventId(eventId)
		
		List resultList = []
		
		
			for (Question q: questions){
				def lastBet = betService.getLatestBetByQuestionId(q.id.toString())
				boolean placedBet = false
				if (betService.findAllByQuestionIdAndUserId(q.id, userId)){
					placedBet = true
				}
				resultList.add([
					id: q.id,
					content: q.content,
					pick1: q.pick1,
					pick2: q.pick2,
					placedBet: placedBet,
					pool: [
						pick1Amount: lastBet.pick1Amount,
						pick1NumPeople: lastBet.pick1NumPeople,
						pick2Amount:lastBet.pick2Amount,
						pick2NumPeople: lastBet.pick2NumPeople
					]
				])
			}
		

		return resultList
	}
	
	def getQuestion(qId){
		def q = Question.findById(qId)
		
		def lastBet = betService.getLatestBetByQuestionId(qId.toString())
		def result = [
			id: q.id,
			content: q.content,
			pick1: q.pick1,
			pick2: q.pick2,
			pool: [
				pick1Amount: lastBet.pick1Amount,
				pick1NumPeople: lastBet.pick1NumPeople,
				pick2Amount:lastBet.pick2Amount,
				pick2NumPeople: lastBet.pick2NumPeople
			],
			betters: getBetters(q.bet)
			
		]
	}
	
	def getBetters(betTransactions){
		def homeBettersArr = []
		def awayBettersArr = []
				
		
		for (BetTransaction betTrans: betTransactions){
			if (betTrans.pick==1){
				if (homeBettersArr.size() <=10){
					homeBettersArr.add([
						name:betTrans.account.username,
						wager:betTrans.wager])
				}
			}else{
				if (awayBettersArr.size() <=10){				
					awayBettersArr.add( [
					name:betTrans.account.username,
					wager:betTrans.wager])
				}
			}		
		}
		
		
		def betters=[
			homeBetters: homeBettersArr,
			awayBetters: awayBettersArr			
		]
		
		return betters
	}
}

