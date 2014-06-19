package com.doozi

import com.doozi.scorena.Account;
import com.doozi.scorena.Question;
import com.doozi.scorena.GameController;
import com.doozi.scorena.UserController;
import com.doozi.scorena.QuestionController;
import com.doozi.scorena.controllerservice.HelperService;

import grails.plugins.rest.client.RestBuilder
import spock.lang.*
import org.apache.commons.lang.*
import groovy.time.TimeCategory
import groovy.time.TimeDuration

/**
 *
 */
class QuestionControllerIntegrationSpec extends Specification {

	def parseService
	def gameService
	def sportsDataService
	def helperService
	def userService
	def questionService
	
    def setup() {
    }

    def cleanup() {
    }
	
	/**
	 * <h1>Integration test on listQuestions() for upcoming games without logging in.</h1>
	 * 
	 * <p> Note: This test assumes that getUpcomingGames from GameController does not fail.</p>
	 * 
	 * @param gameId The unique gameId from getUpcomingGames()
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 * @since June 6th, 2014
	 */
	void "listQuestions_Success_UpcomingGamesWithoutUserId"() {
		
		setup:
			def questionC = new QuestionController()
			questionC.questionService = questionService
			questionC.userService = userService
			
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			gameC.getUpcomingGames()
			def upcomingGames = gameC.response.json
			println "gameC.response.json (upcomingGames) is the following: " + upcomingGames
			gameC.response.reset()
			
		when:
			questionC.params.gameId = upcomingGames.gameId[0]
			questionC.listQuestions()
			def resp = questionC.response.json
			println "questionC.response.json is the following:" + questionC.response.json
			
		then:
			questionC.response.status == 200
			for (eachQuestion in resp) {
				assert eachQuestion.questionId.getClass() == Integer
				assert eachQuestion.questionId > 0
				assert eachQuestion.content.getClass() == String
				assert eachQuestion.content.length() > 0
				assert eachQuestion.content[-1] == "?"
				assert eachQuestion.pick1.getClass() == String
				assert eachQuestion.pick1.length() > 0
				assert eachQuestion.pick2.getClass() == String
				assert eachQuestion.pick2.length() > 0
				assert eachQuestion.userInfo == [placedBet:false]
				assert eachQuestion.winnerPick == -1
				assert eachQuestion.pool.pick1Amount.getClass() == Integer
				assert eachQuestion.pool.pick1Amount >= 0
				assert eachQuestion.pool.pick1NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick1NumPeople >= 0
				assert eachQuestion.pool.pick1PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick1PayoutPercent >= 0
				assert eachQuestion.pool.pick1odds.getClass() == Double
				assert eachQuestion.pool.pick1odds >= 1
				assert eachQuestion.pool.pick2Amount.getClass() == Integer
				assert eachQuestion.pool.pick2Amount >= 0
				assert eachQuestion.pool.pick2NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick2NumPeople >= 0
				assert eachQuestion.pool.pick2PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick2PayoutPercent >= 0
				assert eachQuestion.pool.pick2odds.getClass() == Double
				assert eachQuestion.pool.pick2odds >= 1
			}
			resp.questionId.unique(false) == resp.questionId 
			//unique will check the uniqueness of the input list, alter it, and return a modified list with only unique values
			//the false is to specify the unique method to not alter the original list. (because unique is passed by reference)
	}
	
