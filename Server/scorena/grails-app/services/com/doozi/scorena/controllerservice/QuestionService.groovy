package com.doozi.scorena.controllerservice

import com.doozi.scorena.Pool
import com.doozi.scorena.PoolTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;
import com.doozi.scorena.QuestionContent

import grails.transaction.Transactional

import java.text.DecimalFormat


@Transactional
class QuestionService {
	def betService
	def gameService
	def processEngineImplService
	def questionService
	
	public static final int FEATURE_QUESTION_SIZE = 3
	
	def listFeatureQuestions(userId){
		def questionList = listUpcomingQuesitonsByMostPeopleBetOn()
		def featureQuestions = getUnpickMostBetQuestions(questionList, userId, FEATURE_QUESTION_SIZE)
		return constructFeatureGameData(featureQuestions)		
	}
	
	def listFeatureQuestions(){
		def questionList = listUpcomingQuesitonsByMostPeopleBetOn()		
		def featureQuestions = getMostBetQuestions(questionList, FEATURE_QUESTION_SIZE)
		return constructFeatureGameData(featureQuestions)
	}
	
	
	def getQuestionWithPoolInfo(String eventKey, int qId){
		List questions = listQuestionsWithPoolInfo(eventKey)
		for (Map q: questions){
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
				
			
				def pick1PayoutMultiple =  (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult
				def pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
				def pick1PayoutPercentage = Math.round(100 * (pick1PayoutMultiple-1))
				def pick2PayoutPercentage =  Math.round(100 * (pick2PayoutMultiple-1))
				DecimalFormat df = new DecimalFormat("###.##")
				
				
				def game = gameService.getGame(q.eventKey)
				def winnerPick =-1
				if (game.gameStatus.trim() == "post-event"){
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
		
		if (q==null){
			return [message: "invalid question ID"]
		}
		
		def game = gameService.getGame(q.eventKey)
		if (game.gameStatus.trim() == "post-event"){			
			return getPostEventQuestion(q, userId)
		}else{			
			return getPreEventQuestion(q, userId)
		}
		
		
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
	
	def createQuestions(){
		println "QuestionService::createQuesitons(): starts at "+new Date()
		
		List upcomingGames = gameService.listUpcomingGames()
		
		for (int i=0; i < upcomingGames.size(); i++){
			def game = upcomingGames.get(i)
			println "game id: "+game.gameId
			
			if (Question.findByEventKey(game.gameId) == null){
				populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
			}
		}
	}
	
	def populateQuestions(String away, String home, String eventId){
	
		def questionContent2 = QuestionContent.findAllByQuestionType("truefalse-0")
		for (QuestionContent qc: questionContent2){
			def q = new Question(eventKey: eventId, pick1: "Yes", pick2: "No", pool: new Pool(minBet: 5))
			qc.addToQuestion(q)
			if (qc.save()){
				System.out.println("game successfully saved")
			}else{
				System.out.println("game save failed")
				qc.errors.each{
					println it
				}
			}
		}
		
		def questionContent1 = QuestionContent.findAllByQuestionType("team-0")
		for (QuestionContent qc: questionContent1){
			def q = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
			qc.addToQuestion(q)
			if (qc.save()){
				System.out.println("game successfully saved")
			}else{
				System.out.println("game save failed")
				qc.errors.each{
					println it
				}
			}
		}
	}

	
	private def getPostEventQuestion(Question q, String userId){
		PoolTransaction lastBet = betService.getLatestBetByQuestionId(q.id)
		int winnerPick = processEngineImplService.getWinningPick(q.eventKey, q)
		
		def pick1PayoutPercentage
		def pick2PayoutPercentage
		def pick1WinningPayoutMultiple
		def pick2WinningPayoutMultiple
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
			
		pick1PayoutMultiple =  (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult
		pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
		
		
		pick1PayoutPercentage = Math.round(100 * (pick1PayoutMultiple-1))
		pick2PayoutPercentage =  Math.round(100 * (pick2PayoutMultiple-1))
		
		switch (winnerPick){
			case 1:
				pick1WinningPayoutMultiple =  pick1PayoutMultiple
				pick2WinningPayoutMultiple = -1
				pick1WinningPayoutPercentage = pick1PayoutPercentage							
				pick2WinningPayoutPercentage = 0
				
				break
			case 2: 
				pick1WinningPayoutMultiple =  -1
				pick2WinningPayoutMultiple = pick2PayoutMultiple
				pick1WinningPayoutPercentage = 0
				pick2WinningPayoutPercentage = pick2PayoutPercentage
				
				break
			case 0:
				pick1WinningPayoutMultiple =  1
				pick2WinningPayoutMultiple = 1
				pick1WinningPayoutPercentage = 0
				pick2WinningPayoutPercentage = 0
				
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
					userWinningAmount = Math.floor(userBet.transactionAmount * pick1WinningPayoutMultiple)
					userPayoutPercent = pick1WinningPayoutPercentage
				}else{
					userWinningAmount = Math.floor(userBet.transactionAmount * pick2WinningPayoutMultiple)
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
			currentOddsPick1=(lastBet.pick1Amount/denominatorPick2Mult)
		}else if (lastBet.pick1Amount < lastBet.pick2Amount){
			currentOddsPick2 = lastBet.pick2Amount/denominatorPick1Mult
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
			betters: getBetters(q.id, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple, userInfo, username)
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
			
			
		def pick1PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult
		def pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
		def pick1PayoutPercentage = Math.round(100 * (pick1PayoutMultiple-1))
		def pick2PayoutPercentage =  Math.round(100 * (pick2PayoutMultiple-1))
		
		
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

	private def constructFeatureGameData(List featureQuestions){
		List featureQuestionResponse = []
		
		for (Map questionMap: featureQuestions){
			String gameId = questionMap.get("gameId")
			int questionId = questionMap.get("questionId")
			def game = gameService.getGame(gameId)
			game.question = questionService.getQuestionWithPoolInfo(gameId, questionId)
			featureQuestionResponse.add(game)
		}
		return featureQuestionResponse
	}
	
	
	/**
	 * returns list of [gameId:"",questionId:""] that are prevent
	 */
	private def listUpcomingQuesitonsByMostPeopleBetOn(){
		int eventKeyIndex = 0
		int quesitonIdIndex = 1
		int transactionTypeSumIndex = 2
		
		List preeventQuestions=[]
		
		//The questionList data structure is list of [(eventKey),(quesitonId), (transactionTypeSum), (BetCount)]
		def questionList = PoolTransaction.executeQuery("select eventKey, question.id ,sum(transactionType) as type1, count(*) as betCount "+
			"from PoolTransaction as t1 group by 1,2 order by betCount desc limit 100")
							
		for (List question: questionList){
			
			//get the questions that have no process transaction 
			if (question[transactionTypeSumIndex]==0){
				
				def game = gameService.getGame(question[eventKeyIndex])				
				if (game.gameStatus == SportsDataService.PREEVENT){
					preeventQuestions.add([gameId:question[eventKeyIndex], questionId: question[quesitonIdIndex]])
				}					
			}
		}
		return preeventQuestions
	}
	

	/**
	 * get a list of unpicked questions that have most player bet on with size=limit
	 * 
	 * @param questions: a list of [gameId:"",questionId:""]
	 * @return a list of [gameId:"",questionId:""] that have size=limit
	 */
	private def getUnpickMostBetQuestions(List questions, String userId, int limit){
		List unpickedQuestionList = []
		for (Map q: questions){
							
			def userBet = betService.getBetByQuestionIdAndUserId(q.get("questionId"), userId)
			if (userBet == null){
				unpickedQuestionList.add(q)
			}
			
			if (unpickedQuestionList.size()>limit){
				break
			}
		}
		return unpickedQuestionList
	}
	
	private def getMostBetQuestions(List questions, int limit){
		List unpickedQuestionList = []
		for (Map q: questions){		
			unpickedQuestionList.add(q) 
			if (unpickedQuestionList.size()>=limit){
				break
			}
		}
		return unpickedQuestionList
	}
	

}

