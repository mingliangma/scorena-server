package com.doozi



import com.doozi.scorena.Account;
import com.doozi.scorena.GameController;
import com.doozi.scorena.UserController;
import com.doozi.scorena.utils.HelperService;

import grails.plugins.rest.client.RestBuilder
import spock.lang.*

import org.apache.commons.lang.*

import groovy.time.TimeCategory
import groovy.time.TimeDuration


/**
 *
 */
class GameControllerIntegrationSpec extends Specification {

	def parseService
	def gameService
	def sportsDataService
	def helperService
	def userService
	
    def setup() {
    }

    def cleanup() {
    }

	/**
	 * Integration test on getFeatureGames() without logging in.
	 * 
	 * <p> This checks if each component of the response is a valid class and length, and also checks if there are any duplicated games by comparing gameId.
	 * Note: gameStatus can only be "pre-event" in feature games
	 * This test is also the basis of all the other tests in GameController with in-line explanation.
	 * </p>
	 * 
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 *	
	 */
    void "getFeatureGames_Success_NoUserIdInParam"() {
		
		setup:
			def numberOfFeatureGames = 3 //The number size was hard-coded as 3 in QuestionService.listFeatureQuestions. Suggest changing to a variable.
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService

		when:
			gameC.getFeatureGames()
			def currentDate = new Date()
			def timeInterval
			def currentTimeLowerBound
			use (TimeCategory) {
				timeInterval = 3.seconds
				currentTimeLowerBound = currentDate - timeInterval
			}
			def resp = gameC.response.json
			def gameIdList = gameC.response.json.gameId
			def questionIdList = gameC.response.json.question.questionId
			println "Complete JSON response after calling getFeatureGames(): " + resp
			
		then:
			gameC.response.status == 200
			resp.size() == numberOfFeatureGames 
			
			resp.leagueName.size() == numberOfFeatureGames
			for (eachLeagueName in resp.leagueName) {
				assert eachLeagueName.getClass() == String //In integration test, it is necessary to put the key word assert in for-loop.
				assert eachLeagueName.length() > 0
			}
			
			resp.leagueCode.size() == numberOfFeatureGames
			for (eachLeagueCode in resp.leagueCode) {
				assert eachLeagueCode.getClass() == String
				assert eachLeagueCode.length() > 0
			}
			
			resp.gameId.size() == numberOfFeatureGames
			for (eachGameId in gameIdList) {
				assert eachGameId.getClass() == String
				assert eachGameId.length() > 0
			}
			
			resp.type.size() == numberOfFeatureGames
			for (eachType in resp.type) {
				assert eachType.getClass() == String
				assert eachType.length() > 0
			}
			
			resp.gameStatus.size() == numberOfFeatureGames
			for (eachGameStatus in resp.gameStatus) {
				assert eachGameStatus == "pre-event"
			}
			
			resp.date.size() == numberOfFeatureGames
			for (eachDate in resp.date) {
				assert eachDate.getClass() == String
				assert eachDate.length() > 0
				//This adds a buffer time for discrepancy between server time and client time
				assert helperService.parseDateFromString(eachDate) > currentTimeLowerBound
			}
			
			resp.away.teamname.size() == numberOfFeatureGames
			for (eachAwayTeamName in resp.away.teamname) {
				assert eachAwayTeamName.getClass() == String
				assert eachAwayTeamName.length() > 0
			}
			
			resp.away.score.size() == numberOfFeatureGames
			//because these two nulls are belong to different classes, need to parse them into String
			for (eachAwayScore in resp.away.score) {
				assert eachAwayScore.toString() == null.toString()
			} 
			
			resp.home.teamname.size() == numberOfFeatureGames
			for (eachHomeTeamName in resp.home.teamname) {
				assert eachHomeTeamName.getClass() == String
				assert eachHomeTeamName.length() > 0
			}
			
			resp.home.score.size() == numberOfFeatureGames
			for (eachHomeScore in resp.home.score) {
				assert eachHomeScore.toString() == null.toString()
			}
			
			resp.question.size() == numberOfFeatureGames
			for (eachQuestion in resp.question) {
				assert eachQuestion.questionId.getClass() == Integer
				assert eachQuestion.questionId >= 0
				assert eachQuestion.content.getClass() == String
				assert eachQuestion.content.length() > 0
				assert eachQuestion.content[-1] == "?"
				assert eachQuestion.pick1.getClass() == String
				assert eachQuestion.pick1.length() > 0
				assert eachQuestion.pick2.getClass() == String
				assert eachQuestion.pick2.length() > 0
				assert eachQuestion.userInfo == []
				assert eachQuestion.winnerPick == -1
				assert eachQuestion.pool.pick1Amount.getClass() == Integer
				assert eachQuestion.pool.pick1Amount >= 0
				assert eachQuestion.pool.pick2Amount.getClass() == Integer
				assert eachQuestion.pool.pick2Amount >= 0
				assert eachQuestion.pool.pick1NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick1NumPeople >= 0
				assert eachQuestion.pool.pick2NumPeople.getClass() == Integer
				assert eachQuestion.pool.pick2NumPeople >= 0
				assert eachQuestion.pool.pick1PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick1PayoutPercent >= 0
				assert eachQuestion.pool.pick2PayoutPercent.getClass() == Integer
				assert eachQuestion.pool.pick2PayoutPercent >= 0
				assert eachQuestion.pool.pick1odds.getClass() == Double
				assert eachQuestion.pool.pick1odds >= 1
				assert eachQuestion.pool.pick2odds.getClass() == Double
				assert eachQuestion.pool.pick2odds >= 1
			}
			assert questionIdList == questionIdList.unique(false)
    }