	/**
	 * <h1>Integration test on listQuestions() for upcoming games with user logging in.</h1>
	 *
	 * <p> Note: This test assumes that getUpcomingGames from GameController does not fail.</p>
	 * <p> Note2: This test also assumes there is at least one user account in MySQL datebase </p>
	 *
	 * @param gameId The unique gameId from getUpcomingGames()
	 * @param userId A random userId selected directly from MySQL database
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 * @since June 6th, 2014
	 */
	void "listQuestions_Success_UpcomingGamesWithUserId"() {
		
		setup:
			def questionC = new QuestionController()
			questionC.questionService = questionService
			questionC.userService = userService
			
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			gameC.getUpcomingGames()
			def upcomingGames = gameC.response.json
			println "gameC.response.json (upcomingGames) is the following: " + upcomingGames
			gameC.response.reset()
			
		when:
			questionC.params.gameId = upcomingGames.gameId[0]
			questionC.params.userId = Account.executeQuery('select distinct a.userId from Account a order by rand()', [max:1])[0]
			println "Sample userId is: " + questionC.params.userId
			questionC.listQuestions()
			def resp = questionC.response.json
			println "questionC.response.json is the following:" + questionC.response.json
			
		then:
			questionC.response.status == 200
			for (eachQuestion in resp) {
				assert eachQuestion.questionId.getClass() == Integer
				assert eachQuestion.questionId > 0
				assert eachQuestion.content.getClass() == String
				assert eachQuestion.content.length() > 0
				assert eachQuestion.content[-1] == "?"
				assert eachQuestion.pick1.getClass() == String
				assert eachQuestion.pick1.length() > 0
				assert eachQuestion.pick2.getClass() == String
				assert eachQuestion.pick2.length() > 0
				assert eachQuestion.winnerPick == -1 //upcoming games have no result yet
				assert eachQuestion.userInfo.placedBet.getClass() == Boolean
				
				if (eachQuestion.userInfo.placedBet == false) {
					assert eachQuestion.userInfo.userPick == -1 //user did not place any bet
				} else { //eachQuestion.userInfo.placedBet == true
					assert (eachQuestion.userInfo.userPick == 1 || eachQuestion.userInfo.userPick == 2) //user has placed bet and can be either 1 or 2
				}
				
				assert eachQuestion.userInfo.userPickStatus == -1 //no game results yet
				assert eachQuestion.pool.pick1Amount.getClass() == Integer
				assert eachQuestion.pool.pick1Amount >= 0
				assert eachQuestion.pool.pick1NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick1NumPeople >= 0
				assert eachQuestion.pool.pick1PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick1PayoutPercent >= 0
				assert eachQuestion.pool.pick1odds.getClass() == Double
				assert eachQuestion.pool.pick1odds >= 1
				assert eachQuestion.pool.pick2Amount.getClass() == Integer
				assert eachQuestion.pool.pick2Amount >= 0
				assert eachQuestion.pool.pick2NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick2NumPeople >= 0
				assert eachQuestion.pool.pick2PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick2PayoutPercent >= 0
				assert eachQuestion.pool.pick2odds.getClass() == Double
				assert eachQuestion.pool.pick2odds >= 1
			}
			resp.questionId.unique(false) == resp.questionId
			//unique will check the uniqueness of the input list, alter it, and return a modified list with only unique values
			//the false is to specify the unique method to not alter the original list. (because unique is passed by reference)
	}
	
