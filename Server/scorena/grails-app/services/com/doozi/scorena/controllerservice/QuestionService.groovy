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
	def questionService
	
	def listFeatureQuestions(userId){
		def questionList = listUpcomingQuesitonsByMostPeopleBetOn()
		def featureQuestions = getUnpickMostBetQuestions(questionList, userId, 3)
		return constructFeatureGameData(featureQuestions)
		
	}
	
	private def constructFeatureGameData(featureQuestions){
		List featureQuestionResponse = []
		for (int i=0; i<featureQuestions.size(); i++){
			def gameIdAndQuestionIdArray = featureQuestions.get(i)
			def game = gameService.getGame(gameIdAndQuestionIdArray[0])
			game.question = questionService.getQuestionWithPoolInfo(gameIdAndQuestionIdArray[0], gameIdAndQuestionIdArray[1])
			featureQuestionResponse.add(game)
		}
		return featureQuestionResponse
	}
	
	//return list of [(eventKey),(quesitonId), (transactionTypeSum), (BetCount)]
	private def listUpcomingQuesitonsByMostPeopleBetOn(){
		return PoolTransaction.executeQuery("select eventKey, question.id ,sum(transactionType) as type1, count(*) as betCount "+
			"from PoolTransaction as t1 group by 1,2 order by betCount desc limit 100")
	}
	
	private def getUnpickMostBetQuestions(def questions, String userId, int limit){
		int i = 0
		int numUsersBet = 0
		List qList = []
		for (def q: questions){
			if (q.getAt(2) == 0){
				if (userId!=null && userId!=""){
					def userBet = betService.getBetByQuestionIdAndUserId(q[1], userId)
					if (userBet != null){
						continue
					}
				}
				qList.add([q[0], q[1]])
				i++
				if (i>=limit){
					break
				}
			}
		}		
		return qList
	}
	
	def getQuestionWithPoolInfo(eventKey, qId){
		List questions = listQuestionsWithPoolInfo(eventKey)
		for (def q: questions){
			if (q.questionId == qId){
				return q
			}
		}
		return []		
	}
	
	
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
				def winnerPick =-1
				if (game.eventStatus.trim() == "post-event"){
					winnerPick = processEngineImplService.getWinningPick(q.eventKey, q)
				}
				def userInfo=[]
				if (userId!=null){
					boolean placedBet = false
					int userPickStatus = -1
					def userPick =-1
					
					def userBet
					userBet = betService.getBetByQuestionIdAndUserId(q.id, userId)	
					if (userBet){
						placedBet = true
						userPick=userBet.pick
						if (winnerPick!=-1){
							
							if (winnerPick==0){
								userPickStatus = 0
							}else if (winnerPick==userBet.pick){
								userPickStatus = 1
							}else{
								userPickStatus = 2
							}
						}
					}							
				
					
												
					
					userInfo=[placedBet:placedBet, userPickStatus:userPickStatus, userPick:userPick]
				}
				resultList.add([
					questionId: q.id,
					content: q.questionContent.content,
					pick1: q.pick1,
					pick2: q.pick2,
					userInfo:userInfo,
					winnerPick:winnerPick,
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
		def pick1WinningPayoutPercentage 
		def pick2WinningPayoutPercentage 
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
			
		pick1PayoutPercentage = Math.round(100 * lastBet.pick1Amount/denominatorPickPerc)
		pick2PayoutPercentage =  Math.round(100 * lastBet.pick2Amount/denominatorPickPerc)
		
		switch (winnerPick){
			case 1:

				pick1WinningPayoutPercentage = Math.round(100 * lastBet.pick1Amount / denominatorPickPerc)				
				pick1PayoutMultiple =  (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult		
				pick2WinningPayoutPercentage = 0
				pick2PayoutMultiple = -1
				break
			case 2: 
				pick1WinningPayoutPercentage = 0
				pick2WinningPayoutPercentage = Math.round(100 * lastBet.pick2Amount/denominatorPickPerc)
				pick1PayoutMultiple =  -1
				pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
				break
			case 0:
				pick1WinningPayoutPercentage = 0
				pick2WinningPayoutPercentage = 0
				pick1PayoutMultiple =  1
				pick2PayoutMultiple = 1
				break
			case -1:
				println "QuestionService::getPastEventQuestion()"
				println "ERROR: winner pick error"
				break
		}
		
		def userInfo = []
		def username=""
		if (userId != null){
			def userWinningAmount = 0
			def userPayoutPercent = 0
			def userBetAmount = 0
			def userPick =-1
			
			PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(q.id, userId)
			if (userBet!= null){
				if (userBet.pick==1){
					userWinningAmount = Math.floor(userBet.transactionAmount * pick1PayoutMultiple)
					userPayoutPercent = pick1WinningPayoutPercentage
				}else{
					userWinningAmount = Math.floor(userBet.transactionAmount * pick2PayoutMultiple)
					userPayoutPercent = pick2WinningPayoutPercentage
				}
				
				userBetAmount=userBet.transactionAmount
				userPick=userBet.pick	
				username=userBet.account.username				
			}
			userInfo=[userWinningAmount:userWinningAmount, userPayoutPercent:userPayoutPercent, userWager:userBetAmount, userPick:userPick]
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
			betters: getBetters(q.id, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, username)
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
		def username = ""
		if (userId != null && userId!=""){
			def userBetAmount = 0
			def userPick =-1
			PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(q.id, userId)
			if (userBet){
				userBetAmount=userBet.transactionAmount
				userPick=userBet.pick
				username=userBet.account.username
			}
			userInfo=[userWager:userBetAmount, userPick:userPick]
		}
		
		def currentOddsPick1=1
		def currentOddsPick2=1
		if (lastBet.pick1Amount > lastBet.pick2Amount){
			currentOddsPick1=(lastBet.pick1Amount/denominatorPick2Mult)
		}else if (lastBet.pick1Amount < lastBet.pick2Amount){
			currentOddsPick2 = lastBet.pick2Amount/denominatorPick1Mult
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
			betters: getBetters(q.id, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, username)
		]
		return result
	}

	
	def getBetters(qId, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, username){
		def homeBettersArr = []
		def awayBettersArr = []
		def currentUserAdded = false
		
		def betTransactions = betService.listAllBets(qId)
		
		for (PoolTransaction betTrans: betTransactions){
			if (betTrans.pick==1){
				if (homeBettersArr.size() <=10){
					if (betTrans.account.username != username){
						
						homeBettersArr.add([
							name:betTrans.account.username,
							wager:betTrans.transactionAmount,
							expectedWinning: Math.round(pick1PayoutMultiple * betTrans.transactionAmount)])
					}
				}
			}else{
				if (awayBettersArr.size() <=10){	
					if (betTrans.account.username != username){
					
						awayBettersArr.add( [
							name:betTrans.account.username,
							wager:betTrans.transactionAmount,
							expectedWinning: Math.round(pick2PayoutMultiple * betTrans.transactionAmount)])
				
					}
				}
			}
			if (awayBettersArr.size() >10 && homeBettersArr.size() >10)
				break
		}
		
		
		if ( currentUserAdded == false && userInfo!=[] && username!=""){
			if (userInfo.userPick==1){
				homeBettersArr.add(0,[name:username, wager:userInfo.userWager,expectedWinning: Math.round(pick1PayoutMultiple * userInfo.userWager)])
			}else{
				awayBettersArr.add(0,[name:username, wager:userInfo.userWager,expectedWinning: Math.round(pick2PayoutMultiple * userInfo.userWager)])
			}
		}
		def betters=[
			pick1Betters: homeBettersArr,
			pick2Betters: awayBettersArr			
		]
		
		return betters
	}
}

