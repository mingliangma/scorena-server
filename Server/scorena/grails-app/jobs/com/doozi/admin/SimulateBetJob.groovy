package com.doozi.admin
import com.doozi.scorena.*
import com.doozi.scorena.processengine.*

class SimulateBetJob {
	def simulateBetService
	def simulateCommentService
	def test2Service
	static triggers = {
	  simple name: 'simulateBetTrigger', startDelay: 60 * 60000, repeatInterval: 25*60*1000 // execute job once in 15 minutes
	}

	def execute() {
		println "simulateBetUpcoming trigged at " + new Date()
		boolean isReadyProcess = ProcessStatus.transactionProcessStartRunning()
		if (isReadyProcess){
			println "simulate Bet started"
			def result = simulateBetService.simulateBetUpcoming()
			ProcessStatus.transactionProcessStopped()
			println "simulateBetUpcoming completed"
		}else{
			println "other process is running"
		}
		
//		println "simulateComment trigged at " + new Date()
//		simulateCommentService.simulateComment()
//		println "simulateComment completed"
		
		
	}
}