	/**
	 * <h1>Integration test on listQuestions() for past games without logging in.</h1>
	 *
	 * <p> Note: This test assumes that getPastGames from GameController does not fail.</p>
	 *
	 * @param gameId The unique gameId from getPastGames()
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 * @since June 6th, 2014
	 */
	void "listQuestions_Success_PastGamesWithoutUserId"() {
		
		setup:
			def questionC = new QuestionController()
			questionC.questionService = questionService
			questionC.userService = userService
			
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			gameC.getPastGames()
			def pastGames = gameC.response.json
			println "gameC.response.json (pastGames) is the following: " + pastGames
			gameC.response.reset()
			
		when:
			questionC.params.gameId = pastGames.gameId[0]
			questionC.listQuestions()
			def resp = questionC.response.json
			println "questionC.response.json is the following:" + questionC.response.json
			
		then:
			questionC.response.status == 200
			for (eachQuestion in resp) {
				assert eachQuestion.questionId.getClass() == Integer
				assert eachQuestion.questionId > 0
				assert eachQuestion.content.getClass() == String
				assert eachQuestion.content.length() > 0
				assert eachQuestion.content[-1] == "?"
				assert eachQuestion.pick1.getClass() == String
				assert eachQuestion.pick1.length() > 0
				assert eachQuestion.pick2.getClass() == String
				assert eachQuestion.pick2.length() > 0
				assert eachQuestion.userInfo == [placedBet:false]
				assert (eachQuestion.winnerPick == -1 || eachQuestion.winnerPick == 0 
						|| eachQuestion.winnerPick == 1 || eachQuestion.winnerPick == 2)
				assert eachQuestion.pool.pick1Amount.getClass() == Integer
				assert eachQuestion.pool.pick1Amount >= 0
				assert eachQuestion.pool.pick1NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick1NumPeople >= 0
				assert eachQuestion.pool.pick1PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick1PayoutPercent >= 0
				assert eachQuestion.pool.pick1odds.getClass() == Double
				assert eachQuestion.pool.pick1odds >= 1
				assert eachQuestion.pool.pick2Amount.getClass() == Integer
				assert eachQuestion.pool.pick2Amount >= 0
				assert eachQuestion.pool.pick2NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick2NumPeople >= 0
				assert eachQuestion.pool.pick2PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick2PayoutPercent >= 0
				assert eachQuestion.pool.pick2odds.getClass() == Double
				assert eachQuestion.pool.pick2odds >= 1
			}
			resp.questionId.unique(false) == resp.questionId
			//unique will check the uniqueness of the input list, alter it, and return a modified list with only unique values
			//the false is to specify the unique method to not alter the original list. (because unique is passed by reference)
	}
	
	/**
	 * <h1>Integration test on listQuestions() for past games without logging in.</h1>
	 *
	 * <p> Note: This test assumes that getPastGames from GameController does not fail.</p>
	 * <p> Note2: This test also assumes there is at least one user account in MySQL datebase </p>
	 *
	 * @param gameId The unique gameId from getPastGames()
	 * @param userId A random userId selected directly from MySQL database
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 * @since June 6th, 2014
	 */
	void "listQuestions_Success_PastGamesWithUserId"() {
		
		setup:
			def questionC = new QuestionController()
			questionC.questionService = questionService
			questionC.userService = userService
			
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			gameC.getPastGames()
			def pastGames = gameC.response.json
			println "gameC.response.json (pastGames) is the following: " + pastGames
			gameC.response.reset()
			
		when:
			questionC.params.gameId = pastGames.gameId[0]
			questionC.params.userId = Account.executeQuery('select distinct a.userId from Account a order by rand()', [max:1])[0]
			println "Sample userId is: " + questionC.params.userId
			questionC.listQuestions()
			def resp = questionC.response.json
			println "questionC.response.json is the following:" + questionC.response.json
			
		then:
			questionC.response.status == 200
			for (eachQuestion in resp) {
				def placedBet = eachQuestion.userInfo.placedBet
				def userPick = eachQuestion.userInfo.userPick
				def userPickStatus = eachQuestion.userInfo.userPickStatus
				def winnerPick = eachQuestion.winnerPick
				assert eachQuestion.questionId.getClass() == Integer
				assert eachQuestion.questionId > 0
				assert eachQuestion.content.getClass() == String
				assert eachQuestion.content.length() > 0
				assert eachQuestion.content[-1] == "?"
				assert eachQuestion.pick1.getClass() == String
				assert eachQuestion.pick1.length() > 0
				assert eachQuestion.pick2.getClass() == String
				assert eachQuestion.pick2.length() > 0
				assert eachQuestion.winnerPick.getClass() == Integer
				assert eachQuestion.userInfo.placedBet.getClass() == Boolean
				assert eachQuestion.userInfo.userPick.getClass() == Integer
				assert eachQuestion.userInfo.userPickStatus.getClass() == Integer
				
				/**
				 * placedBet: true: user has placed a bet
				 * 			  false: user has not placed any bet yet.
				 * winnerPick: -1: result has not been released yet
				 * 				0: the game is tied
				 * 				1 or 2: 1 or 2 won
				 * userPick: -1: user has not picked anything yet
				 * 			  1 or 2: user's predicted choice
				 * userPickStatus: -1: user has not picked anything yet
				 * 			  0: user's pick is tied
				 * 			  1: user has the right pick
				 * 			  2: user has the wrong pick
				 * 
				 * Logic:
				 *  If user has not placed a bet, both userPick and userPickStatus must be -1
				 *  Else If user has placed a bet,
				 *  	if the result has not been released yet or the result is a tie (i.e -1 or 0),
				 *  		winnerPick will be the same as userPickStatus (i.e if the game is a tie, both winnerPick and userPickStatus are 0)
				 *  	else if the result has been released and the game is not a tie (i.e 1 or 2),
				 *  		if the user's pick matches winner's pick, userPickStatus is 1
				 *  		else if the user's pick doesn't match the winner's pick, userPickStatus is 2
				 */
				
				if (placedBet == false) {
					assert (userPick == -1 && userPickStatus == -1)
				} else { //placedBet == true
					if (winnerPick == -1 || winnerPick == 0){
						assert winnerPick == userPickStatus
						assert (userPick == 1 || userPick == 2)
					} else { //winnerPick == 1 or 2
						if (winnerPick == userPick) {
							assert userPickStatus == 1
						} else { //winnerPick != userPick
							assert userPickStatus == 2
						}
					}
				}
				
				assert eachQuestion.pool.pick1Amount.getClass() == Integer
				assert eachQuestion.pool.pick1Amount >= 0
				assert eachQuestion.pool.pick1NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick1NumPeople >= 0
				assert eachQuestion.pool.pick1PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick1PayoutPercent >= 0
				assert eachQuestion.pool.pick1odds.getClass() == Double
				assert eachQuestion.pool.pick1odds >= 1
				assert eachQuestion.pool.pick2Amount.getClass() == Integer
				assert eachQuestion.pool.pick2Amount >= 0
				assert eachQuestion.pool.pick2NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick2NumPeople >= 0
				assert eachQuestion.pool.pick2PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick2PayoutPercent >= 0
				assert eachQuestion.pool.pick2odds.getClass() == Double
				assert eachQuestion.pool.pick2odds >= 1
				
			}
			resp.questionId.unique(false) == resp.questionId
			//unique will check the uniqueness of the input list, alter it, and return a modified list with only unique values
			//the false is to specify the unique method to not alter the original list. (because unique is passed by reference)
	}
	
