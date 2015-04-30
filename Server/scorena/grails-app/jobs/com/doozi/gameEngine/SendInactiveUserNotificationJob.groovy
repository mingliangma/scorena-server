package scorena



class SendInactiveUserNotificationJob {
    def pushService
	
    static triggers = {
		cron name:'sendInactiveUserNotificationTrigger', startDelay:50000, cronExpression: '0 21 * * * ?' //every day at 9pm utc
    }

    def execute() {
		
		if (grails.util.Environment.current == grails.util.Environment.PRODUCTION){
			println "UpdateScheduleJob trigged at " + new Date()
			pushService.inactiveUsersReminder()
			println "UpdateScheduleJob completed"
		}else{
			println "update schedule job cancelled. Server environment is not production. at " + new Date()
		}
    }
}
