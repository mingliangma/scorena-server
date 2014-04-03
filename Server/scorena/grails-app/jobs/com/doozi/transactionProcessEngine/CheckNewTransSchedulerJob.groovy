package com.doozi.transactionProcessEngine



class CheckNewTransSchedulerJob {
    def newGameResultFetcherService
	static triggers = {
	  simple name: 'newGameResultTrigger', repeatInterval: 10000 // execute job once in 5 seconds
	}

	def execute() {		
		newGameResultFetcherService.printNowTime(new Date())		
	}
}
