package com.doozi.scorena.gameengine.custom

import grails.converters.JSON

class CustomQuestionController {

	def customQuestionService
	def customQuestionResultService
    def createCustomQuestion() { 
		log.info "createCustomQuestion(): begins..."
		
		if (!request.JSON.qContent ||!request.JSON.pick1 ||!request.JSON.pick2 || !request.JSON.eventId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "createCustomQuestion(): result = ${result}"
			return
		}
		
		if (!request.JSON.username || request.JSON.username!="scorenaadmin"){
			response.status = 404
			def result = [error: "username is incorrect"]
			render result as JSON
			log.error "createCustomQuestion(): result = ${result}"
			return
		}
		
		if (!request.JSON.password || request.JSON.password!="mz8785"){
			response.status = 404
			def result = [error: "password is incorrect"]
			render result as JSON
			log.error "createCustomQuestion(): result = ${result}"
			return
		}
		
		def result = customQuestionService.createCustomQuestion(request.JSON.eventId.toString(), request.JSON.qContent.toString(),
			 request.JSON.pick1.toString() , request.JSON.pick2.toString())
		
		if (result!=[:]){
			println "failed with message: "+result.get("error")
			response.status = 404
			render result as JSON
			log.error "createCustomQuestion(): result = ${result}"
			return
		}
		println "success"
		response.status = 201
		render [:] as JSON
		log.info "createCustomQuestion(): ends"
		return
		
	}
	
	def createCustomQuestionResult(){
		log.info "createCustomQuestionResult(): begins..."

		if ( request.JSON.winnerPick == null || !request.JSON.questionId ||!request.JSON.eventId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "createCustomQuestionResult(): result = ${result}"
			return
		}
		
		if (!request.JSON.username || request.JSON.username!="scorenaadmin"){
			response.status = 404
			def result = [error: "username is incorrect"]
			render result as JSON
			log.error "createCustomQuestionResult(): result = ${result}"
			return
		}
		
		if (!request.JSON.password || request.JSON.password!="mz8785"){
			response.status = 404
			def result = [error: "password is incorrect"]
			render result as JSON
			log.error "createCustomQuestionResult(): result = ${result}"
			return
		}
		def result = customQuestionResultService.addCustomQuestionResult(request.JSON.winnerPick.toInteger(), request.JSON.questionId.toInteger(), request.JSON.eventId)
		
		if (result.error){
			response.status = 404
			render result as JSON
			log.error "createCustomQuestionResult(): result = ${result}"
			return
		}
		
		render result as JSON
		log.info "createCustomQuestionResult(): ends with result = ${result}"
		return
	}
	
	def handleException(Exception e) {
		render e.toString()
		log.info "${e.toString()}"
	}
}
