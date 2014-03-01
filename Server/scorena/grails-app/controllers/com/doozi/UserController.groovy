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
	
	def createNewUser(){
		System.out.println("create new user")
		log.info "enter override create()"
		def user = new User(email:"mingliang.ma@gmail.com", displayName: "mingma", password:"232", FID:"asdf")
		
//		user.account = new Account()
		
		if (user.save()){
			System.out.println("user successfully saved")
			render user as JSON
		}else{
			System.out.println("user save failed")
		
			render status:404
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