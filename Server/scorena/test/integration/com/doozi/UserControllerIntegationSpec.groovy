package com.doozi



import com.doozi.scorena.Account;
import com.doozi.scorena.UserController;

import grails.plugins.rest.client.RestBuilder
import spock.lang.*
import org.apache.commons.lang.*

/**
 *
 */
class UserControllerIntegationSpec extends Specification {
	def parseService
	def userService
	def _sessionToken
    def setup() {
    }

    def cleanup() {
		
    }

	/**
	 *  Integration test on createNewUser() by passing in dummy user data.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.1
	 *  
	 *  @since 1.0
	 *  @author Mingliang Ma
	 */
    void "signupUser_Success" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'

			println content
		
		when:
			println "when"
			def userC = new UserController()
			userC.userService = userService			
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
						
			userC.createNewUser()
			
			def sessionTokenCopy = userC.response.json.sessionToken
			def userIdCopy = userC.response.json.userId
			
		then:
			println "then"
			userC.response.status == 201
			userC.response.json.username == username
			userC.response.json.userId != null
			userC.response.json.sessionToken != null			
			
			def account = Account.findByUserId(userC.response.json.userId)
			account != null
			account.userId == userC.response.json.userId
			account.username == username
			account.currentBalance == userC.userService.INITIAL_BALANCE
			account.previousBalance == userC.userService.PREVIOUS_BALANCE
			
