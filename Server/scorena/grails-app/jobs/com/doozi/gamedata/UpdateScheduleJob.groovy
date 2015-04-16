package com.doozi.gamedata



class UpdateScheduleJob {
	def gameDataDbInputStatsNbaService
	
    static triggers = {
		cron name:'updateScheduleTimeTrigger', startDelay:5000, cronExpression: '0 3 * * * ?' //repease every 5 minutes on 1am, 2am, 3am, 4am, 11pm UTC time
    }

    def execute() {
		println "UpdateScheduleJob trigged at " + new Date()
		gameDataDbInputStatsNbaService.updateSchedule()
		println "UpdateScheduleJob completed"
    }
}
