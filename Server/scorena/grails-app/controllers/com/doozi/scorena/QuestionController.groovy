package com.doozi.scorena

import grails.converters.JSON


// /v1/sports/soccer/premier/questions
//GET	-gameID



class QuestionController {
	
	def questionService
	def userService
	
    def listQuestions() { 
		log.info "listQuestions(): begins..."
		
		if ( !params.gameId){
			response.status = 404
			def result = [error: "invalid game ID parameter"]
			render result as JSON
			log.error "listQuestions(): result = ${result}"
			return
		}
		def questions = ""
		if (params.userId && params.userId!=null && userService.accountExists(params.userId)){			
			
			questions = questionService.listQuestionsWithPoolInfo(params.gameId, params.userId)
		}else{
			questions = questionService.listQuestionsWithPoolInfo(params.gameId)
		
		}
		render questions as JSON
		log.info "listQuestions(): ends with questions = ${questions}"
	}
	
	def getQuestionDetails(){
		log.info "getQuestionDetails(): begins..."
		
		if (!params.qId){
			response.status = 404
			def result = [error: "invalid question ID parameter"]
			render result as JSON
			log.error "getQuestionDetails(): result = ${result}"
			return
		}
		def questionDetails = ""
		if (params.userId && userService.accountExists(params.userId)){			
			questionDetails = questionService.getQuestion(params.qId, params.userId)
		}else{
			questionDetails = questionService.getQuestion(params.qId)
		
		}
		render questionDetails as JSON
		log.info "getQuestionDetails(): ends with questionDetails = ${questionDetails}"
	}
	
	def createQuestions(){
		log.info "createQuestions(): begins..."
		
		def questionCreationResult = questionService.createQuestions()
		Map result = [questionCreated: questionCreationResult]
		render result as JSON
		log.info "createQuestions(): end"
	}
}
