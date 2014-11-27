package com.doozi.scorena.admin

class SimulateBetController {
	def simulateBetService
	def simulateCommentService
    def simulateBet() { 		
		simulateBetService.simulateBetUpcoming()
	}
	
	def simulateComment(){
		simulateCommentService.simulateComment()
	}
}
