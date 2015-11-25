package com.doozi.gameEngine



class GenerateMonthlyBanner {
	
	def userBannerService
	
   static triggers = {
		cron name:'GenerateMonthlyBanner', cronExpression: '0 0 0 1 * ?' //every month at 12am utc
    }

    def execute() {
			println "Generate Montly Banner job triggered at "+new Date()
	        userBannerService.generatePastMonthBanner()
			println "Generate Monthly Banner job ends"
			
    }
}
