package com.doozi

import grails.converters.JSON


// /v1/sports/soccer/premier/questions
//GET	-gameID



class QuestionController {
	
	def questionService
    def getQuestions() { 
		if (params.gameId){
			def questions = questionService.listQuestions(params.gameId)
			render questions as JSON
		}
	}
}
