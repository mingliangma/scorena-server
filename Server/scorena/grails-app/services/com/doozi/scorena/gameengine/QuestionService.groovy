package com.doozi.scorena.gameengine

import com.doozi.scorena.Account
import com.doozi.scorena.Pool
import com.doozi.scorena.Question;
import com.doozi.scorena.QuestionContent
import com.doozi.scorena.controllerservice.SportsDataService;
import com.doozi.scorena.gamedata.*
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction;
import com.doozi.scorena.utils.*
import com.doozi.scorena.processengine.*
import com.doozi.scorena.transaction.LeagueTypeEnum;


import grails.plugins.rest.client.RestBuilder
import org.springframework.transaction.annotation.Transactional

import java.text.DecimalFormat
import java.util.List;
import java.util.Map;


class QuestionService {
	def betTransactionService
	def payoutTansactionService
	def gameService
	def parseService
	def processEngineImplService
	def questionService
	def userService
	def teamLogoService
	def questionUserInfoService
	def questionPoolUtilService
	def poolInfoService
	def commentService
	def friendSystemService
	def sportsDataService
	
	public static final int FEATURE_QUESTION_SIZE = 3
	public static final int BETTERS_LIST_SIZE = 100
	
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

	//TODO: fetch all transactions at the beginning of the method, then process them into different category
	def listQuestionsWithPoolInfo(eventKey, userId) {
		log.info "listQuestionsWithPoolInfo(): begins with eventKey = ${eventKey}, userId = ${userId}"
		
		DecimalFormat df = new DecimalFormat("###.##")
		List resultList = []		
		def questions = listQuestions(eventKey)
		def game = gameService.getGame(eventKey)
		List<PayoutTransaction> userPayouts = payoutTansactionService.listPayoutTransByGameIdAndUserId(eventKey, userId)
		List<BetTransaction> userBets = betTransactionService.listBetsByUserIdAndGameId(eventKey, userId)
		List<Map> userFriendsList = friendSystemService.listFollowings(userId)
		
		def rest = new RestBuilder()
		for (Question q: questions){
			
			QuestionContent questionContent = q.questionContent
			def userInfo=[:]
			def winnerPick =-1
			PayoutTransaction userPayoutInThisQuestion;
			BetTransaction userBetInThisQuestion;
			
			for (PayoutTransaction payout: userPayouts){
				if (payout.question.id==q.id){
					userPayoutInThisQuestion = payout
				}
			}
			
			for (BetTransaction bet: userBets){
				if (bet.question.id==q.id){
					userBetInThisQuestion = bet
				}
			}
			
			
			if (questionContent.questionType == "disable")
				continue
						
			
			PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(userFriendsList, q.id)
			def pick1PayoutMultiple = questionPoolUtilService.calculatePick1PayoutMultiple(questionPoolInfo)
			def pick2PayoutMultiple = questionPoolUtilService.calculatePick2PayoutMultiple(questionPoolInfo)		

			Boolean isGameProcessed = false
			if (game.gameStatus.trim() == "post-event"){
				GameProcessRecord processRecord = GameProcessRecord.findByEventKey(game.gameId)
				if (processRecord && processRecord.transProcessStatus == TransactionProcessStatusEnum.PROCESSED 
					&& processRecord.scoreProcessStatus == ScoreProcessStatusEnum.PROCESSED){
					winnerPick = processEngineImplService.calculateWinningPick(game, q)
					isGameProcessed = true
				}					
			}

			if (userId != null){
				userInfo = questionUserInfoService.getQuestionsUserInfo(userPayoutInThisQuestion, userBetInThisQuestion)
			}
			
			
			String followingPlayerPictureUrl = ""
			int followingPlayerBetAmount = -1
			String followingPlayerPick = ""
			boolean followingExistsInQuestion = false
			
			String playerPictureUrl = ""
			int playerBetAmount = -1
			String playerPick = ""
			
			
			if (questionPoolInfo.getFriendsExist()){ //friends are playing in this question, use friend's profile in question player icon
				followingPlayerPictureUrl = questionPoolInfo.getFriendPictureUrl()
				followingPlayerBetAmount = questionPoolInfo.getFriendBetAmount()
				followingPlayerPick = questionPoolInfo.getFriendBetPick() == 1 ? q.pick1 : q.pick2
				followingExistsInQuestion = true
			}

			if (questionPoolInfo.getHighestBetUserId() != null){ 	 //no friends are playing, use highest bet user	
				def resp = parseService.retrieveUser(rest, questionPoolInfo.getHighestBetUserId())
				if (resp.status == 200){
					playerPictureUrl = resp.json.pictureURL
				}
				playerBetAmount = questionPoolInfo.getHighestBetAmount()
				playerPick = questionPoolInfo.getHighestBetPick() == 1 ? q.pick1 : q.pick2
			}
			

			
			resultList.add([
				questionId: q.id,
				content: questionContent.content,
				pick1: q.pick1,
				pick2: q.pick2,
				pick1LogoUrl: teamLogoService.getTeamLogo(q.pick1.trim()),
				pick2LogoUrl: teamLogoService.getTeamLogo(q.pick2.trim()),
				userInfo:userInfo,
				winnerPick:winnerPick,
				playerBetAmount: playerBetAmount, //the question preview player bet amount
				playerPictureUrl: playerPictureUrl,	// the question preview player picture URL
				playerPick: playerPick, // the question preview player pick				
				followingPlayerBetAmount: followingPlayerBetAmount,
				followingPlayerPictureUrl: followingPlayerPictureUrl,
				followingPlayerPick: followingPlayerPick,				
				followingPlayerExistsInQuestion: followingExistsInQuestion,
				friendPlayerBetAmount: followingPlayerBetAmount, //will be deprecate
				friendPlayerPictureUrl: followingPlayerPictureUrl,//will be deprecate
				friendPlayerPick: followingPlayerPick,//will be deprecate
				friendExistsInQuestion: followingExistsInQuestion,//will be deprecate
				isGameProcessed: isGameProcessed,
				pool: [
					pick1Amount: questionPoolInfo.getPick1Amount(),
					pick1NumPeople: questionPoolInfo.getPick1NumPeople(),
					pick1odds:  df.format(pick1PayoutMultiple).toDouble(),
					pick2Amount:questionPoolInfo.getPick2Amount(),
					pick2NumPeople: questionPoolInfo.getPick2NumPeople(),
					pick2odds:  df.format(pick2PayoutMultiple).toDouble(),
				]
				
			])
		}
	
		log.info "listQuestionsWithPoolInfo(): ends with resultList = ${resultList}"
		
		return resultList
	}
	
