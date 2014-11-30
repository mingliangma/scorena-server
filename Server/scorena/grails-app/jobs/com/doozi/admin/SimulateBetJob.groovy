package com.doozi.admin
import com.doozi.scorena.*

class SimulateBetJob {
	def simulateBetService
	def simulateCommentService
	static triggers = {
	  simple name: 'simulateBetTrigger', repeatInterval: 20*60*1000 // execute job once in 15 minutes
	}

	def execute() {
		println "simulateBetUpcoming trigged at " + new Date()
		def result = simulateBetService.simulateBetUpcoming()
		println "simulateBetUpcoming completed"
		
		println "simulateComment trigged at " + new Date()
		simulateCommentService.simulateComment()
		println "simulateComment completed"
	}
}
