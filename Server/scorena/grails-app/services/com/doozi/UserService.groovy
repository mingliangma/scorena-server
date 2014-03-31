package com.doozi

import grails.transaction.Transactional


import grails.converters.JSON
import grails.web.JSONBuilder
import groovy.util.slurpersupport.GPathResult

import org.codehaus.groovy.grails.web.json.JSONArray
import org.codehaus.groovy.grails.web.json.JSONObject
import org.codehaus.groovy.grails.web.servlet.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import grails.plugins.rest.client.RestBuilder

@Transactional
class UserService {
	def INITIAL_BALANCE = 2000
	def PREVIOUS_BALANCE = INITIAL_BALANCE
	def grailsApplication
	def parseService
			
	def createUser(String _username, String _email, String _password, String gender, String region, def image, String imageName){
		def rest = new RestBuilder()
		def resp = parseService.createUser(rest, _username, _email, _password, gender, region)
		
		println "parse create user status:"+resp.status
		println "parse create user content:"+resp.json
		
		if (resp.status != 201){
			def result = [
				code:resp.json.code,
				error:resp.json.error
			]
			println result
			return result
		}
		
		def respUpload = parseService.uploadImage(rest, image, imageName)

		println "user profile created successfully"		
			
		def account = new Account(userId:resp.json.objectId, username:_username, currentBalance:INITIAL_BALANCE,previousBalance:PREVIOUS_BALANCE)
		if (!account.save()){

			def delResp = parseService.deleteUser(rest, resp.json.sessionToken)
			def result = [
				code:202,
				error:"account creation failed"
			]
			return result			
		}
		
		
		println "user profile and account created successfully"
		def result = [			
			createdAt:resp.json.createdAt,
			username:_username,
			currentBalance:INITIAL_BALANCE,
			sessionToken:resp.json.sessionToken,
			objectId: resp.json.objectId,
			createdAt: resp.json.createdAt
		]
		return result		
	}
	
	def login(String username, String password){
		def rest = new RestBuilder()
		def resp = parseService.loginUser(rest, username, password)
		return resp.json
	}
	
	def validateSession (def sessionToken){
		def rest = new RestBuilder()
		def resp = parseService.validateSession(rest, sessionToken)
		if (resp.status == 200){
			return resp.json
		}else if (resp.json.code){
			return resp.json
		}else{
			def result = [
				code:resp.status,
				error:resp.json.error
			]
			return result
		}
	}

	
	def deleteUser(String sessionToken, String userId){
		def rest = new RestBuilder()
		def resp = parseService.deleteUser(rest, sessionToken, userId)
		if (resp.status != (200)){
			println "delete user profile failed: "+resp.json
			return resp.json
		}
		def account = Account.findByUserId(userId)
		
		if (account != null){
			account.delete()
		}
		return [:]
		
	}
}