	def listQuestions(eventKey){
		return Question.findAllByEventKey(eventKey)
	}
	
	Map getQuestion(qId){
		getQuestion(qId, null)
	}
	
	Map getQuestion(qId, userId){
		log.info "getQuestion(): begins with qId = ${qId}, userId = ${userId}"
		
		Question q = Question.findById(qId)
		
		if (q==null){
			def eMessage = "invalid question ID"
			log.error "${eMessage}"
			return [message: eMessage]
		}
		
		Map game = gameService.getGame(q.eventKey)
		Map questionDetails = [:]
		
		if (game.gameStatus.trim() == "post-event"){
			questionDetails = getPostEventQuestion(q, userId, game)
		}else{
			questionDetails = getPreEventQuestion(q, userId)
		}
		
		log.info "getQuestions(): ends with questionDetails = ${questionDetails}"
		
		return questionDetails
	}
	
	@Transactional
	def createQuestions(){
		log.info "createQuestions(): begins..."
		
		int questionsCreated = 0
//		List upcomingGames = []
		List upcomingGames = gameService.listUpcomingNonCustomGames()
//		List upcomingGamesNBA = gameService.listUpcomingGamesData("all", "nba")
//		upcomingGames.addAll(upcomingGamesSoccer)
//		upcomingGames.addAll(upcomingGamesNBA)
		
		
		for (int i=0; i < upcomingGames.size(); i++){

			def game = upcomingGames.get(i)			
			
			if (Question.findByEventKey(game.gameId) == null){
				println "createQuestions(): game id is "+game.gameId
				questionsCreated = questionsCreated + populateQuestions(game.away.teamname, game.home.teamname, game.gameId)
			}
		}
		println "questionsCreated: " + questionsCreated
		log.info "createQuestions(): questionsCreated: " + questionsCreated
		
		return questionsCreated
	}
	
