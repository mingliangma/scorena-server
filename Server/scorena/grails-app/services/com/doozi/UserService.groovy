package com.doozi

import grails.transaction.Transactional

@Transactional
class UserService {

	def createNewUser(String name){
		User.create()
	}
	def serviceMethod() {

    }
}
