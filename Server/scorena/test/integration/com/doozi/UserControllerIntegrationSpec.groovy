package com.doozi



import com.doozi.scorena.Account;
import com.doozi.scorena.UserController;
import com.doozi.scorena.controllerservice.HelperService;

import grails.plugins.rest.client.RestBuilder
import spock.lang.*
import org.apache.commons.lang.*

/**
 *
 */
class UserControllerIntegrationSpec extends Specification {
	def parseService
	def userService
	def helperService
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
	 *  Integration test on createNewUser() by passing in an email address with spaces and no "@" sign.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 *
	 */
	void "signupUser_Failure_InvalidEmailFormat" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "This is an invalid email" //This email should be in an invalid format (i.e with spaces)
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
			userC.response.status == 400
			userC.response.json.error == "invalid email " + email
			userC.response.json.code == 125
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionTokenCopy, userIdCopy)
			println resp.json
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
	
	/**
	 *  Integration test on deleteUserProfile() by creating a dummy user and deleting this user.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.1
	 *
	 *	@author Mingliang Ma
	 *	@version 1.0
	 */
	void "deleteUserProfile_Success" (){
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
			
			userC.response.reset() //a reset is necessary to clear the response because somehow it would not be overwritten
			
		when:			
			userC.request.addHeader("sessionToken", sessionToken)
			userC.params.userId = userId
			userC.deleteUserProfile()

		then:
			userC.response.status == 200
			userC.response.json == [:]
			Account.findByUserId(userId) == null
			
		cleanup:
			println "cleanup"
			if (userC.response.status != 200){ //In case the test failed, the dummy account will still be deleted.
				def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			}
	}
	
	/**
	 *  Integration test on deleteUserProfile() by not setting any sessionToken for the header.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.1
	 *
	 */
	void "deleteUserProfile_Failure_RequestFoundNoSessionToken" () {
		setup:
			def userC = new UserController()
			userC.userService = userService

		when:
			userC.params.userId = "ThisIsDummyUserId"
			userC.deleteUserProfile()
		
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
	}
	
	/**
	 *  Integration test on deleteUserProfile() by not setting any userId in the params.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.1
	 *
	 */
	void "deleteUserProfile_Failure_RequestFoundNoUserId" () {
		setup:
			def userC = new UserController()
			userC.userService = userService

		when:
			userC.request.addHeader("sessionToken", "ThisIsDummySessionToken")
			userC.deleteUserProfile()
		
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
	}
	
	/**
	 *  Integration test on deleteUserProfile() by giving a userId that does not exist in database.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.1
	 *
	 */
	void "deleteUserProfile_Failure_AccountNotExsisted" () {
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
			
			userC.response.reset()

		when:
			userC.request.addHeader("sessionToken", sessionToken)
			userC.params.userId = "ThisIdShouldNotExist"
			userC.deleteUserProfile()
		
		then:
			userC.response.status == 400
			userC.response.json.error == "Parse::UserCannotBeAlteredWithoutSessionError"
			userC.response.json.code == 206
			Account.findByUserId(userId) != null
		
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
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
	
	/**
	 *  Integration test on passwordReset() by creating a dummy user and requesting a password reset email to be sent to the dummy email address.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "passwordReset_Success" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com" //If you use a valid email address, there should be an email sent to this address.
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"11111111", "email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
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
			userC.passwordReset()
			
		then:
			userC.response.status == 200
			userC.response.json == [:]
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on passwordReset() by having no request for any email.
	 *	
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "passwordReset_Failure_RequestFoundNoEmail" () {
		setup:	
			def userC = new UserController()
			userC.userService = userService
	
		when:
			userC.passwordReset()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
	}
	
	/**
	 *  Integration test on passwordReset() by passing in an non-exsisting email in the request.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "passwordReset_Failure_NoUserFoundWithEmail" () {
		setup:
			def invalidEmail = "ThisEmailIs@not.valid"	
			def content = '{"email":"'+invalidEmail+'"}'
			println content
			
			def userC = new UserController()
			userC.userService = userService
			
			userC.request.contentType = "application/json"
			userC.request.content = content.getBytes()
			
		when:
			userC.passwordReset()
		
		then:
			userC.response.status == 400
			userC.response.json.error == "no user found with email " + invalidEmail
			userC.response.json.code == 205 
	}

	/**
	 *  Integration test on getUserProfile() by creating a dummy user.
	 *  
	 *  <p>
	 *  Note that the level and levelName were hardcoded as "1" and "novice" so no test conditions were written until further implementation.
	 *  Also, the testing condition on date is not implemented because the system time may be different from Parse server time.
	 *  </p>
	 *  
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserProfile_Success" () {
		setup:
			def Date1 = new Date()
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
			def createdAt = helperService.parseDateFromStringT(userC.response.json.createdAt.toString())
			
			userC.response.reset()
			
		when:
			userC.params.userId = userId
			userC.getUserProfile()
			//def updatedAt = helperService.parseDateFromStringT(userC.response.json.updatedAt.toString())
			//println "Response after calling getUserProfile(): " + userC.response.json
			//println "Response of updatedAt after calling getUserProfile(): " + updatedAt
			
			
		then:
			userC.response.status == 200
			userC.response.json.username == username
			userC.response.json.currentBalance == Account.findByUserId(userId).currentBalance
			userC.response.json.userId == userId
			userC.response.json.gender == gender
			userC.response.json.region == region
			userC.response.json.email == email
			userC.response.json.userStats == 
				[weekly:[leagues:[:], ties:0, losses:0, netGain:0, netGainPercent:0, wins:0],
					 monthly:[leagues:[:], ties:0, losses:0, netGain:0, netGainPercent:0, wins:0],
					 all:[leagues:[:], ties:0, losses:0, netGain:0, netGainPercent:0, wins:0]]
			
			//The testing condition below will be written after further implementation
			//userC.response.json.level ==
			//userC.response.json.levelName == 
		
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
			
	}
	
	/**
	 *  Integration test on getUserProfile() by not setting any userId in the params.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserProfile_Failure_RequestFoundNoUserId" () {
		setup:
			def userC = new UserController()
			userC.userService = userService

		when:
			userC.getUserProfile()
		
		then:
			userC.response.status == 404
			userC.response.json.error == "invalid parameters"
	}
	
	/**
	 *  Integration test on getUserProfile() by giving an invalid userId as the param. 
	 *  <p>
	 *  This is the first case that will fail when retrieving user profile from Parse.
	 *  </p>
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserProfile_Failure_UserAccountDoesNotExist_Parse" () {
		setup:
			def userC = new UserController()
			userC.userService = userService
			def invalidUserId = "ThisIdShouldNotExist"
			
		when:
			userC.params.userId = invalidUserId
			userC.getUserProfile()
			
		then:
			userC.response.status == 400
			userC.response.json.error == "user account does not exist"
			userC.response.json.code == 500		
	}
	
	/**
	 *  Integration test on getUserProfile() by only creating a dummy user on Parse but not on the MySQL database.
	 *  <p>
	 *  This is the second case that will fail when retrieving user profile from MySQL database.
	 *  </p>
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getUserProfile_Failure_UserAccountDoesNotExist_MySQL" () {
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
			
			def rest = new RestBuilder()
			def resp = parseService.createUser(rest, username.toLowerCase(), email, password, gender, region)
			
			def userId = resp.json.objectId //This userId should exist in Parse, but not in the MySQL database.
			def sessionToken = resp.json.sessionToken
		
		when:
			userC.params.userId = userId
			userC.getUserProfile()
		
		then:
			userC.response.status == 400
			userC.response.json.error == "user account does not exist"
			userC.response.json.code == 500
		
		cleanup:
			println "cleanup"
			def resp2 = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp2.json
	}
	
	/**
	 *  Integration test on getCoins() by creating a dummy user, setting the current balance (2000)
	 *  below the FREE_COIN_BALANCE_THRESHOLD (50).
	 *  
	 *  <p> Note that this test sets the current balance less the
	 *   	FREE_COIN_BALANCE_THRESHOLD (50) directly in the Account Domain and adds FREE_COIN_AMOUNT (1000)
	 *   	to the current balance.
	 * 	 	This test should be rewritten once encapsulation is implemented. 
	 *	</p>
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getCoins_Success" () {
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
			userC.params.userId = userId
			Account.findByUserId(userId).currentBalance = userC.userService.FREE_COIN_BALANCE_THRESHOLD - 1 //setting the current balance to be 1 less than the threshold
			def currentBalance = Account.findByUserId(userId).currentBalance
			userC.getCoins()
			
		then:
			userC.response.status == 200
			userC.response.json.username == username
			userC.response.json.userId == userId
			userC.response.json.currentBalance == currentBalance + userC.userService.FREE_COIN_AMOUNT
			userC.response.json.newCoinsAmount == userC.userService.FREE_COIN_AMOUNT
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on getCoins() by not setting any userId in the params.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getCoins_Failure_RequestFoundNoUserId" () {
		setup:
			def userC = new UserController()
			userC.userService = userService

		when:
			userC.getCoins()
		
		then:
			userC.response.status == 404
			userC.response.json.error == "userId is required"
	}
	
	/**
	 *  Integration test on getCoins() by giving an invalid userId as the param.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getCoins_Failure_UserAccountDoesNotExist" () {
		setup:
			def userC = new UserController()
			userC.userService = userService
			def invalidUserId = "ThisIdShouldNotExist"
		
		when:
			userC.params.userId = invalidUserId
			userC.getCoins()
			
		then:
			userC.response.status == 404
			userC.response.json.error == "userId is invalid"
			userC.response.json.code == 400
	}
	
	/**
	 *  Integration test on getCoins() by creating a dummy user whose current balance (2000) is above the 
	 *  FREE_COIN_BALANCE_THRESHOLD (50).
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getCoins_Failure_AboveFreeCoinThreshold" () {
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
			userC.params.userId = userId
			userC.getCoins()
			
		then:
			userC.response.status == 404
			userC.response.json.code == 400
			userC.response.json.error == "Balance above "+userC.userService.FREE_COIN_BALANCE_THRESHOLD+" cannot get free coins"
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on getRanking() by creating a dummy user.
	 *  
	 *  <p> Note: This test case is only temporarily due to known issues. 
	 *  If the user does not have any processed transactions, it should still return user's ranking.
	 *  This test should be rewritten after the issuse(s) are fixed and/or after encapsulation is implemented.
	 *  </p>
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getRanking_Success" () {
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def gender = "male"
			def region = "Toronto"
			def content = '{"username":"'+username+'","password":"'+password+'","email":"'+email+'", "gender":"'+gender+'", "region":"'+region+'"}'
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
			userC.params.userId = userId
			userC.getRanking()
			println userC.response.json
		
		then:
			userC.response.status == 200
			userC.response.json.weekly != null
			userC.response.json.all != null
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), sessionToken, userId)
			println resp.json
	}
	
	/**
	 *  Integration test on getRanking() by not setting any userId in the params.
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getRanking_Failure_RequestFoundNoUserId" () {
		setup:
			def userC = new UserController()
			userC.userService = userService

		when:
			userC.getRanking()
		
		then:
			userC.response.status == 404
			userC.response.json.error == "userId is required"
	}
	
	/**
	 *  Integration test on getRanking() by setting the param userId to be empty string. 
	 *
	 *  @author Chih-Hong (James) Pang
	 *  @version 1.0
	 */
	void "getRanking_Failure_userIdIsEmptyString" () {
		setup:
			def userC = new UserController()
			userC.userService = userService

		when:
			userC.params.userId = ""
			userC.getRanking()
		
		then:
			userC.response.status == 404
			userC.response.json.error == "userId is required"
	}
	
}
