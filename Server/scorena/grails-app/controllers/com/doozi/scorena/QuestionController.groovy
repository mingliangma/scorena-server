package com.doozi.scorena

import grails.converters.JSON


// /v1/sports/soccer/premier/questions
//GET	-gameID



class QuestionController {
	
	def questionService
	def userService
	
    def listQuestions() { 
		
		if ( !params.gameId){
			response.status = 404
			def result = [error: "invalid game ID parameter"]
			render result as JSON
			return
		}
		def questions = ""
		if (params.userId && params.userId!=null && userService.accountExists(params.userId)){			
			
			questions = questionService.listQuestionsWithPoolInfo(params.gameId, params.userId)
		}else{
			questions = questionService.listQuestionsWithPoolInfo(params.gameId)
		
		}
		render questions as JSON
	}
	
	def getQuestionDetails(){
		
		if (!params.qId){
			response.status = 404
			def result = [error: "invalid question ID parameter"]
			render result as JSON
			return
		}
		def questionDetails = ""
		if (params.userId && userService.accountExists(params.userId)){			
			questionDetails = questionService.getQuestion(params.qId, params.userId)
		}else{
			questionDetails = questionService.getQuestion(params.qId)
		
		}
		render questionDetails as JSON
	}
	
	def createQuestions(){
		def questionCreationResult = questionService.createQuestions()
		Map result = [questionCreated: questionCreationResult]
		render result as JSON
	}
}