	@Transactional
	int populateQuestions(String away, String home, String eventId){
		log.info "populateQuestions(): begins with away = ${away}, home = ${home}, eventId = ${eventId}"
		
		int questionCreated = 0
		
		def questionContent1 = QuestionContent.findAllByQuestionType(QuestionContent.WHOWIN)
		for (QuestionContent qc: questionContent1){
			def q = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
			qc.addToQuestion(q)
			if (qc.save(failOnError:true)){
				questionCreated++
				System.out.println("game successfully saved")
				log.info "populateQuestions(): game successfully saved"
			}else{
				System.out.println("game save failed")
				log.error "populateQuestions(): game save failed"
				qc.errors.each{
					println it
				}
			}
		}
		
		if (sportsDataService.getLeagueCodeFromEventKey(eventId) == LeagueTypeEnum.EPL){
			def questionContent2 = QuestionContent.findAllByQuestionType(QuestionContent.SCOREGREATERTHAN_SOCCER)
			for (QuestionContent qc: questionContent2){
				def q = new Question(eventKey: eventId, pick1: "3 or above", pick2: "2 or below", pool: new Pool(minBet: 5))
				qc.addToQuestion(q)			
				if (qc.save(failOnError:true)){
					questionCreated++
					System.out.println("game successfully saved")
					log.info "populateQuestions(): game successfully saved"
				}else{
					System.out.println("game save failed")
					log.error "populateQuestions(): game save failed"
					qc.errors.each{
						println it
					}
				}
			}
			
			def QC_MoreShots = QuestionContent.findAllByQuestionType(QuestionContent.AUTOCUSTOM_SOCCER1)
			for (QuestionContent qc: QC_MoreShots){
				def q = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
				qc.addToQuestion(q)
				if (qc.save(failOnError:true)){
					questionCreated++
					System.out.println("game successfully saved")
					log.info "populateQuestions(): game successfully saved"
				}else{
					System.out.println("game save failed")
					log.error "populateQuestions(): game save failed"
					qc.errors.each{
						println it
					}
				}
			}
			
		}else if (sportsDataService.getLeagueCodeFromEventKey(eventId) == LeagueTypeEnum.NBA){
			def questionContent2 = QuestionContent.findAllByQuestionType(QuestionContent.SCOREGREATERTHAN_BASKETBALL)
			for (QuestionContent qc: questionContent2){
				def q = new Question(eventKey: eventId, pick1: "200 or above", pick2: "199 or below", pool: new Pool(minBet: 5))
				qc.addToQuestion(q)
				if (qc.save(failOnError:true)){
					questionCreated++
					System.out.println("game successfully saved")
					log.info "populateQuestions(): game successfully saved"
				}else{
					System.out.println("game save failed")
					log.error "populateQuestions(): game save failed"
					qc.errors.each{
						println it
					}
				}
			}
			
//			def QC_HigherFG = QuestionContent.findAllByQuestionType(QuestionContent.HIGHERFIELDGOAL_BASKETBALL)
//			for (QuestionContent qc: QC_HigherFG){
//				def q = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
//				qc.addToQuestion(q)
//				if (qc.save(failOnError:true)){
//					questionCreated++
//					System.out.println("game successfully saved")
//					log.info "populateQuestions(): game successfully saved"
//				}else{
//					System.out.println("game save failed")
//					log.error "populateQuestions(): game save failed"
//					qc.errors.each{
//						println it
//					}
//				}
//			}
			
			def QC_HigherRebounce = QuestionContent.findAllByQuestionType(QuestionContent.HIGHERREBOUNDS_BASKETBALL)
			for (QuestionContent qc: QC_HigherRebounce){
				def q = new Question(eventKey: eventId, pick1: home, pick2: away, pool: new Pool(minBet: 5))
				qc.addToQuestion(q)
				if (qc.save(failOnError:true)){
					questionCreated++
					System.out.println("game successfully saved")
					log.info "populateQuestions(): game successfully saved"
				}else{
					System.out.println("game save failed")
					log.error "populateQuestions(): game save failed"
					qc.errors.each{
						println it
					}
				}
			}
		
		}
		

		

		
		log.info "populateQuestions(): ends with questionCreated = ${questionCreated}"
		
		return questionCreated
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
				pick1odds: The odds that shows in the question preview of each question in the question list page
				pick2odds: The odds that shows in the question preview of each question in the question list page
			],
			betters: [
				name: the user's display name,
				wager: the user's initial wager,
				expectedWinning: The user's winning amount
			]
	 */
	