	/**
	 * Integration test on getUpcomingGames() without logging in.
	 *
	 * <p> This checks if each component of the response is a valid class and length, and also checks if there are any duplicated games by comparing gameId.
	 * Note: gameStatus can be either "pre-event", "mid-event", or "intermission"
	 *
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 *
	 */
	void "getUpcomingGames_Success_NoUserIdInParam"() {
		
		setup:
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService

		when:
			gameC.getUpcomingGames()
			def currentDate = new Date()
			def timeInterval
			def currentTimeLowerBound
			def currentTimeUpperBound
			use (TimeCategory) {
				timeInterval = 3.seconds
				currentTimeLowerBound = currentDate - timeInterval
				currentTimeUpperBound = currentDate + timeInterval
			}
			def resp = gameC.response.json
			def gameIdList = gameC.response.json.gameId
			def numberOfUpcomingGames = gameC.response.json.size()
			println "Complete JSON response after calling getFeatureGames(): " + resp
			
		then:
			gameC.response.status == 200
			
			resp.leagueName.size() == numberOfUpcomingGames
			for (eachLeagueName in resp.leagueName) {
				assert eachLeagueName.getClass() == String //In integration test, it is necessary to put the key word assert in for-loop.
				assert eachLeagueName.length() > 0
			}
			
			resp.leagueCode.size() == numberOfUpcomingGames
			for (eachLeagueCode in resp.leagueCode) {
				assert eachLeagueCode.getClass() == String
				assert eachLeagueCode.length() > 0
			}
			
			resp.gameId.size() == numberOfUpcomingGames
			for (eachGameId in gameIdList) {
				assert eachGameId.getClass() == String
				assert eachGameId.length() > 0
			}
			
			//check for duplicate games by using two pointers, i and j. i will iterate the entire list and every time j will iterate the rest of the list.
//			for (int i = 0; i < gameIdList.size() - 1; i++) {
//				for (int j = i + 1; j < gameIdList.size(); j++) {
//					assert gameIdList[i] != gameIdList[j]
//				}
//			}
			
			resp.type.size() == numberOfUpcomingGames
			for (eachType in resp.type) {
				assert eachType.getClass() == String
				assert eachType.length() > 0
			}
			
			//Checking gameStatus and Date altogether.
			for (eachGame in resp) {
				assert (eachGame.gameStatus == "pre-event" || eachGame.gameStatus == "intermission" || eachGame.gameStatus == "mid-event")
				if (eachGame.gameStatus == "pre-event") {
					assert helperService.parseDateFromString(eachGame.date) > currentTimeLowerBound
				} else {
					assert helperService.parseDateFromString(eachGame.date) < currentTimeUpperBound
				}
			}
			
			resp.away.teamname.size() == numberOfUpcomingGames
			for (eachAwayTeamName in resp.away.teamname) {
				assert eachAwayTeamName.getClass() == String
				assert eachAwayTeamName.length() > 0
			}
			
			resp.away.score.size() == numberOfUpcomingGames
			//because these two nulls are belong to different classes, need to parse them into String
			for (eachAwayScore in resp.away.score) {
				assert (eachAwayScore.toString() == null.toString() || eachAwayScore.getClass() == String)
				if (eachAwayScore.getClass() == String){
					assert eachAwayScore.toInteger() >= 0
				}
			}
			
			resp.home.teamname.size() == numberOfUpcomingGames
			for (eachHomeTeamName in resp.home.teamname) {
				assert eachHomeTeamName.getClass() == String
				assert eachHomeTeamName.length() > 0
			}
			
			resp.home.score.size() == numberOfUpcomingGames
			for (eachHomeScore in resp.home.score) {
				assert (eachHomeScore.toString() == null.toString() || eachHomeScore.getClass() == String)
				if (eachHomeScore.getClass() == Integer) {
					assert eachHomeScore >= 0
				}
			}
			
			assert gameIdList == gameIdList.unique(false)
			
	}
	
	
	/**
	 * Integration test on getUpcomingGames() with a userId in the param.
	 * 
	 * IMPORTANT NOTE: This test does not create a new user to pass in a valid userId. Instead, it passed in an invalid userId which still returned {"placedBet" : false}
	 * 		for all the questions. It is desired to create a valid user, place a bet, then call getUpcomingGames.
	 * 
	 *
	 * This checks if each component of the response is a valid class and length, and also checks if there are any duplicated games by comparing gameId.
	 * 		Note: gameStatus can be either "pre-event", "mid-event", or "intermission"
	 * 
	 *
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 *
	 */
	void "getUpcomingGames_Success_WithUserIdInParam"() {
		
		setup:
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def password = "12345"
			def email = "testingname"+ranNum+"@gmail.com"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "Passing in this content for creating a dummy user: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			
			userC.createNewUser()
			
			def sessionTokenCopy = userC.response.json.sessionToken
			def userIdCopy = userC.response.json.userId
			
			userC.response.reset()
			
			gameC.params.userId = userIdCopy

		when:
			gameC.getUpcomingGames()
			def currentDate = new Date()
			def timeInterval
			def currentTimeLowerBound
			def currentTimeUpperBound
			use (TimeCategory) {
				timeInterval = 3.seconds
				currentTimeLowerBound = currentDate - timeInterval
				currentTimeUpperBound = currentDate + timeInterval
			}
			def resp = gameC.response.json
			def gameIdList = gameC.response.json.gameId
			def numberOfUpcomingGames = gameC.response.json.size()
			println "Complete JSON response after calling getFeatureGames(): " + resp
			
		then:
			gameC.response.status == 200
			
			resp.leagueName.size() == numberOfUpcomingGames
			for (eachLeagueName in resp.leagueName) {
				assert eachLeagueName.getClass() == String //In integration test, it is necessary to put the key word assert in for-loop.
				assert eachLeagueName.length() > 0
			}
			
			resp.leagueCode.size() == numberOfUpcomingGames
			for (eachLeagueCode in resp.leagueCode) {
				assert eachLeagueCode.getClass() == String
				assert eachLeagueCode.length() > 0
			}
			
			resp.gameId.size() == numberOfUpcomingGames
			for (eachGameId in gameIdList) {
				assert eachGameId.getClass() == String
				assert eachGameId.length() > 0
			}
			
			//check for duplicate games by using two pointers, i and j. i will iterate the entire list and every time j will iterate the rest of the list.
//			for (int i = 0; i < gameIdList.size() - 1; i++) {
//				for (int j = i + 1; j < gameIdList.size(); j++) {
//					assert gameIdList[i] != gameIdList[j]
//				}
//			}
			
			resp.type.size() == numberOfUpcomingGames
			for (eachType in resp.type) {
				assert eachType.getClass() == String
				assert eachType.length() > 0
			}
			
			//Checking gameStatus and Date altogether.
			for (eachGame in resp) {
				assert (eachGame.gameStatus == "pre-event" || eachGame.gameStatus == "intermission" || eachGame.gameStatus == "mid-event")
				if (eachGame.gameStatus == "pre-event") {
					assert helperService.parseDateFromString(eachGame.date) > currentTimeLowerBound
				} else {
					assert helperService.parseDateFromString(eachGame.date) < currentTimeUpperBound
				}
			}
			
			resp.away.teamname.size() == numberOfUpcomingGames
			for (eachAwayTeamName in resp.away.teamname) {
				assert eachAwayTeamName.getClass() == String
				assert eachAwayTeamName.length() > 0
			}
			
			resp.away.score.size() == numberOfUpcomingGames
			//because these two nulls are belong to different classes, need to parse them into String
			for (eachAwayScore in resp.away.score) {
				assert (eachAwayScore.toString() == null.toString() || eachAwayScore.getClass() == String)
				if (eachAwayScore.getClass() == String){
					assert eachAwayScore.toInteger() >= 0
				}
			}
			
			resp.home.teamname.size() == numberOfUpcomingGames
			for (eachHomeTeamName in resp.home.teamname) {
				assert eachHomeTeamName.getClass() == String
				assert eachHomeTeamName.length() > 0
			}
			
			resp.home.score.size() == numberOfUpcomingGames
			for (eachHomeScore in resp.home.score) {
				assert (eachHomeScore.toString() == null.toString() || eachHomeScore.getClass() == String)
				if (eachHomeScore.getClass() == Integer) {
					assert eachHomeScore >= 0
				}
			}
			
			resp.placedBet.size() == numberOfUpcomingGames
			for (eachPlacedBet in resp.placedBet) {
				assert eachPlacedBet.getClass() == Boolean
				assert (eachPlacedBet == false || eachPlacedBet == true)
			}
			
			assert gameIdList == gameIdList.unique(false)
		
		cleanup:
			def respCleanup = parseService.deleteUser(new RestBuilder(), sessionTokenCopy, userIdCopy)
			println "cleanup json response: " + respCleanup.json
			
	}
	
