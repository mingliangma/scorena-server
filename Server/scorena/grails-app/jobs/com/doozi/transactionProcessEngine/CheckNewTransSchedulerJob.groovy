package com.doozi.transactionProcessEngine
import com.doozi.scorena.*
import com.doozi.scorena.processengine.*


class CheckNewTransSchedulerJob {
    def processEngineManagerService

	static triggers = {
	  simple name: 'newGameResultTrigger', startDelay: 75 * 60000, repeatInterval: 74*60*1000 // execute job once in 30 minutes
	}

	def execute() {	
		println "new game result trigged at " + new Date()
		boolean isReadyProcess = ProcessStatus.transactionProcessStartRunning("cron job process payout")
		if (isReadyProcess){
			println "process game started"
			def result = processEngineManagerService.startProcessEngine()
			ProcessStatus.transactionProcessStopped()
			println "processEngineManager result:"+result
		}else{
			println "other process is running"
		}				
	}
}
