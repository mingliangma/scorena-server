package com.doozi

import com.doozi.scorena.Account;
import com.doozi.scorena.transaction.BetController;
import com.doozi.scorena.user.UserController;
import com.doozi.scorena.utils.HelperService;

import grails.plugins.rest.client.RestBuilder
import spock.lang.*

import org.apache.commons.lang.*

import groovy.time.TimeCategory
import groovy.time.TimeDuration

/**
 * Note: This whole BetControllerIntegrationSpec was not complete! It was not properly tested.
 * Note: For the majority of the methods below, it requires one userController to create a dummy user, and then 
 * 		 	one BetController to make a bet, thus two requests are required. For some reason, when passed in a request for userController
 *			it will be carried to other controllers. For example, if I set a BetController and attach it with its request, somehow it
 *			would use the previous UserController's request. Need to find a way around it. Possible alternative solution is to use query language
 *			to get a user and avoiding the use of UserController. So that only BetController will make a request. However, it does not seem to be
 *			the desired method.
 * Note: Some test are intended for testing failure cases that do not require a dummy user
 *
 * Author: James Pang
 */
class BetControllerIntegrationSpec extends Specification {

	def parseService
	def helperService
	def userService
	def betTransactionService
	def sessionFactory
	def _sessionToken
	
    def setup() {
    }

    def cleanup() {
    }

	void "placeBet_Success" () {
		
	}
	//need to change how to pick, questionId and sessionToken
    void "placeBet_Failure_WithoutWager"() {
		setup:
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.pick = "2"
			betC.request.questionId = "123"
			betC.request.sessionToken = "123"
		
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 404
			resp.json.code == 101
			resp.json.error == "invalid parameters"
    }
	//need to change how to pick, questionId and sessionToken
	void "placeBet_Failure_WithoutPick" () {
		setup:
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = 40
			betC.request.questionId = 123
			betC.request.sessionToken = 123
	
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 404
			resp.json.code == 101
			resp.json.error == "invalid parameters"
	}
	//need to change how to pick, questionId and sessionToken
	void "placeBet_Failure_WithoutQuestionId" () {
		setup:
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "2"
			betC.request.sessionToken = "123"
	
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 404
			resp.json.code == 101
			resp.json.error == "invalid parameters"

	}
	//need to change how to pick, questionId and sessionToken
	void "placeBet_Failure_WithoutsessionToken" () {
		setup:
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "2"
			betC.request.questionId = "123"
	
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 404
			resp.json.code == 101
			resp.json.error == "invalid parameters"

	}
	//need to change how to pick, questionId and sessionToken
	//there are two cases for failure, now just temporarily cover two cases. Better idea?
	void "placeBet_Failure_InvalidSessionToken" () {
		setup:
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "2"
			betC.request.questionId = "123"
			betC.request.sessionToken = "123"
	
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 404
			(resp.json.code || resp.json.error) == true //temporarily to cover two cases
	}
	//need to change how to pick, questionId and sessionToken
	//Key: create an account on parse but not in the db. (not done)
	void "placeBet_Failure_SessionTokenMatchWithInvalidUserId" () {
		
	}
	//need to change how to pick, questionId
	//Key: create a dummy user then bet over initial balance (2000 coins). (done)
	//need to get legit question ID.
	void "placeBet_Failure_NotEnoughCoins" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			/**
			 * Starting from here, it is where I was testing how to pass in two requests without conflicting to each other.
			 * The methods below have been attempted but they all failed.
			 */
			//userC.request.reset()
			//request.reset()
			//userC.request.close()		
			//userC.request.clearAttributes()
			//userC.request.removeAllParameters()
			//userC.session.invalidate()
			sessionFactory.currentSession.flush()
			sessionFactory.currentSession.clear()
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			def wager = userC.userService.INITIAL_BALANCE + 1000
			def betCContent = '{"wager":"'+wager+'", "pick": "2", "questionId":"123", "sessionToken": "'+sessionToken+'"}'
			betC.request.contentType = "application/json"
			betC.request.content = betCContent.getBytes()
			//betC.request.JSON.wager = "1000"
//			betC.request.wager = userC.userService.INITIAL_BALANCE + 1000 //wager has to be more than initial balance to fail
//			betC.request.pick = "2"
//			betC.request.questionId = "123"
//			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "The user does not have enough coins to make a bet"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick, questionId
	//Key: wager is 0 (done)
	//need to get legit question ID.
	void "placeBet_Failure_WagerIsZero" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "0"
			betC.request.pick = "2"
			betC.request.questionId = "123"
			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "The user cannot bet negative amount"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick, questionId
	//Key: wager a negative number (done)
	//need to get legit question ID.
	void "placeBet_Failure_WagerIsNegative" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "-100" // wager has to be negative to fail
			betC.request.pick = "2"
			betC.request.questionId = "123"
			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "The user cannot bet negative amount"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick, questionId
	//Key: pick is less than 1 (done)
	//need to get legit question ID.
	void "placeBet_Failure_PickIsLessThan1" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "-1" // pick must be a number less than 1 to fail
			betC.request.questionId = "123"
			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "the pick is not available"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick, questionId
	//Key: pick is more than 2 (done)
	//need to get legit question ID.
	void "placeBet_Failure_PickIsGreaterThan2" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "3" // pick must be a number less than 1 to fail
			betC.request.questionId = "123"
			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "the pick is not available"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick
	//Key: give a random invalid questionId (done)
	void "placeBet_Failure_InvalidQuestionId" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "2"
			betC.request.questionId = "ThisQuestionIdShouldNotExist" //this questionId must be an invalid questionId to fail
			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "the questionId does not exsist"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick, questionId
	//Key: use a questionId that will point to a game that is not a pre-event (not done)
	//need to get legit question ID.
	void "placeBet_Failure_GameStatusIsNotPreEvent_RegularGame" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "2"
			betC.request.questionId = "ThisQuestionIdShouldNotExist" //this questionId must be an invalid questionId to fail
			betC.request.sessionToken = sessionToken
			
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "the match is already started. All pool is closed"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json
	}
	//need to change how to pick, questionId
	//Key: use a questionId that will point to a game that is not a pre-event (not done)
	//need to get legit question ID.
	void "placeBet_Failure_GameStatusIsNotPreEvent_CustomGame" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println "The dummy user is created as the following: " + content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println "userC.response.json is: " + userC.response.json
			
			userC.response.reset()
			
			def betC = new BetController()
			betC.betTransactionService = betTransactionService
			betC.userService = userService
			betC.request.wager = "40"
			betC.request.pick = "2"
			betC.request.questionId = "ThisQuestionIdShouldNotExist" //this questionId must be an invalid questionId to fail
			betC.request.sessionToken = sessionToken
		
		when:
			betC.placeBet()
			def resp = betC.response
			println "betC.response.json is the following:" + betC.response.json
			
		then:
			resp.status == 400
			resp.json.code == 202
			resp.json.error == "the match is already started. All pool is closed"
			
		cleanup:
			def deleteResp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println "Cleanup: parseService delete message: " + deleteResp.json

	}
	
	void "placeBet_Failure_BetTransactionExisted" () {
		
	}
	
	void "placeBet_Failure_AccountDataNotSaved" () {
		
	}
	
	void "placeBet_Failure_QuestionDataNotSaved" () {
		
	}
}
