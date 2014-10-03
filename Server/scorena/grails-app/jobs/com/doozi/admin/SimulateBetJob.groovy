package com.doozi.admin
import com.doozi.scorena.*

class SimulateBetJob {
	def simulateBetService
	static triggers = {
	  simple name: 'simulateBetTrigger', repeatInterval: 20*60*1000 // execute job once in 15 minutes
	}

	def execute() {
		println "simulateBetUpcoming trigged at " + new Date()
		def result = simulateBetService.simulateBetUpcoming()
		println "simulateBetUpcoming completed"
	}
}