	private def getPostEventQuestion(Question q, String userId, Map game){
		log.info "getPostEventQuestion(): begins with q = ${q}, userId = ${userId}, game = ${game}"

		List<BetTransaction> betTransList = betTransactionService.listAllBetsByQId(q.id)
		int winnerPick = -1
		Boolean isGameProcessed = false
		if (game.gameStatus.trim() == "post-event"){
			GameProcessRecord processRecord = GameProcessRecord.findByEventKey(game.gameId)
			if (processRecord && processRecord.transProcessStatus == TransactionProcessStatusEnum.PROCESSED
				&& processRecord.scoreProcessStatus == ScoreProcessStatusEnum.PROCESSED){
				winnerPick = processEngineImplService.calculateWinningPick(game, q)
				isGameProcessed = true
			}
		}
		
		def pick1PayoutMultiple=0
		def pick2PayoutMultiple=0
		def pick1ProfitMultiple=0
		def pick2ProfitMultiple=0
//		int pick1PayoutPercentage=0 
//		int pick2PayoutPercentage=0 
		 
		DecimalFormat df = new DecimalFormat("###.##")

		PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(betTransList)
		
		def pick1PayoutMultipleWhenWin = questionPoolUtilService.calculatePick1PayoutMultiple(questionPoolInfo)
		def pick2PayoutMultipleWhenWin = questionPoolUtilService.calculatePick2PayoutMultiple(questionPoolInfo)				
//		def pick1PayoutPercentageWhenWin = Math.round(100 * (pick1PayoutMultiple-1))
//		def pick2PayoutPercentageWhenWin =  Math.round(100 * (pick2PayoutMultiple-1))
		
		//when isGameProcessed=false and winnerPick=-1, the payout and profit multiple are excepted multiple on winning
		switch (winnerPick){
			case 1:
				pick1PayoutMultiple =  pick1PayoutMultipleWhenWin
				pick2PayoutMultiple = 0
				pick1ProfitMultiple = pick1PayoutMultipleWhenWin - 1
				pick2ProfitMultiple = -1
//				pick1PayoutPercentage = pick1PayoutPercentageWhenWin							
//				pick2PayoutPercentage = 0
				
				break
			case 2: 
				pick1PayoutMultiple =  0
				pick2PayoutMultiple = pick2PayoutMultipleWhenWin
				pick1ProfitMultiple = -1
				pick2ProfitMultiple = pick2PayoutMultipleWhenWin - 1
//				pick1PayoutPercentage = 0
//				pick2PayoutPercentage = pick2PayoutPercentageWhenWin
				
				break
			case 0:
				pick1PayoutMultiple =  1
				pick2PayoutMultiple = 1
				pick1ProfitMultiple = 0
				pick2ProfitMultiple = 0
//				pick1PayoutPercentage = 0
//				pick2PayoutPercentage = 0
				
				break
			case -1:
				pick1PayoutMultiple =  pick1PayoutMultipleWhenWin
				pick2PayoutMultiple = pick2PayoutMultipleWhenWin
				pick1ProfitMultiple = pick1PayoutMultipleWhenWin - 1
				pick2ProfitMultiple = pick2PayoutMultipleWhenWin - 1
				break
		}
		
		
		println "pick1PayoutMultiple="+pick1PayoutMultiple
		println "pick2PayoutMultiple="+pick2PayoutMultiple
		println "pick1ProfitMultiple="+pick1ProfitMultiple
		println "pick2ProfitMultiple="+pick2ProfitMultiple
		
		Map userInfo = [:]
		Map betters = [:]
		
		if (userService.accountExists(userId)){
			userInfo = questionUserInfoService.getPostEventQuestionUserInfo(userId, q.id, pick1PayoutMultiple, pick2PayoutMultiple,
		pick1ProfitMultiple,pick2ProfitMultiple)
			
			
			List<String> userFriendsList = friendSystemService.listFollowingUserId(userId)
			List<BetTransaction> friendBets = betTransactionService.listAllFriendsBetsByQId(q.id, userFriendsList)			
			betters = getBetters(betTransList, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, userId, friendBets)
		}else{
			betters = getBetters(betTransList, pick1PayoutMultiple, pick2PayoutMultiple, userInfo)
		}
		
//		def currentOddsPick1=1
//		def currentOddsPick2=1
//		if (lastBet.pick1Amount > lastBet.pick2Amount){
//			currentOddsPick1=(lastBet.pick1Amount/denominatorPick2Mult)
//		}else if (lastBet.pick1Amount < lastBet.pick2Amount){
//			currentOddsPick2 = lastBet.pick2Amount/denominatorPick1Mult
//		}
		
		def result = [
			questionId: q.id,
			content: q.questionContent.content,
			pick1: q.pick1,
			pick2: q.pick2,
			pick1LogoUrl: teamLogoService.getTeamLogo(q.pick1.trim()),
			pick2LogoUrl: teamLogoService.getTeamLogo(q.pick2.trim()),
			winnerPick: winnerPick,
//			currentOddsPick1:df.format(currentOddsPick1).toDouble(),
//			currentOddsPick2:df.format(currentOddsPick2).toDouble(),
			userInfo: userInfo,
			pool: [
				pick1Amount: questionPoolInfo.getPick1Amount(),
				pick1NumPeople: questionPoolInfo.getPick1NumPeople(),
//				pick1PayoutPercent: pick1PayoutPercentage,
				pick2Amount:questionPoolInfo.getPick2Amount(),
				pick2NumPeople: questionPoolInfo.getPick2NumPeople(),
//				pick2PayoutPercent: pick2PayoutPercentage,
				pick1odds:  df.format(pick1PayoutMultipleWhenWin).toDouble(),
				pick2odds:  df.format(pick2PayoutMultipleWhenWin).toDouble(),
			],
			betters: betters,
			winnerPick: winnerPick,
			isGameProcessed: isGameProcessed,
			comments: commentService.getExistingComments(q.id)
		]
		
		log.info "getPostEventQuestion(): ends with result = ${result}"
		
		return result
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
				pick1odds: The odds that shows in the question preview of each question in the question list page
				pick2odds: The odds that shows in the question preview of each question in the question list page
			],
			betters: [
				name: the user's display name,
				wager: the user's initial wager,
				expectedWinning: The expected winning amount
			]
	 */
	
