package com.doozi.scorena.admin

class SimulateBetController {
	def simulateBetService
    def simulateBet() { 		
		simulateBetService.simulateBetUpcoming()
	}
}
