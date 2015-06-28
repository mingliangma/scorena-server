package com.doozi.gameEngine



class SendInactiveUserNotificationJob {
    def notificationService
	
    static triggers = {
		cron name:'sendInactiveUserNotificationTrigger', startDelay:50000, cronExpression: '0 0 21 * * ?' //every day at 9pm utc
    }

    def execute() {
		
//		if (grails.util.Environment.current.getName() == "productioncronjobs"){
			println "SendInactiveUserNotificationJob trigged at " + new Date()
			notificationService.inactiveUsersReminder()
			println "SendInactiveUserNotificationJob completed"
//		}else{
//			println "SendInactiveUserNotificationJob cancelled. Server environment is not productioncronjobs. at " + new Date()
//		}
    }
}