	private def getPreEventQuestion(Question q, def userId){
		log.info "getPreEventQuestion(): begins with q = ${q}, userId = ${userId}"
		
		DecimalFormat df = new DecimalFormat("###.##")
		Map betters = [:]
		Map userInfo = [:]
		List<BetTransaction> betTransList = betTransactionService.listAllBetsByQId(q.id)
		PoolInfo questionPoolInfo = poolInfoService.getQuestionPoolInfo(betTransList)
	
		
		def pick1PayoutMultiple = questionPoolUtilService.calculatePick1PayoutMultiple(questionPoolInfo)
		def pick2PayoutMultiple = questionPoolUtilService.calculatePick2PayoutMultiple(questionPoolInfo)	
		def pick1PayoutPercentage = Math.round(100 * (pick1PayoutMultiple-1))
		def pick2PayoutPercentage =  Math.round(100 * (pick2PayoutMultiple-1))
		
		if (userService.accountExists(userId)){
			userInfo = questionUserInfoService.getPreEventQuestionUserInfo(userId, q.id)
			List<String> userFriendsList = friendSystemService.listFollowingUserId(userId)
			List<BetTransaction> friendBets = betTransactionService.listAllFriendsBetsByQId(q.id, userFriendsList)			
			betters = getBetters(betTransList, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, userId, friendBets)
		}else{
			betters = getBetters(betTransList, pick1PayoutMultiple, pick2PayoutMultiple, userInfo)
		}

		
//		def currentOddsPick1=1
//		def currentOddsPick2=1
//		if (lastBet.pick1Amount > lastBet.pick2Amount){
//			currentOddsPick1=(lastBet.pick1Amount/denominatorPick2Mult)
//		}else if (lastBet.pick1Amount < lastBet.pick2Amount){
//			currentOddsPick2 = lastBet.pick2Amount/denominatorPick1Mult
//		}
		
		def result = [
			questionId: q.id,
			content: q.questionContent.content,
			pick1: q.pick1,
			pick1LogoUrl: teamLogoService.getTeamLogo(q.pick1.trim()),
			pick2: q.pick2,
			pick2LogoUrl: teamLogoService.getTeamLogo(q.pick2.trim()),
			lastUpdate: "",
			userInfo:userInfo,
			pool: [
				pick1Amount: questionPoolInfo.getPick1Amount(),
				pick1NumPeople: questionPoolInfo.getPick1NumPeople(),
				pick2Amount:questionPoolInfo.getPick2Amount(),
				pick2NumPeople: questionPoolInfo.getPick2NumPeople(),
				pick1odds:  df.format(pick1PayoutMultiple).toDouble(),
				pick2odds:  df.format(pick2PayoutMultiple).toDouble(),
//				pick1PayoutPercent: pick1PayoutPercentage,
//				pick2PayoutPercent: pick2PayoutPercentage,
//				currentOddsPick1:df.format(currentOddsPick1).toDouble(),
//				currentOddsPick2:df.format(currentOddsPick2).toDouble(),
			],
			betters: betters,
			
			comments: commentService.getExistingComments(q.id)
		]
		
		log.info "getPreEventQuestion(): ends with result = ${result}"
		
		return result
	}