			def respRetreive = parseService.retreiveUser(new RestBuilder(), userC.response.json.userId)
			respRetreive.status == 200
			respRetreive.json.gender == "male"
			respRetreive.json.region == "Toronto"
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionTokenCopy, userIdCopy)
			println resp.json
    }
	
	/**
	 *  Integration test on createNewUser() by not passing in any email.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "signupUser_Failure_InvalidEmail" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111", "gender":"'+gender+'", "region":"'+region+'"}'

			println content
		
		when:
			println "when"
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
						
			def createNewUserCall = userC.createNewUser()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
			createNewUserCall == null
	}
	
	/**
	 *  Integration test on createNewUser() by not passing in any password.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "signupUser_Failure_InvalidPassword" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'

			println content
		
		when:
			println "when"
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
						
			def createNewUserCall = userC.createNewUser()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
			createNewUserCall == null
	}
	
	/**
	 *  Integration test on createNewUser() by not passing in any username.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "signupUser_Failure_InvalidUserName" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def email = "testingname"+ranNum+"@gmail.com"
			def gender = "male"
			def region = "Toronto"
			def content = '{"password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'

			println content
		
		when:
			println "when"
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
						
			def createNewUserCall = userC.createNewUser()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
			createNewUserCall == null
	}
	
	/**
	 *  Integration test on createNewUser() by creating a dummy user and attempting to create the same dummy user again.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "signupUser_Failure_AccountExisted" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'

			println content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
						
			userC.createNewUser()
			
			def sessionTokenCopy = userC.response.json.sessionToken
			def userIdCopy = userC.response.json.userId
			
		when:
			println "when"
			userC.response.reset()
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			
			def createNewUserCall = userC.createNewUser()
			
		then:
			userC.response.status == 400
			userC.response.json.error == "username " + username + " already taken"
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionTokenCopy, userIdCopy)
			println resp.json
	}
	
	/**
	 *  Integration test on login() by creating a dummy user and log in.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.1
	 *
	 *	@author Mingliang Ma
	 *	@Since 1.0
	 */
	void "loginUser_Success"(){
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()						
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println userC.response.json
			
			userC.response.reset()
			
		when: 		
			userC.params.username = username.encodeAsURL()
			userC.params.password = password.encodeAsURL()
			userC.login()
			
		then:
			userC.response.status == 200
			userC.response.json.username == username
			userC.response.json.userId != null
			userC.response.json.sessionToken != null
			userC.response.json.currentBalance == userC.userService.INITIAL_BALANCE
			println userC.response.json
		
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on login() by creating a dummy user and log in without passing in any username.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "loginUser_Failure_InvalidUsername"(){
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println userC.response.json
			
			userC.response.reset()
			
		when:
			userC.params.password = password.encodeAsURL()
			userC.login()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"

		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	/**
	 *  Integration test on login() by creating a dummy user and log in without passing in any password.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "loginUser_Failure_InvalidPassword"(){
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println userC.response.json
			
			userC.response.reset()
			
		when:
			userC.params.username = username.encodeAsURL()
			userC.login()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"

		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on login() by creating a dummy user and log in with incorrect password.
	 *  NOTE: The same result will be obtained if an incorrect username is passed in, or the the input account does not even exist.
	 *  
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "loginUser_Failure_WrongPassword"(){
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
		
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			println userC.response.json
			
			userC.response.reset()
			
			def wrongPassword = "99999" //This password should not be the same as the one above
			
		when:
			userC.params.username = username.encodeAsURL()
			userC.params.password = wrongPassword.encodeAsURL()
			userC.login()
			
		then:
			userC.response.status == 400
			userC.response.json.error == "invalid login parameters"
			userC.response.json.code == 101

		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
//	/**
//	 *  Integration test on deleteUserProfile() by creating a dummy user and deleting this user. (NOT DONE YET)
//	 *
//	 *  @author Chih-Hong (James) Pang
//	 *  @version 1.1
//	 *
//	 *	@author Mingliang Ma
//	 *	@version 1.0
//	 */
//	//!!!!!!!!!!!!NOT DONE YET!!!!!!!!!!!!!!
//	void "deleteUser" (){
//		setup:
//			Random random = new Random()
//			def ranNum = random.nextInt(10000).toString()
//			def username = "testingname"+ranNum
//			def email = "testingname"+ranNum+"@gmail.com"
//			def password = "11111111"
//			def gender = "male"
//			def region = "Toronto"
//			def content = '{"username":"'+username+'","password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
//			println content
//			
//			def userC = new UserController()
//			userC.userService = userService
//			
//			userC.request.contentType = "application/json"
//			userC.request.content = content.getBytes()						
//			userC.createNewUser()
//		
//			def userId = userC.response.json.userId
//			def sessionToken = userC.response.json.sessionToken
//			
//			userC.response.reset() //a reset is necessary to clear the response because somehow it would not be overwritten
//			
//			def userC2 = new UserController() 
//			userC2.userService = userService
//			
//		when:			
//			userC2.request.addHeader("sessionToken", sessionToken)
//			userC2.params.userId = userId
//			userC2.deleteUserProfile()
//
//		then:
//			userC2.response.status == 200
//			userC2.response.json == [:]
//			def resp = parseService.retreiveUser(new RestBuilder(), userId)
//			resp.status == 404
//			resp.json.code == 101
//
//			
//			def account = Account.findByUserId(userId)
//			account == null
//	}
	
	/**
	 *  Integration test on getBalance() by getting the initial balance of a newly created user.
	 *  
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserBalanceTest_Succees" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname" + ranNum
			def email = "testingname" + ranNum + "@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'", "password":"11111111","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
			println content
		
			def userC = new UserController()
			userC.userService = userService
			
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			userC.createNewUser()
			
			def userId = userC.response.json.userId
			def sessionToken = userC.response.json.sessionToken
			
			userC.response.reset()
			
		when:
			userC.params.userId = userId
			userC.getBalance()
		
		then:
			userC.response.status == 200
			userC.response.json.currentBalance == userC.userService.INITIAL_BALANCE
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on getBalance() by giving a null userId.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserBalanceTest_Failure_NoUserId" (){
		setup:
			def userC = new UserController()
			userC.userService = userService
			def userId = null
		when:
			userC.params.userId = userId
			def getBalanceCall = userC.getBalance()
		then:
			userC.response.status == 404
			userC.response.json.error == "userId is required"
			getBalanceCall == null
	}
	
	/**
	 *  Integration test on getBalance() by giving a random userId that does not exist in user database.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserBalanceTest_Failure_AccountNotExsisted" (){
		setup:
			def userC = new UserController()
			userC.userService = userService
			def randomId = RandomStringUtils.random(10,true,true) //generate a random 10-digit id consisting of numbers and characters
			def account = Account.findByUserId(randomId)
			
			//ensure the randomId does not point to any exsisting account
			while (account != null){
				randomId = RandomStringUtils.random(10,true,true)
				account = Account.findByUserId(randomId)
			}
			println "The random user id is: " + randomId
			
		when:
			userC.params.userId = randomId
			def getBalanceCall = userC.getBalance()
			
		then:
			userC.response.status == 200
			userC.response.json.code == 500
			userC.response.json.error == "user account does not exist"
	}
	
//	//!!!!NOT DONE YET!!!! Can't get the then part to go to incorrect output
//	void "passwordReset_Failure_InvalidEmail" () {
//		setup:
//			Random random = new Random()
//			def ranNum = random.nextInt(10000).toString()
//			def username = "testingname"+ranNum
//			def email = "testingname"+ranNum+"@gmail.com"
//			def password = "11111111"
//			def gender = "male"
//			def region = "Toronto"
//			def content = '{"username":"'+username+'","password":"11111111", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
//			println content
//			
//			def userC = new UserController()
//			userC.userService = userService
//			userC.request.contentType = "application/json"
//			userC.request.content = content.getBytes()
//			userC.createNewUser()
//		
//			def userId = userC.response.json.userId
//			def sessionToken = userC.response.json.sessionToken
//			println userC.response.json
//			
//			userC.response.reset()
//		
//		when:
//			userC.passwordReset()
//			
//		then:
//			userC.response.status == 404
//			userC.response.json.error == "invalid parameters"
//			
//		cleanup:
//			println "cleanup"
//			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
//			println resp.json
//	}
}
