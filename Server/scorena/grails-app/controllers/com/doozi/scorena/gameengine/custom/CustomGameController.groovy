package com.doozi.scorena.gameengine.custom

import grails.converters.JSON

class CustomGameController {
		def customGameService
		
	def createCustomGame() { 
		
		if ( !request.JSON.awayTeamName ||!request.JSON.homeTeamName || !request.JSON.startDateTimeInput){
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
		
		def result = customGameService.createCustomGameByName(request.JSON.awayTeamName, request.JSON.homeTeamName, request.JSON.eventName,
			 request.JSON.startDateTimeInput)
		
		if (result.error){
			println "failed with message: "+result.get("error")
			response.status = 404
			render result as JSON
			return
		}
		println "success"
		response.status = 201
		render result as JSON
		return
		
	}
}
