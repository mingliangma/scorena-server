package com.doozi.transactionProcessEngine



class CheckNewTransSchedulerJob {
    def processEngineManagerService
	static triggers = {
	  simple name: 'newGameResultTrigger', repeatInterval: 20*60*1000 // execute job once in 20 minutes
	}

	def execute() {		
		def result = processEngineManagerService.startProcessEngine()
		println "processEngineManager result:"+result
	}
}
