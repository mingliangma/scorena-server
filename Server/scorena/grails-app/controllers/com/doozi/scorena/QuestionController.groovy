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
		if (params.userId){
			
			if (!userService.accountExists(params.userId)){
				response.status = 404
				def errorMap = [code: 102, error: "User Id does not exists"]
				render errorMap as JSON
				return
			}
			
			questions = questionService.listQuestionsWithPoolInfo(params.gameId, params.userId)
			render questions as JSON
		}else{
			questions = questionService.listQuestionsWithPoolInfo(params.gameId)
		
		}
		render questions as JSON
	}
	
	def getQuestionDetails(){
		
		if (!params.gameId){
			response.status = 404
			def result = [error: "invalid game ID parameter"]
			render result as JSON
			return
		}
		
		if (params.qId){
			def questionDetails = questionService.getQuestion( params.qId, params.userId)
			render questionDetails as JSON
		}
	}
	
	def createQuestions(){
		def questionCreationResult = questionService.createQuestions()
		render questionCreationResult as JSON
	}
}
