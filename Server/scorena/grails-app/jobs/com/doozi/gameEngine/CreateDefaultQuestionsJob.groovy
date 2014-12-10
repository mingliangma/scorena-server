package com.doozi.gameEngine



class CreateDefaultQuestionsJob {
	
	def questionService
	
    static triggers = {
      simple name: 'createQuestionTrigger', startDelay: 40 * 60000, repeatInterval: 60*60*1000 // execute job once in 60 minutes
    }

    def execute() {
		println "create Quesitons job triggered at "+new Date()
        questionService.createQuestions()
		println "create Quesitons job ends"
    }
}
