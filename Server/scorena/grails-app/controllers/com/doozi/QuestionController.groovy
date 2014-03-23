package com.doozi

import grails.converters.JSON


// /v1/sports/soccer/premier/questions
//GET	-gameID



class QuestionController {
	
	def questionService
    def listQuestions() { 
		if (params.gameId){
			def questions = questionService.listQuestions(params.gameId)
			render json:questions as JSON
		}
	}
	
	def getQuestionDetails(){
		if (params.gameId && params.qId){
			def questionDetails = questionService.getQuestion( params.qId)
			render json:questionDetails as JSON
		}
	}
}
