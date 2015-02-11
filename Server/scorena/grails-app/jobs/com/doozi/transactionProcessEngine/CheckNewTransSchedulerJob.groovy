package com.doozi.transactionProcessEngine
import com.doozi.scorena.*


class CheckNewTransSchedulerJob {
    def processEngineManagerService
	static triggers = {
	  simple name: 'newGameResultTrigger', startDelay: 75 * 60000, repeatInterval: 74*60*1000 // execute job once in 30 minutes
	}

	def execute() {	
		println "new game result trigged at " + new Date()		
//		def result = processEngineManagerService.startProcessEngine()
		println "processEngineManager result:"+result 
	}
}
