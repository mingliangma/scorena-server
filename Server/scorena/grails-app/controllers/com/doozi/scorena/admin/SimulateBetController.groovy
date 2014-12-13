package com.doozi.scorena.admin

import com.doozi.scorena.*
import grails.plugins.rest.client.RestBuilder

class SimulateBetController {
	def simulateBetService
	def simulateCommentService
	def payoutTansactionService
    def simulateBet() { 		
		simulateBetService.simulateBetUpcoming()
	}
	
	def simulateComment(){
		simulateCommentService.simulateComment()
	}
	
	def pay(){
		def rest = new RestBuilder()
		payoutTansactionService.createPayoutTrans(Account.findByUserId("0sZBOz3D6W"), Question.get(1), 1020, 2, 100, 2, 1, new Date())
	}
}
