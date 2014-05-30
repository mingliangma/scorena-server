package com.doozi



import com.doozi.scorena.Account;
import com.doozi.scorena.GameController;
import com.doozi.scorena.controllerservice.HelperService;

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
	
    def setup() {
    }

    def cleanup() {
    }

	/**
	 * Integration test on getFeatureGames() without logging in.
	 * 
	 * <p> This checks each component of the response is a valid class and length, and also checks if there are any duplicated games by comparing gameId.
	 * This test is also the basis of all the other tests in GameController with in-line explanation.
	 * </p>
	 * 
	 * @author Chih-Hong (James) Pang
	 * @version 1.0
	 *	
	 */
    void "getFeatureGames_Success_NoUserIdInParam"() {
		
		Integer.metaClass.mixin TimeCategory //these two lines made it easier for doing operations on Date Objects.
		Date.metaClass.mixin TimeCategory
		
		setup:
			def numberOfFeatureGames = 3 //The number size was hard-coded as 3 in QuestionService.listFeatureQuestions. Suggest changing to a variable.
			def userC = new GameController()
			userC.gameService = gameService
			userC.sportsDataService = sportsDataService

		when:
			userC.getFeatureGames()
			def currentDate = new Date()
			def timeInterval = 3.seconds
			def currentTimeLowerBound = currentDate - timeInterval
			def resp = userC.response.json
			def gameIdList = userC.response.json.gameId
			println "Complete JSON response after calling getFeatureGames(): " + resp
			
		then:
			userC.response.status == 200
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
			
			//check for duplicate games by using two pointers, i and j. i will iterate the entire list and every time j will iterate the rest of the list.
			for (int i = 0; i < gameIdList.size() - 1; i++) {
				for (int j = i + 1; j < gameIdList.size(); j++) {
					assert gameIdList[i] != gameIdList[j]
				}
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
				assert (eachQuestion.winnerPick == -1 || eachQuestion.winnerPick == 0 
						|| eachQuestion.winnerPick == 1 || eachQuestion.winnerPick == 2)
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
			
    }
}
