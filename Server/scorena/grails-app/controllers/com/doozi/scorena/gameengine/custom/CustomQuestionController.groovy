package com.doozi.scorena.gameengine.custom

import grails.converters.JSON

class CustomQuestionController {

	def customQuestionService
	def customQuestionResultService
    def createCustomQuestion() { 
		
		if (!request.JSON.qContent ||!request.JSON.pick1 ||!request.JSON.pick2 || !request.JSON.eventId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		if (!request.JSON.username || request.JSON.username!="scorenaadmin"){
			response.status = 404
			def result = [error: "username is incorrect"]
			render result as JSON
			return
		}
		
		if (!request.JSON.password || request.JSON.password!="mz8785"){
			response.status = 404
			def result = [error: "password is incorrect"]
			render result as JSON
			return
		}
		
		def result = customQuestionService.createCustomQuestion(request.JSON.eventId.toString(), request.JSON.qContent.toString(),
			 request.JSON.pick1.toString() , request.JSON.pick2.toString())
		
		if (result!=[:]){
			println "failed with message: "+result.get("error")
			response.status = 404
			render result as JSON
			return
		}
		println "success"
		response.status = 201
		render [:] as JSON
		return
		
	}
	
	def createCustomQuestionResult(){
		
		if ( !request.JSON.winnerPick || !request.JSON.questionId ||!request.JSON.eventId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			return
		}
		
		if (!request.JSON.username || request.JSON.username!="scorenaadmin"){
			response.status = 404
			def result = [error: "username is incorrect"]
			render result as JSON
			return
		}
		
		if (!request.JSON.password || request.JSON.password!="mz8785"){
			response.status = 404
			def result = [error: "password is incorrect"]
			render result as JSON
			return
		}
		def result = customQuestionResultService.addCustomQuestionResult(request.JSON.winnerPick.toInteger(), request.JSON.questionId.toInteger(), request.JSON.eventId)
		
		if (result.error){
			response.status = 404
			render result as JSON
			return
		}
		
		render result as JSON
		return
	}
}
