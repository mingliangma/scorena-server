package com.doozi.transactionProcessEngine



class CheckNewTransSchedulerJob {
    def processEngineManagerService
	static triggers = {
	  simple name: 'newGameResultTrigger', repeatInterval: 60*60*1000 // execute job once in 5 seconds
	}

	def execute() {		
		def result = processEngineManagerService.startProcessEngine()
		println "processEngineManager result:"+result
	}
}
