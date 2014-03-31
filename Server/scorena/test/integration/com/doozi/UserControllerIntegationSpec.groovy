package com.doozi



import com.doozi.scorena.Account;
import com.doozi.scorena.UserController;

import grails.plugins.rest.client.RestBuilder
import spock.lang.*

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

    void "signupUser" () {
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
			userC.request.contentType = "text/json"
			userC.request.content = content.getBytes()
						
			userC.createNewUser()
			
		then:
			println "then"
			userC.response.status == 201
			userC.response.json.username == username
			userC.response.json.objectId != null
			userC.response.json.sessionToken != null			
			
			def account = Account.findByUserId(userC.response.json.objectId)
			account != null
			account.userId == userC.response.json.objectId
			account.username == username
			account.currentBalance == 2000
			account.previousBalance == 2000
			
			def respRetreive = parseService.retreiveUser(new RestBuilder(), userC.response.json.objectId)
			respRetreive.status == 200
			respRetreive.json.gender == "male"
			respRetreive.json.region == "Toronto"
			
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), userC.response.json.sessionToken,userC.response.json.objectId )
			println resp.json
    }
	
	void "log in user"(){
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def content = '{"username":"'+username+'","password":"'+password+'","email":"'+email+'"}'
			println content
			
			def userC = new UserController()
			userC.userService = userService
			userC.request.contentType = "text/json"
			userC.request.content = content.getBytes()						
			userC.createNewUser()
		
			def objectId = userC.response.json.objectId
			def sessionToken = userC.response.json.sessionToken
			
		when: 		
			userC.params.username = username.encodeAsURL()
			userC.params.password = password.encodeAsURL()
			userC.login()
			
		then:
			userC.response.status == 201
			userC.response.json.username == username
			userC.response.json.objectId != null
			userC.response.json.sessionToken != null
			userC.response.json.currentBalance == 2000
			println userC.response.json
		
		cleanup:
			println "cleanup"
			def resp = parseService.deleteUser(new RestBuilder(), userC.response.json.sessionToken,userC.response.json.objectId )
			println resp.json
	}
	
	void "deleteUser" (){
		setup:
			Random random = new Random()
			def ranNum = random.nextInt(10000).toString()
			def username = "testingname"+ranNum
			def email = "testingname"+ranNum+"@gmail.com"
			def password = "11111111"
			def content = '{"username":"'+username+'","password":"'+password+'","email":"'+email+'"}'
			println content
			
			def createUserResp = userService.createUser(username, email, password)
			
			def objectId = createUserResp.objectId
			def sessionToken = createUserResp.sessionToken
			println "objectId: "+objectId
			
			def userC = new UserController()
			
			
		when:
			
			userC.params.sessionToken = sessionToken
			userC.params.userId = objectId
			userC.delete()
			
		then:
			println userC.response.json
			userC.response.status == 200
			userC.response.json == [:]
			
			
			def resp = parseService.retreiveUser(new RestBuilder(), objectId)
			resp.status == 404
			resp.json.code == 101
			
			def account = Account.findByUserId(objectId)
			account == null
	}
}