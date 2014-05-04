package com.doozi.scorena

import grails.converters.JSON


// /v1/sports/soccer/premier/questions
//GET	-gameID



class QuestionController {
	
	def questionService
    def listQuestions() { 
		println params
		
//		if (!params.userId){
//			response.status = 404
//			def result = [error: "invalid parameters"]
//			render result as JSON
//			return
//		}
		
		if (params.gameId){
			def questions = questionService.listQuestionsWithPoolInfo(params.gameId, params.userId)
			render questions as JSON
		}
	}
	
	def getQuestionDetails(){
		
//		if (!params.userId){
//			response.status = 404
//			def result = [error: "invalid parameters"]
//			render result as JSON
//			return
//		}
		
		if (params.gameId && params.qId){
			def questionDetails = questionService.getQuestion( params.qId, params.userId)
			render questionDetails as JSON
		}
	}
}
