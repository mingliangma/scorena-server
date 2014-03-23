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
	def createUser(String _username, String _email, String _password){
		def rest = new RestBuilder()
		
		def resp = rest.post("https://api.parse.com/1/users"){
			header 	"X-Parse-Application-Id", "sxfzjYsgGiSXVwr7pj6vmaFR2f8ok9YGrnXGfx91"
			header	"X-Parse-REST-API-Key", "IQX6dOlw7KfsLmNw2tau0cGWsE4I3vBliCw67Ca3"
			contentType "application/json"
			json {
				username= _username
				password= _password
				email=_email
			}
		}
		
		if (resp.status == 201){
			println "user profile created successfully"			
			def account = new Account(userId:resp.json.objectId, username:_username, currentBalance:INITIAL_BALANCE,previousBalance:PREVIOUS_BALANCE)
			if (!account.save()){
				def delResp = rest.delete("https://api.parse.com/1/users/"+resp.json.objectId){
					header 	"X-Parse-Application-Id", "sxfzjYsgGiSXVwr7pj6vmaFR2f8ok9YGrnXGfx91"
					header	"X-Parse-REST-API-Key", "IQX6dOlw7KfsLmNw2tau0cGWsE4I3vBliCw67Ca3"
					header	"X-Parse-Session-Token", resp.json.sessionToken
				}
				def result = [
					code:202,
					error:"account creation failed"
				]
				return result			
			}
			def result = [
				code:201,			
				createdAt:resp.json.createdAt,
				username:_username,
				currentBalance:INITIAL_BALANCE,
				sessionToken:resp.json.sessionToken,
			]
			return result
						
		}
		def result = [
			code:resp.json.code,
			error:resp.json.error
		]
		return result
	}
}
