package com.doozi.scorena.controllerservice

import com.doozi.scorena.BetTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional

@Transactional
class QuestionService {
	def betService
	def listQuestions(gameId) {
		def game = Game.findById(gameId, [fetch:[question:"eager"]])
		List resultList = []
		
		
			for (Question q: game.question){
				def lastBet = betService.getLatestBetByQuestionId(q.id.toString())
				resultList.add([
					id: q.id,
					content: q.content,
					pick1: q.pick1,
					pick2: q.pick2,
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