	/**
	 * Integration test on listQuestions() without a gameId in the param.
	 * 
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 * @since June 6th, 2014
	 * 
	 */
	void "listQuestions_Failure_NoGameIdInParam"() {
		
		setup:
			def questionC = new QuestionController()
			questionC.questionService = questionService
			questionC.userService = userService
			
		when:
			questionC.listQuestions()
			println "questionC.response.json is the following:" + questionC.response.json
		
		then:
			questionC.response.status == 404
			questionC.response.json.error == "invalid game ID parameter"
	}
	
	/**
	 * Integration test on listQuestions() with an invalid userId.
	 * 
	 * @param gameId The unique gameId from getUpcomingGames()
	 * @param userId An invalid userId that does not exist in the datebase.
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 * @since June 6th, 2014
	 */
	void "listQuestions_Failure_WithInvalidUserIdInParam"() {
		
		setup:
			def questionC = new QuestionController()
			questionC.questionService = questionService
			questionC.userService = userService
			
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			gameC.getUpcomingGames()
			def upcomingGames = gameC.response.json
			println "gameC.response.json (upcomingGames) is the following: " + upcomingGames
			gameC.response.reset()
			
		when:
			questionC.params.gameId = upcomingGames.gameId[0]
			questionC.params.userId = "ThisUserIdShouldNotExist" //This userId should not exist in the database because this part should fail
			questionC.listQuestions()
			println "questionC.response.json is the following:" + questionC.response.json
		
		then:
			questionC.response.status == 200
			questionC.response.json.code == 102
			questionC.response.json.error == "User Id does not exists"
			
	}
}
