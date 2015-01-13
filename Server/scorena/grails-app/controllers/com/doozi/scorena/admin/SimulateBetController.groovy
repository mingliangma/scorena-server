package com.doozi.scorena.admin

import com.doozi.scorena.*

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder

class SimulateBetController {
	def simulateBetService
	def simulateCommentService
	def payoutTansactionService
    def simulateBet() { 		
		int betCounter = simulateBetService.simulateBetUpcoming()
		def result = [numberBets: betCounter]
		render result as JSON
	}
	
	def simulateComment(){
		simulateCommentService.simulateComment()
	}
	
	def handleException(Exception e) {
		render e.toString()
		log.info "${e.toString()}"
	}
}
