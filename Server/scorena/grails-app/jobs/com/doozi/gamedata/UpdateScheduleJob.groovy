package com.doozi.gamedata



class UpdateScheduleJob {
	def gameDataDbInputStatsNbaService
	def gameDataDbInputMlbService
	
    static triggers = {
		cron name:'updateScheduleTimeTrigger', startDelay:5000, cronExpression: '0 0 7 * * ?' //repease every 5 minutes on 1am, 2am, 3am, 4am, 11pm UTC time
    }

    def execute() {
		
//		if (grails.util.Environment.current.getName() == "productioncronjobs"){
			println "UpdateScheduleJob trigged at " + new Date()
//			gameDataDbInputStatsNbaService.updateSchedule()
			gameDataDbInputMlbService.updateSchedule()
			println "UpdateScheduleJob completed"
//		}else{
//			println "update schedule job cancelled. Server environment is not productioncronjobs. at " + new Date()
//		}
		
    }
}
