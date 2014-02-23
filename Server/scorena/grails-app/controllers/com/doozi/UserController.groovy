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
	
//	def save(){
//		log.info "enter override create()"
//		def paramTemp = params
//		def acc = new Account(0,0)
//		def user = new User(params)
//		if (user.save()){
//			log.info "user successfully saved"
//			render user as JSON
//		}else{
//		log.info "user save failed"
//			render status:404
//		}
//	}
	
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