package com.doozi

import grails.converters.JSON


// v1/sports/userprofile/{UserType or Status}/events

//eg. v1/sports/userprofile/newuser 
//eg. v1/sports/userprofile/existinguser
//eg. v1/sports/userprofile/superuser


//curl -i -X POST -H "Content-Type: application/json" -d '{"username":"mastermml8", "email":"masterml8@gmail.com", "password":"lkffd"}' localhost:8080/scorena/users/new

//curl -v -X GET  -G --data-urlencode 'username=michealLiu' --data-urlencode 'password=11111111' localhost:8080/scorena/v1/login

class UserController {
	
	def list(){
		def user="this is testing user"
		render user as JSON
	}

//    def index() {}
	def userService
	def createNewUser(){
		
		def resp = userService.createUser(request.JSON.username, request.JSON.email, request.JSON.password)
		if (!resp.code){
			response.status = 201
			render resp as JSON
		}else{
			response.status =400
			render resp as JSON
		}		
	}
	
	def login(){
		if (!params.username||!params.password){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
		}
		
		def resp = userService.login(params.username.decodeURL(), params.password.decodeURL())
		render resp as JSON
	}
	
	
//	def show(User user){
//		if (user == null){
//			render status:404
//		}else{
//			render user as JSON
//		}
//	}
//	
//	def list(){
//		render User.list() as JSON
//	}
//	def save() {}
//	def update() {}

}