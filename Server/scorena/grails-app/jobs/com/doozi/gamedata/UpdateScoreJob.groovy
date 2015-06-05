package com.doozi.gamedata



class UpdateScoreJob {
	def gameDataDbInputStatsNbaService
	def gameDataDbInputMlbService
    static triggers = {
		cron name:'busyTimeTrigger', startDelay:60000, cronExpression: '0 0/5 0,1,2,3,4,22,23 * * ?' //repease every 5 minutes on 1am, 2am, 3am, 4am, 11pm UTC time
		simple name: 'nonBusyTimeTrigger', startDelay: 90000, repeatInterval: 20*60*1000 //repeat every 20 minutes
	  
    }

    def execute() {
		if (grails.util.Environment.current == grails.util.Environment.PRODUCTION){
			println "UpdateScoreJob trigged at " + new Date()
			gameDataDbInputStatsNbaService.updateScore()
			println "UpdateScoreJob completed"
		}else{
			println "update score job cancelled. Server environment is not production. at " + new Date()
		}
		gameDataDbInputMlbService.updateScore()
    }
}
