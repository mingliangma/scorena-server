package com.doozi.scorena.gameengine.custom

import grails.async.Promise
import static grails.async.Promises.*

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
		
		println "result.questionId="+result.questionContentId
		
		if (!result.questionContentId){
			println "failed with message: "+result.get("error")
			response.status = 404
			render result as JSON
			log.error "createCustomQuestion(): result = ${result}"
			return
		}		
		customQuestionService.simulateBetCustomQuestion(request.JSON.eventId.toString(), result.questionContentId)
//		Promise p = task {
//			customQuestionService.simulateBetCustomQuestion(request.JSON.eventId.toString(), result.questionContentId)
//		}
//		p.onComplete { cresult ->
//			println "simulateBetCustomQuestion completed and returned $cresult"
//		}
		
		
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
		response.status = 500
		render e.toString()
		log.error "${e.toString()}", e
	}
}
