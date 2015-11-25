package com.doozi.gameEngine



class CreateDefaultQuestionsJob {
	
	def questionService
	def testService
	
    static triggers = {
      simple name: 'createQuestionTrigger', startDelay: 70 * 60000, repeatInterval: 60*60*1000 // execute job once in 60 minutes
    }

    def execute() {
//		if (grails.util.Environment.current.getName() == "productioncronjobs" || grails.util.Environment.current.getName() == "awsdev"){
//			println "create Quesitons job triggered at "+new Date()
//	        questionService.createQuestions()
//			println "create Quesitons job ends"
//		}else{
//			println "update score job cancelled. Server environment is not productioncronjobs. at " + new Date()
//		}
			
    }
}