	/**
	 * Integration test on getPastGames() without logging in.
	 *
	 * <p> This checks if each component of the response is a valid class and length, and also checks if there are any duplicated games by comparing gameId.
	 * Note: gameStatus can only be "post-game"
	 * </p>
	 *
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 *
	 */
	void "getPastGames_Success_NoUserIdInParam"() {
		
		setup:
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService

		when:
			gameC.getPastGames()
			def currentDate = new Date()
			def timeInterval
			def currentTimeUpperBound
			use (TimeCategory) {
				timeInterval = 3.seconds
				currentTimeUpperBound = currentDate + timeInterval
			}
			def resp = gameC.response.json
			def gameIdList = gameC.response.json.gameId
			def numberOfPastGames = gameC.response.json.size()
			println "Complete JSON response after calling getFeatureGames(): " + resp
			
		then:
			gameC.response.status == 200
			
			resp.leagueName.size() == numberOfPastGames
			for (eachLeagueName in resp.leagueName) {
				assert eachLeagueName.getClass() == String //In integration test, it is necessary to put the key word assert in for-loop.
				assert eachLeagueName.length() > 0
			}
			
			resp.leagueCode.size() == numberOfPastGames
			for (eachLeagueCode in resp.leagueCode) {
				assert eachLeagueCode.getClass() == String
				assert eachLeagueCode.length() > 0
			}
			
			resp.gameId.size() == numberOfPastGames
			for (eachGameId in gameIdList) {
				assert eachGameId.getClass() == String
				assert eachGameId.length() > 0
			}
			
			//check for duplicate games by using two pointers, i and j. i will iterate the entire list and every time j will iterate the rest of the list.
//			for (int i = 0; i < gameIdList.size() - 1; i++) {
//				for (int j = i + 1; j < gameIdList.size(); j++) {
//					assert gameIdList[i] != gameIdList[j]
//				}
//			}
			
			resp.type.size() == numberOfPastGames
			for (eachType in resp.type) {
				assert eachType.getClass() == String
				assert eachType.length() > 0
			}
			
			resp.gameStatus.size() == numberOfPastGames
			for (eachGameStatus in resp.gameStatus) {
				assert eachGameStatus == "post-event"
			}
			
			resp.date.size() == numberOfPastGames
			for (eachDate in resp.date) {
				assert eachDate.getClass() == String
				assert eachDate.length() > 0
				//This adds a buffer time for discrepancy between server time and client time
				assert helperService.parseDateFromString(eachDate) < currentTimeUpperBound
			}
			
			resp.away.teamname.size() == numberOfPastGames
			for (eachAwayTeamName in resp.away.teamname) {
				assert eachAwayTeamName.getClass() == String
				assert eachAwayTeamName.length() > 0
			}
			
			resp.away.score.size() == numberOfPastGames
			//because these two nulls are belong to different classes, need to parse them into String
			for (eachAwayScore in resp.away.score) {
				assert eachAwayScore.getClass() == String
				assert eachAwayScore.length() > 0
				assert eachAwayScore.toInteger() >= 0
			}
			
			resp.home.teamname.size() == numberOfPastGames
			for (eachHomeTeamName in resp.home.teamname) {
				assert eachHomeTeamName.getClass() == String
				assert eachHomeTeamName.length() > 0
			}
			
			resp.home.score.size() == numberOfPastGames
			for (eachHomeScore in resp.home.score) {
				assert eachHomeScore.getClass() == String
				assert eachHomeScore.length() > 0
				assert eachHomeScore.toInteger() >= 0
			}
			
			assert gameIdList == gameIdList.unique(false)
			
	}
	
