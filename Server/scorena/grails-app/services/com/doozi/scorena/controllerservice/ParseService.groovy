package com.doozi.scorena.controllerservice

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
	
	def createUser(def rest, String usernameInput, String emailInput, String passwordInput, String genderInput, String regionInput, String displayNameInput){
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.post("https://api.parse.com/1/users"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				username=usernameInput
				password=passwordInput
				email=emailInput
				gender=genderInput
				region=regionInput
				display_name=displayNameInput
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
	
	def retreiveUser(def rest, def objectId){
		def parseConfig = grailsApplication.config.parse
		def resp = rest.get("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
		}
		return resp
	}	
	
	def updateUser(def rest, def sessionToken, def objectId, def updateUserDataJSON){
		def parseConfig = grailsApplication.config.parse		
		def resp = rest.put("https://api.parse.com/1/users/"+objectId){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header	"X-Parse-Session-Token", sessionToken
			contentType "application/json"
			json updateUserDataJSON
		}
		return resp
	}
	
	def passwordReset(def rest, def userEmail){
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/requestPasswordReset"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				email=userEmail
			}
		}
		return resp
	}
	
	def uploadImage(def rest, def image, def imageName){
		def parseConfig = grailsApplication.config.parse
		def resp = rest.post("https://api.parse.com/1/files/"+imageName){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			header  "Content-Type: image/jpeg"
			body image
		}
		return resp
	}
	
	def associateImageWithUser(def rest, def imageName){
		def parseConfig = grailsApplication.config.parse
		def picture = ["name": imageName, "__type":"File"]
		def resp = rest.post("https://api.parse.com/1/classes/user"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
			header	"X-Parse-REST-API-Key", parseConfig.parseRestApiKey
			contentType "application/json"
			json {
				profilePictureMedium= picture				
			}
		}
		return resp
	}
	
	
	
}