	private List constructFeatureGameData(List featureQuestions){
		log.info "constructFeatureGameData(): begins with featureQuestions = ${featureQuestions}"
		
		List featureQuestionResponse = []
		
		for (Map questionMap: featureQuestions){
			String gameId = questionMap.get("gameId")
			int questionId = questionMap.get("questionId")
			def game = gameService.getGame(gameId)
			game.question = questionService.getQuestionWithPoolInfo(gameId, questionId)
			featureQuestionResponse.add(game)
		}
		
		log.info "constructFeatureGameData(): ends with featureQuestionResponse = ${featureQuestionResponse}"
		
		return featureQuestionResponse
	}
	
	
	/**
	 * returns list of [gameId:"",questionId:""] that are prevent
	 */
	private def listUpcomingQuesitonsByMostPeopleBetOn(){
		log.info "listUpcomingQuesitonsByMostPeopleBetOn(): begins..."
		
		int eventKeyIndex = 0
		int quesitonIdIndex = 1
		int BetCount = 2
		
		List preeventQuestions=[]
		
		//The questionList data structure is list of [(eventKey),(quesitonId), (BetCount)]
		def questionList = BetTransaction.executeQuery("select eventKey, question.id, count(*) as betCount "+
			"from BetTransaction as t1 group by 1,2 order by betCount desc limit 100")
									
		for (List question: questionList){
			Question q = Question.findById(question[quesitonIdIndex])
			QuestionContent questionContent = q.questionContent
			
			if (questionContent.questionType == "disable")
				continue
				
				
			def game = gameService.getGame(question[eventKeyIndex])				
			if (game.gameStatus == SportsDataService.PREEVENT_NAME){
				preeventQuestions.add([gameId:question[eventKeyIndex], questionId: question[quesitonIdIndex]])
			}					
			
		}
		
		log.info "listUpcomingQuesitonsByMostPeopleBetOn(): ends with preeventQuestions = ${preeventQuestions}"
		
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
			if (unpickedQuestionList.size()>limit){
				break
			}
			def userBet = betTransactionService.getBetByQuestionIdAndUserId(q.get("questionId"), userId)
			if (userBet == null){
				unpickedQuestionList.add(q)
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
	
	private Map getBetters(List<BetTransaction> betTransactions, def pick1PayoutMultiple, def pick2PayoutMultiple, Map userInfo){
		return getBetters(betTransactions, pick1PayoutMultiple, pick2PayoutMultiple, userInfo, null, [])
		
	}
	
	
	private Map getBetters(List<BetTransaction> betTransactions, def pick1PayoutMultiple, def pick2PayoutMultiple, Map userInfo, String userId, List<BetTransaction> followingUserBetList){
		log.info "getBetters(): begins with betTransactions = ${betTransactions}, pick1PayoutMultiple = ${pick1PayoutMultiple}, pick2PayoutMultiple = ${pick2PayoutMultiple}, userInfo = ${userInfo}, followingUserBetList = ${followingUserBetList}"
		
		Map pick1BettersMap = [:]
		Map pick2BettersMap = [:]
		def currentUserAdded = false
		Boolean isFriend = false	//indicator weather better is current user's friends or not 
		Boolean isFriendMySelf = false	//indicator weather current user is current user's friends or not 
		Boolean isBettersMapFull = false
		String username = ""
		
		
		if (userId != null){
			username = userService.getUserDisplayName(userId)
			if (userInfo!=[:]){
				if (userInfo.userPick==1){
					pick1BettersMap.put(userId,[name:username, userId:userId, wager:userInfo.userWager,expectedPayout: Math.round(pick1PayoutMultiple * userInfo.userWager), isFriend: isFriendMySelf])
				}else if (userInfo.userPick==2){
					pick2BettersMap.put(userId,[name:username, userId:userId, wager:userInfo.userWager,expectedPayout: Math.round(pick2PayoutMultiple * userInfo.userWager), isFriend: isFriendMySelf])
				}
			}
		}
		
		//add 	
		for (BetTransaction friendTransaction: followingUserBetList){
			Account betterAccount = friendTransaction.account			
			String betterUsername = betterAccount.username
			String betterUserId =  betterAccount.userId
			
			if (friendTransaction.pick==Pick.PICK1){
				
				if (pick1BettersMap.size() <=BETTERS_LIST_SIZE){
					if (betterUserId != userId){
						pick1BettersMap.put(betterUserId,[
							name:betterUsername,
							userId:betterUserId,
							wager:friendTransaction.transactionAmount,
							expectedPayout: Math.round(pick1PayoutMultiple * friendTransaction.transactionAmount),
							isFriend: true,
							isFollowing: true])
					}
				}
			}else{
				if (pick2BettersMap.size() <=BETTERS_LIST_SIZE){
					if (betterUserId != userId){
						pick2BettersMap.put(betterUserId, [
							name:betterUsername,
							userId:betterUserId,
							wager:friendTransaction.transactionAmount,
							expectedPayout: Math.round(pick2PayoutMultiple * friendTransaction.transactionAmount),
							isFriend: true,
							isFollowing: true])
					}
				}
			}
			if (pick2BettersMap.size() >BETTERS_LIST_SIZE && pick1BettersMap.size() >BETTERS_LIST_SIZE){
				isBettersMapFull = true
				break
			}
		}
		if (!isBettersMapFull){
			for (BetTransaction betTrans: betTransactions){
	
				Account betterAccount = betTrans.account		
				String betterUsername = betterAccount.username
				String betterUserId =  betterAccount.userId
				
				if (pick1BettersMap.containsKey(betterUserId) || pick2BettersMap.containsKey(betterUserId)){
					continue
				}
				
				if (betTrans.pick==Pick.PICK1){
					
					if (pick1BettersMap.size() <=BETTERS_LIST_SIZE){
						if (betterUserId != userId){
							pick1BettersMap.put(betterUserId,[
								name:betterUsername,
								userId:betterUserId,
								wager:betTrans.transactionAmount,
								expectedPayout: Math.round(pick1PayoutMultiple * betTrans.transactionAmount),
								isFriend: false,
								isFollowing: false])
						}
					}
				}else{
					if (pick2BettersMap.size() <=BETTERS_LIST_SIZE){
						if (betterUserId != userId){
							pick2BettersMap.put(betterUserId, [
								name:betterUsername,
								userId:betterUserId,
								wager:betTrans.transactionAmount,
								expectedPayout: Math.round(pick2PayoutMultiple * betTrans.transactionAmount),
								isFriend: false,
								isFollowing: false])				
						}
					}
				}
				if (pick2BettersMap.size() >BETTERS_LIST_SIZE && pick1BettersMap.size() >BETTERS_LIST_SIZE)
					break
			}
		}		
		def homeBettersArr = getBettersProfile(pick1BettersMap)
		def awayBettersArr = getBettersProfile(pick2BettersMap)
		
		homeBettersArr = getBettersOrderedByIsFriend(homeBettersArr)	//get betters presented in order by it is current user's friend or not
		awayBettersArr = getBettersOrderedByIsFriend(awayBettersArr)	//get betters presented in order by it is current user's friend or not
		
		def betters=[
			pick1Betters: homeBettersArr,
			pick2Betters: awayBettersArr
		]
		
		log.info "getBetters(): ends with betters = ${betters}"
		
		return betters
	}
	
	private def getBettersOrderedByIsFriend(def bettersArr) {
		log.info "getBettersOrderedByIsFriend(): begins with bettersArr = ${bettersArr}"
		
		List isFriendUserProfileGroup = []
		List noneFriendUserProfileGroup = []
		for(Map userProfileMap: bettersArr) {
			if(userProfileMap.isFriend == true)
				isFriendUserProfileGroup.add(userProfileMap)
			else
				noneFriendUserProfileGroup.add(userProfileMap)
		}
		List bettersList = []
		bettersList.addAll(isFriendUserProfileGroup)
		bettersList.addAll(noneFriendUserProfileGroup)
		
		println "bettersList: "+bettersList
		log.info "getBettersOrderedByIsFriend(): ends with bettersList = ${bettersList}"
		
		return bettersList
	}
	
	private List getBettersProfile(Map bettersMap){
		log.info "getBettersProfile(): begins with bettersMap = ${bettersMap}"
		
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
			log.error "getBettersProfile(): in retrieving user " + userProfileResults.error
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
		
		log.info "getBettersProfile(): ends with bettersProfileList = ${bettersProfileList}"
		
		return bettersProfileList
	}
	
	
}

