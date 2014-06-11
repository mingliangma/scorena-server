package com.doozi.scorena.controllerservice

import com.doozi.scorena.Account
import com.doozi.scorena.Pool
import com.doozi.scorena.PoolTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;
import com.doozi.scorena.QuestionContent
import com.doozi.scorena.gamedata.*

import grails.plugins.rest.client.RestBuilder
import grails.transaction.Transactional

import java.text.DecimalFormat
import java.util.Map;


@Transactional
class QuestionService {
	def betService
	def gameService
	def parseService
	def processEngineImplService
	def questionService
	def userService
	def teamLogoService
	
	public static final int FEATURE_QUESTION_SIZE = 3
	
	List listFeatureQuestions(userId){
		def questionList = listUpcomingQuesitonsByMostPeopleBetOn()
		def featureQuestions = getUnpickMostBetQuestions(questionList, userId, FEATURE_QUESTION_SIZE)
		return constructFeatureGameData(featureQuestions)		
	}
	
	List listFeatureQuestions(){
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
		
		DecimalFormat df = new DecimalFormat("###.##")
		List resultList = []		
		def questions = listQuestions(eventKey)
		boolean isValidUserId = false
		
		if (userId != null){
			isValidUserId = true
		}
		
		for (Question q: questions){
			def userInfo=[]
			def winnerPick =-1			
			def lastBet = betService.getLatestBetByQuestionId(q.id.toString())
			def game = gameService.getGame(q.eventKey)
			
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

			if (game.gameStatus.trim() == "post-event"){
				winnerPick = processEngineImplService.getWinningPick(q.eventKey, q)
			}

			if (isValidUserId){
				userInfo = getQuestionsUserInfo(userId, q.id, winnerPick)
			}
			resultList.add([
				questionId: q.id,
				content: q.questionContent.content,
				pick1: q.pick1,
				pick2: q.pick2,
				pick1LogoUrl: teamLogoService.getTeamLogo(q.pick1.trim()),
				pick2LogoUrl: teamLogoService.getTeamLogo(q.pick2.trim()),
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
	
	Map getQuestion(qId){
		getQuestion(qId, null)
	}
	
	Map getQuestion(qId, userId){
		Question q = Question.findById(qId)
		
		if (q==null){
			return [message: "invalid question ID"]
		}
		
		def game = gameService.getGame(q.eventKey)
		Map questionDetails = [:]
		
		if (game.gameStatus.trim() == "post-event"){
			questionDetails = getPostEventQuestion(q, userId)
		}else{
			questionDetails = getPreEventQuestion(q, userId)
		}
		return questionDetails
	}
	
	def createQuestions(){
		int questionsCreated = 0
		List upcomingGames = gameService.listUpcomingNonCustomGames()
		
		for (int i=0; i < upcomingGames.size(); i++){
			def game = upcomingGames.get(i)			
			
			if (Question.findByEventKey(game.gameId) == null){
				println "createQuestions(): game id is "+game.gameId
				questionsCreated = questionsCreated + populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
			}
		}
		println "questionsCreated: " + questionsCreated
		return questionsCreated
	}
	
	int populateQuestions(String away, String home, String eventId){
		int questionCreated = 0
		def questionContent2 = QuestionContent.findAllByQuestionType("truefalse-0")
		for (QuestionContent qc: questionContent2){
			def q = new Question(eventKey: eventId, pick1: "Yes", pick2: "No", pool: new Pool(minBet: 5))
			qc.addToQuestion(q)			
			if (qc.save(failOnError:true)){
				questionCreated++
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
			if (qc.save(failOnError:true)){
				questionCreated++
				System.out.println("game successfully saved")
			}else{
				System.out.println("game save failed")
				qc.errors.each{
					println it
				}
			}
		}
		return questionCreated
	}
	
	private Map getQuestionsUserInfo(String userId, long questionId, int winnerPick){
		
		boolean placedBet = false
		int userPickStatus = -1
		def userPick =-1
		def userBet
		
		userBet = betService.getBetByQuestionIdAndUserId(questionId, userId)
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

		return [placedBet:placedBet, userPickStatus:userPickStatus, userPick:userPick]
	}
	
	private Map getPostEventQuestionUserInfo(String userId, long questionId, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple,
		pick1WinningPayoutPercentage,pick2WinningPayoutPercentage){
		
		def userWinningAmount = 0
		def userPayoutPercent = 0
		def userBetAmount = 0
		def userPick =-1
		
		PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(questionId, userId)
		
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
	
	/**
	 * @param q: Question Object that is being requested
	 * @param userId: the userId that user made the request
	 * @return: Result map that contains question data, pool data, and social data
	 *
			questionId: The question Id of the question that is returned
			content: The Question content. Eg. who will win?
			pick1: The name of pick1 option. It could be a teamname or yes/now
			pick2: The name of pick1 option.
			winnerPick: The winning result which indicate a tie or winner pick option. 0 is tie, 1 indicates pick1 wins, 2 indicates pick2 wins
			currentOddsPick1: The odds that shows in the question preview of each question in the question list page
			currentOddsPick2: The odds that shows in the question preview of each question in the question list page
			userInfo: [
				userWinningAmount: The amount of money that the user win,
				userPayoutPercent: The user's payout on investment in percentage. (Total payout / initial wager) * 100.
									if a user lost the bet, the userPayoutPercent is 0%
				userWager: User's initial wager,
				userPick: the pick option a user selected
			],
			pool: [
				pick1Amount: The total amount of money that users have put into pick1,
				pick1NumPeople: The number of users that bet on pick1,
				pick1PayoutPercent: the payout on investment in percentage assuming pick 1 wins,
				pick2Amount: The total amount of money that users have put into pick2,
				pick2NumPeople: The number of users that bet on pick1,
				pick2PayoutPercent: the payout on investment in percentage assuming pick 2 wins,
			],
			betters: [
				name: the user's display name,
				wager: the user's initial wager,
				expectedWinning: The user's winning amount
			]
	 */
	
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
		
		Map userInfo = [:]
		Map betters = [:]
		
		if (userService.accountExists(userId)){
			userInfo = getPostEventQuestionUserInfo(userId, q.id, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple,
		pick1WinningPayoutPercentage,pick2WinningPayoutPercentage)
			betters = getBetters(q.id, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple, userInfo, userId)
		}else{
			betters = getBetters(q.id, pick1WinningPayoutMultiple, pick2WinningPayoutMultiple, userInfo)
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
			pick1LogoUrl: teamLogoService.getTeamLogo(q.pick1.trim()),
			pick2LogoUrl: teamLogoService.getTeamLogo(q.pick2.trim()),
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
			betters: betters
		]
		return result
	}
	
	private Map getPreEventQuestionUserInfo(String userId, long questionId){
		
		def userBetAmount = 0
		def userPick =-1
		PoolTransaction userBet = betService.getBetByQuestionIdAndUserId(questionId, userId)
		if (userBet){
			userBetAmount=userBet.transactionAmount
			userPick=userBet.pick
		}
		return [userWager:userBetAmount, userPick:userPick]
		
	}		
	
/**
	 * @param q: Question Object that is being requested
	 * @param userId: the userId that user made the request
	 * @return: Result map that contains question data, pool data, and social data
	 * 
			questionId: The question Id of the question that is returned
			content: The Question content. Eg. who will win?
			pick1: The name of pick1 option. It could be a teamname or yes/now
			pick2: The name of pick1 option.
			lastUpdate: The date and time that the last user made the bet,
			userInfo: [
				userWager: User's initial wager,
				userPick: the pick option a user selected
			],
			pool: [
				pick1Amount: The total amount of money that users have put into pick1,
				pick1NumPeople: The number of users that bet on pick1,
				pick1PayoutPercent: the payout on investment in percentage assuming pick 1 wins,
				pick2Amount: The total amount of money that users have put into pick2,
				pick2NumPeople: The number of users that bet on pick1,
				pick2PayoutPercent: the payout on investment in percentage assuming pick 2 wins,
				currentOddsPick1: The odds that shows in the question preview of each question in the question list page
				currentOddsPick2: The odds that shows in the question preview of each question in the question list page
			],
			betters: [
				name: the user's display name,
				wager: the user's initial wager,
				expectedWinning: The expected winning amount
			]
	 */
	
	private def getPreEventQuestion(Question q, def userId){
		DecimalFormat df = new DecimalFormat("###.##")
		Map betters = [:]
		Map userInfo = [:]
		
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
		
		if (userService.accountExists(userId)){
			userInfo = getPreEventQuestionUserInfo(userId, q.id)
			betters = getBetters(q.id, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, userId)
		}else{
			betters = getBetters(q.id, pick1PayoutMultiple, pick2PayoutMultiple, userInfo)
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
			pick1LogoUrl: teamLogoService.getTeamLogo(q.pick1.trim()),
			pick2: q.pick2,
			pick2LogoUrl: teamLogoService.getTeamLogo(q.pick2.trim()),
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
			betters: betters
		]
		return result
	}

	private List constructFeatureGameData(List featureQuestions){
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
	
	private Map getBetters(qId, pick1PayoutMultiple, pick2PayoutMultiple, userInfo){
		return getBetters(qId, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, null)
	}
	
	private Map getBetters(qId, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, userId){
		Map pick1BettersMap = [:]
		Map pick2BettersMap = [:]
		def currentUserAdded = false
		
		String username = ""
		if (userId != null)
			username = userService.getUserDisplayName(userId)
		
		def betTransactions = betService.listAllBets(qId)
		
		for (PoolTransaction betTrans: betTransactions){

			Account betterAccount = betTrans.account
		
			String betterUsername = betterAccount.username
			String betterUserId =  betterAccount.userId
			if (betTrans.pick==1){
				
				if (pick1BettersMap.size() <=10){
					if ( betterUsername != username){
						pick1BettersMap.put(betterUserId,[
							name:betterUsername,
							userId:betterUserId,
							wager:betTrans.transactionAmount,
							expectedWinning: Math.round(pick1PayoutMultiple * betTrans.transactionAmount)])
					}
				}
			}else{
				if (pick2BettersMap.size() <=10){
					if (betterUsername != username){
						pick2BettersMap.put(betterUserId, [
							name:betterUsername,
							userId:betterUserId,
							wager:betTrans.transactionAmount,
							expectedWinning: Math.round(pick2PayoutMultiple * betTrans.transactionAmount)])				
					}
				}
			}
			if (pick2BettersMap.size() >10 && pick1BettersMap.size() >10)
				break
		}		
		
		if ( currentUserAdded == false && userInfo!=[:] && username!="" ){
			if (userInfo.userPick==1){
				pick1BettersMap.put(userId,[name:username, userId:userId, wager:userInfo.userWager,expectedWinning: Math.round(pick2PayoutMultiple * userInfo.userWager)])
			}else if (userInfo.userPick==2){
				pick2BettersMap.put(userId,[name:username, userId:userId, wager:userInfo.userWager,expectedWinning: Math.round(pick1PayoutMultiple * userInfo.userWager)])			
			}
		}
		
		def homeBettersArr = getBettersProfile(pick1BettersMap)
		def awayBettersArr = getBettersProfile(pick2BettersMap)
		
		def betters=[
			pick1Betters: homeBettersArr,
			pick2Betters: awayBettersArr
		]
		
		return betters
	}
	
	private List getBettersProfile(Map bettersMap){
		def rest = new RestBuilder()
		List userIdList = []
		Map test = [:]
		List bettersProfileList = []
		
		bettersMap.each{
			it -> userIdList.add(it.key)
		}
				
		Map userProfileResults = parseService.retrieveUserList(userIdList)

		if (userProfileResults.error){
			println "Error: QuestionService::getBettersProfile(): in retrieving user "+userProfileResults.error
			return []
		}

		List userProfileList = userProfileResults.results	
		
		for (Map userProfile: userProfileList){
			
			Map betterData = bettersMap.get(userProfile.objectId)
			betterData.pictureURL = ""
			
			if (userProfile.display_name != null && userProfile.display_name != "")
				betterData.name = userProfile.display_name
			
			if (userProfile.pictureURL != null && userProfile.pictureURL != "")
				betterData.pictureURL = userProfile.pictureURL
	
				
			bettersProfileList.add(betterData)
		}
		
		return bettersProfileList
	}	
}

