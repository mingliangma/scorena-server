package com.doozi.scorena

import grails.converters.JSON


// v1/sports/userprofile/{UserType or Status}/events

//eg. v1/sports/userprofile/newuser 
//eg. v1/sports/userprofile/existinguser
//eg. v1/sports/userprofile/superuser


//curl -i -X POST -H "Content-Type: application/json" -d '{"username":"mastermml8", "email":"masterml8@gmail.com", "password":"lkffd"}' localhost:8080/scorena/users/new

//curl -v -X GET  -G --data-urlencode 'username=michealLiu' --data-urlencode 'password=11111111' localhost:8080/scorena/v1/login

class UserController {
	def userService
	
	def list(){
		def user="this is testing user"
		render user as JSON
	}

	
	//curl -i -v -X POST -H "Content-Type: application/json" -d '{"username":"candiceli", "email":"candi@gmail.com", "password":"asdfasdf", "gender":"female", "region":"Toronto"}' localhost:8080/scorena/v1/users/new
	def createNewUser(){
		
		println request.JSON
		if (!request.JSON.username || !request.JSON.email|| !request.JSON.password || !request.JSON.gender|| !request.JSON.region){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		//def image = request.getFile('profilePicture')
		
		def resp = userService.createUser(request.JSON.username, request.JSON.email, request.JSON.password, request.JSON.gender, request.JSON.region)
		
		if (resp.code){
			response.status =400
			render resp as JSON
			return
		}
		
		
			response.status = 201
			render resp as JSON
		
	}
	
	//curl -v -X GET  -G --data-urlencode 'username=candiceli' --data-urlencode 'password=asdfasdf' localhost:8080/scorena/v1/login
	def login(){
		println request.JSON.toString()
		if (!params.username||!params.password){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		def resp = userService.login(params.username.decodeURL(), params.password.decodeURL())
		if (resp.code){
			response.status =400
			render resp as JSON
			return
		}
		render resp as JSON
	}
	
	def deleteUserProfile(){
		String sessionToken = request.getHeader("sessionToken")
		
		if (!sessionToken||!params.userId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		def resp = userService.deleteUser(sessionToken, params.userId.toString())
		if (resp.code){
			response.status =400
			render resp as JSON
			return
		}
		render resp as JSON
	}
	
	
	//curl -v -X GET localhost:8080/scorena/v1/users/R3yN1lprBc
	def getUserProfile(){
		if (!params.userId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		def resp = userService.getUserProfile(params.userId.toString())
		if (resp.code){
			response.status =400
			render resp as JSON
			return
		}
		render resp as JSON
	}
	
	
	//curl -i -v -X PUT -H "sessionToken:jqxy6cutose3wemz1ko5731hg"  -H "Content-Type: application/json" -d '{"email":"ming@gmail.com"}' localhost:8080/scorena/v1/users/tSftavHQP6
	def updateUserProfile(){
		
		String sessionToken = request.getHeader("sessionToken")

		
		if (!sessionToken||!params.userId || !request.JSON){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		if (request.JSON.username){
			response.status = 404
			def result = [error: "username cannot be updated"]
			render result as JSON
			return
		}
		
		def resp = userService.updateUserProfile(sessionToken, params.userId.toString(), request.JSON as JSON)
		
		if (resp.code){
			response.status =400
			render resp as JSON
			return
		}
		
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