	/**
	 * Integration test on getPastGames() with a userId in the param.
	 *
	 * <p> This checks if each component of the response is a valid class and length, and also checks if there are any duplicated games by comparing gameId.
	 * Note: gameStatus can only be "post-game"
	 * </p>
	 *
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 *
	 */
	void "getPastGames_Success_WithUserIdInParam"() {
		
		setup:
			def gameC = new GameController()
			gameC.gameService = gameService
			gameC.sportsDataService = sportsDataService
			
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def password = "12345"
			def email = "testingname"+ranNum+"@gmail.com"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "Passing in this content for creating a dummy user: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			
			userC.createNewUser()
			
			def sessionTokenCopy = userC.response.json.sessionToken
			def userIdCopy = userC.response.json.userId
			
			userC.response.reset()
			
			gameC.params.userId = userIdCopy

		when:
			gameC.getPastGames()
			def currentDate = new Date()
			def timeInterval
			def currentTimeUpperBound
			use (TimeCategory) {
				timeInterval = 3.seconds
				currentTimeUpperBound = currentDate + timeInterval
			}
			def resp = gameC.response.json
			def gameIdList = gameC.response.json.gameId
			def numberOfPastGames = gameC.response.json.size()
			println "Complete JSON response after calling getFeatureGames(): " + resp
			
		then:
			gameC.response.status == 200
			
			resp.leagueName.size() == numberOfPastGames
			for (eachLeagueName in resp.leagueName) {
				assert eachLeagueName.getClass() == String //In integration test, it is necessary to put the key word assert in for-loop.
				assert eachLeagueName.length() > 0
			}
			
			resp.leagueCode.size() == numberOfPastGames
			for (eachLeagueCode in resp.leagueCode) {
				assert eachLeagueCode.getClass() == String
				assert eachLeagueCode.length() > 0
			}
			
			resp.gameId.size() == numberOfPastGames
			for (eachGameId in gameIdList) {
				assert eachGameId.getClass() == String
				assert eachGameId.length() > 0
			}
			
			//check for duplicate games by using two pointers, i and j. i will iterate the entire list and every time j will iterate the rest of the list.
//			for (int i = 0; i < gameIdList.size() - 1; i++) {
//				for (int j = i + 1; j < gameIdList.size(); j++) {
//					assert gameIdList[i] != gameIdList[j]
//				}
//			}

			resp.type.size() == numberOfPastGames
			for (eachType in resp.type) {
				assert eachType.getClass() == String
				assert eachType.length() > 0
			}
			
			resp.gameStatus.size() == numberOfPastGames
			for (eachGameStatus in resp.gameStatus) {
				assert eachGameStatus == "post-event"
			}
			
			resp.date.size() == numberOfPastGames
			for (eachDate in resp.date) {
				assert eachDate.getClass() == String
				assert eachDate.length() > 0
				//This adds a buffer time for discrepancy between server time and client time
				assert helperService.parseDateFromString(eachDate) < currentTimeUpperBound
			}
			
			resp.away.teamname.size() == numberOfPastGames
			for (eachAwayTeamName in resp.away.teamname) {
				assert eachAwayTeamName.getClass() == String
				assert eachAwayTeamName.length() > 0
			}
			
			resp.away.score.size() == numberOfPastGames
			//because these two nulls are belong to different classes, need to parse them into String
			for (eachAwayScore in resp.away.score) {
				assert eachAwayScore.getClass() == String
				assert eachAwayScore.length() > 0
				assert eachAwayScore.toInteger() >= 0
			}
			
			resp.home.teamname.size() == numberOfPastGames
			for (eachHomeTeamName in resp.home.teamname) {
				assert eachHomeTeamName.getClass() == String
				assert eachHomeTeamName.length() > 0
			}
			
			resp.home.score.size() == numberOfPastGames
			for (eachHomeScore in resp.home.score) {
				assert eachHomeScore.getClass() == String
				assert eachHomeScore.length() > 0
				assert eachHomeScore.toInteger() >= 0
			}
			
			resp.placedBet.size() == numberOfPastGames
			for (eachPlacedBet in resp.placedBet) {
				assert eachPlacedBet.getClass() == Boolean
				assert (eachPlacedBet == false || eachPlacedBet == true)
			}
			
			assert gameIdList == gameIdList.unique(false)
		
		cleanup:
			def respCleanup = parseService.deleteUser(new RestBuilder(), sessionTokenCopy, userIdCopy)
			println "cleanup json response: " + respCleanup.json
			
	}
}
