package com.doozi.scorena.controllerservice

import com.doozi.scorena.PoolTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional
import java.text.DecimalFormat

@Transactional
class QuestionService {
	def betService
	def gameService
	def processEngineImplService
	
	def listQuestionsWithPoolInfo(eventKey){
		listQuestionsWithPoolInfo(eventKey, null)
	}
	
	def listQuestionsWithPoolInfo(eventKey, userId) {
		//def game = Game.findById(gameId, [fetch:[question:"eager"]])
		def questions = listQuestions(eventKey)
		
		List resultList = []
		
		
			for (Question q: questions){
				def lastBet = betService.getLatestBetByQuestionId(q.id.toString())
				
				def denominatorPickPerc = lastBet.pick1Amount + lastBet.pick2Amount
				def denominatorPick1Mult = lastBet.pick1Amount
				def denominatorPick2Mult = lastBet.pick2Amount
				
				if (denominatorPickPerc==0)
					denominatorPickPerc=1
					
				if (denominatorPick1Mult==0)
					denominatorPick1Mult=1
					
				if (denominatorPick2Mult==0)
					denominatorPick2Mult=1
				
				def pick1PayoutPercentage = Math.round(100 * lastBet.pick1Amount/denominatorPickPerc)
				def pick2PayoutPercentage =  Math.round(100 * lastBet.pick2Amount/denominatorPickPerc)
				double pick1PayoutMultiple =  (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult
				double pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
				DecimalFormat df = new DecimalFormat("###.##")
				
				
				def game = gameService.getGame(q.eventKey)
				def userInfo=[]
				if (userId!=null){
					boolean placedBet = false
					int userPickStatus = -1
					def userPick =-1
					def winnerPick =-1
					def userBet
					userBet = betService.getBetByQuestionIdAndUserId(q.id, userId)	
					if (userBet){
						placedBet = true
						userPick=userBet.pick
					}							
					if (game.eventStatus.trim() == "post-event"){
						winnerPick = processEngineImplService.getWinningPick(q.eventKey, q)
						if (userBet){																			 

							if (winnerPick==0){
								userPickStatus = 0
							}else if (winnerPick==userBet.pick){
								userPickStatus = 1
							}else{
								userPickStatus = 2
							}
						}
												
					}
					userInfo=[placedBet:placedBet, userPickStatus:userPickStatus, userPick:userPick,winnerPick:winnerPick]
				}
				resultList.add([
					questionId: q.id,
					content: q.questionContent.content,
					pick1: q.pick1,
					pick2: q.pick2,
					userInfo:userInfo,
					pool: [
						pick1Amount: lastBet.pick1Amount,
						pick1NumPeople: lastBet.pick1NumPeople,
						pick1PayoutPercent: pick1PayoutPercentage,
						pick1odds:  df.format(pick1PayoutMultiple).toDouble(),
						pick2Amount:lastBet.pick2Amount,
						pick2NumPeople: lastBet.pick2NumPeople,
						pick2PayoutPercent: pick2PayoutPercentage,
						pick2odds:  df.format(pick2PayoutMultiple).toDouble(),
					]
					
				])
			}
		

		return resultList
	}
	
	def listQuestions(eventKey){
		return Question.findAllByEventKey(eventKey)
	}
	
	
	def getQuestion(qId, userId){
		Question q = Question.findById(qId)
		def game = gameService.getGame(q.eventKey)

		if (game.eventStatus.trim() == "post-event"){
			
			return getPostEventQuestion(q, userId)
		}else{
			
			return getPreEventQuestion(q, userId)
		}
		
		
	}
	
	private def getPostEventQuestion(Question q, String userId){
		PoolTransaction lastBet = betService.getLatestBetByQuestionId(q.id)
		int winnerPick = processEngineImplService.getWinningPick(q.eventKey, q)
		
		
		def pick1PayoutPercentage 
		def pick2PayoutPercentage 
		def pick1PayoutMultiple 
		def pick2PayoutMultiple 
		DecimalFormat df = new DecimalFormat("###.##")
		
		def denominatorPickPerc = lastBet.pick1Amount + lastBet.pick2Amount
		def denominatorPick1Mult = lastBet.pick1Amount
		def denominatorPick2Mult = lastBet.pick2Amount
		
		if (denominatorPickPerc==0)
			denominatorPickPerc=1
			
		if (denominatorPick1Mult==0)
			denominatorPick1Mult=1
			
		if (denominatorPick2Mult==0)
			denominatorPick2Mult=1
		
		switch (winnerPick){
			case 1:

				pick1PayoutPercentage = Math.round(100 * lastBet.pick1Amount / denominatorPickPerc)				
				pick1PayoutMultiple =  (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult		
				pick2PayoutPercentage = 0
				pick2PayoutMultiple = -1
				break
			case 2: 
				pick1PayoutPercentage = 0
				pick2PayoutPercentage = Math.round(100 * lastBet.pick2Amount/denominatorPickPerc)
				pick1PayoutMultiple =  -1
				pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
				break
			case 0:
				pick1PayoutPercentage = 0
				pick2PayoutPercentage = 0
				pick1PayoutMultiple =  1
				pick2PayoutMultiple = 1
				break
			case -1:
				println "QuestionService::getPastEventQuestion()"
				println "ERROR: winner pick error"
				break
		}

		def userInfo = []
		if (userId != null){
			def userWinningAmount = 0
			def userPayoutPercent = 0
			def userBetAmount = 0
			def userPick =-1
			
			PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(q.id, userId)
			if (userBet!= null){
				userWinningAmount = Math.floor(userBet.transactionAmount * pick1PayoutMultiple)
				userPayoutPercent = pick1PayoutPercentage
				userBetAmount=userBet.transactionAmount
				userPick=userBet.pick								
			}
			userInfo=[userWinningAmount:userWinningAmount, userPayoutPercent:userPayoutPercent, userBetAmount:userBetAmount, userPick:userPick]
		}
		
		def currentOddsPick1=1
		def currentOddsPick2=1
		if (lastBet.pick1Amount > lastBet.pick2Amount){
			currentOddsPick1=(lastBet.pick1Amount/lastBet.pick2Amount)
		}else if (lastBet.pick1Amount < lastBet.pick2Amount){
			currentOddsPick2 = lastBet.pick2Amount/lastBet.pick1Amount
		}
		
		def result = [
			questionId: q.id,
			content: q.questionContent.content,
			pick1: q.pick1,
			pick2: q.pick2,
			winnerPick: winnerPick,
			currentOddsPick1:df.format(currentOddsPick1).toDouble(),
			currentOddsPick2:df.format(currentOddsPick2).toDouble(),
			userInfo: userInfo,
			pool: [
				pick1Amount: lastBet.pick1Amount,
				pick1NumPeople: lastBet.pick1NumPeople,
				pick1PayoutPercent: pick1PayoutPercentage,
				pick2Amount:lastBet.pick2Amount,
				pick2NumPeople: lastBet.pick2NumPeople,
				pick2PayoutPercent: pick2PayoutPercentage,
			],
			betters: getBetters(q.id, pick1PayoutMultiple, pick2PayoutMultiple)
		]
		return result
	}
	
	private def getPreEventQuestion(Question q, def userId){
		DecimalFormat df = new DecimalFormat("###.##")
		
		PoolTransaction lastBet = betService.getLatestBetByQuestionId(q.id)
		
		
		def denominatorPickPerc = lastBet.pick1Amount + lastBet.pick2Amount
		def denominatorPick1Mult = lastBet.pick1Amount
		def denominatorPick2Mult = lastBet.pick2Amount
		
		if (denominatorPickPerc==0)
			denominatorPickPerc=1
			
		if (denominatorPick1Mult==0)
			denominatorPick1Mult=1
			
		if (denominatorPick2Mult==0)
			denominatorPick2Mult=1
		
		def pick1PayoutPercentage = Math.round(100 * lastBet.pick1Amount/denominatorPickPerc)
		def pick2PayoutPercentage =  Math.round(100 * lastBet.pick2Amount/denominatorPickPerc)
		def pick1PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult
		def pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
		
		
		def userInfo = []
		if (userId != null){
			def userBetAmount = 0
			def userPick =-1
			PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(q.id, userId)
			if (userBet){
				userBetAmount=userBet.transactionAmount
				userPick=userBet.pick
			}
			userInfo=[userWager:userBetAmount, userPick:userPick]
		}
		
		
		def currentOddsPick1=1
		def currentOddsPick2=1
		if (lastBet.pick1Amount > lastBet.pick2Amount){
			currentOddsPick1=(lastBet.pick1Amount/lastBet.pick2Amount)
		}else if (lastBet.pick1Amount < lastBet.pick2Amount){
			currentOddsPick2 = lastBet.pick2Amount/lastBet.pick1Amount
		}
		
		def result = [
			questionId: q.id,
			content: q.questionContent.content,
			pick1: q.pick1,
			pick2: q.pick2,
			lastUpdate: lastBet.createdAt,
			userInfo:userInfo,
			pool: [
				pick1Amount: lastBet.pick1Amount,
				pick1NumPeople: lastBet.pick1NumPeople,
				pick1PayoutPercent: pick1PayoutPercentage,
				pick2Amount:lastBet.pick2Amount,
				pick2NumPeople: lastBet.pick2NumPeople,
				pick2PayoutPercent: pick2PayoutPercentage,
				currentOddsPick1:df.format(currentOddsPick1).toDouble(),
				currentOddsPick2:df.format(currentOddsPick2).toDouble(),
			],
			betters: getBetters(q.id, pick1PayoutMultiple, pick2PayoutMultiple)
		]
		return result
	}

	
	def getBetters(qId, pick1PayoutMultiple, pick2PayoutMultiple){
		def homeBettersArr = []
		def awayBettersArr = []

		
		def betTransactions = betService.listAllBets(qId)
		
		for (PoolTransaction betTrans: betTransactions){
			if (betTrans.pick==1){
				if (homeBettersArr.size() <=10){
					homeBettersArr.add([
						name:betTrans.account.username,
						wager:betTrans.transactionAmount,
						expectedWinning: Math.round(pick1PayoutMultiple * betTrans.transactionAmount)])
				}
			}else{
				if (awayBettersArr.size() <=10){				
					awayBettersArr.add( [
					name:betTrans.account.username,
					wager:betTrans.transactionAmount,
					expectedWinning: Math.round(pick2PayoutMultiple * betTrans.transactionAmount)])
				}
			}		
		}
		
		
		def betters=[
			pick1Betters: homeBettersArr,
			pick2Betters: awayBettersArr			
		]
		
		return betters
	}
}

