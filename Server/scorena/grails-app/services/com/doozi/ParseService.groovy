package com.doozi

import grails.transaction.Transactional
import grails.plugins.rest.client.RestBuilder


@Transactional
class ParseService {
	def grailsApplication
    def validateSession(def rest, def sessionToken) {
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/users/me"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
		}
		return resp
    }
	
	def createUser(def rest, String _username, String _email, String _password){
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.post("https://api.parse.com/1/users"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				username= _username
				password= _password
				email=_email
			}
		}
		return resp
	}
	
	def loginUser(def rest, String username, String password){
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/login?username=${username.encodeAsURL()}&password=${password.encodeAsURL()}"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey			
		}
		return resp
	}
	
	
	def deleteUser(def rest, def sessionToken, def objectId){
		println "https://api.parse.com/1/users/"+objectId
		println "X-Parse-Session-Token: "+ sessionToken
		
		def parseConfig = grailsApplication.config.parse
		def resp = rest.delete("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
		}
		return resp
	}
}
