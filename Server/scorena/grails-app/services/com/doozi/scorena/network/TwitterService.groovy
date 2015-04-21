package com.doozi.scorena.network

import org.springframework.transaction.annotation.Transactional

class TwitterService {
	def grailsApplication
    def usersShow(def rest, def sessionToken) {
		log.debug "validateSession(): begins with rest = ${rest}, sessionToken = ${sessionToken}"
		
		def parseConfig = grailsApplication.config.parse
		
		def resp = rest.get("https://api.parse.com/1/users/me"){
			header 	"X-Parse-Application-Id", parseConfig.parseApplicationId
		}
		
		log.debug "validateSession(): ends with resp = ${resp}"
		
		return resp
    }
}
