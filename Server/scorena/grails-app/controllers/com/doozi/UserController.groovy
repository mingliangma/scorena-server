package com.doozi

import grails.converters.JSON


// v1/sports/userprofile/{UserType or Status}/events

//eg. v1/sports/userprofile/newuser 
//eg. v1/sports/userprofile/existinguser
//eg. v1/sports/userprofile/superuser

class UserController {
	
	def list(){
		def user="this is testing user"
		render user as JSON
	}

//    def index() {}
	def userService
	def createNewUser(){

		def resp = userService.createUser(request.JSON.username, request.JSON.email, request.JSON.password)
		println resp
		if (resp.code==201){
			response.status = 201
			render json: resp as JSON
		}else{
			response.status =400
			render json: resp as JSON
		}		
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