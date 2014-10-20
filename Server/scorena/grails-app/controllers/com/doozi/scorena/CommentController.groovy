package com.doozi.scorena

import java.util.Map;

import grails.converters.JSON

class CommentController {

    def commentService
	def userService
	
	def index() { }
	
	def getExistingComments(){
		def commentsList
		def commentsMap
		
		/*if (params.qId){
			commentsList=commentService.getExistingComments(params.qId)
		}*/
		
		commentsList=commentService.getExistingComments(params.qId)
		commentsMap=[comments:commentsList]
		
		render commentsMap as JSON
	}
	
	def writeComments(){
		def commentsList
		def commentsMap
		
		String qId = params.qId
		String gameId = params.gameId
		
		//validate all required input parameters exist
		Map validateResult = validateWriteCommentRequest(request, qId, gameId)
		if (validateResult != [:]){
			response.status = 404
			render validateResult as JSON
			return
		}
		
		//validate userId exist on Parse
		def validation = userService.validateSession(request.JSON.sessionToken)
		if (validation.code){
			response.status = 404
			render validation as JSON
			return
		}
		String userId = validation.objectId
		commentsList=commentService.writeComments(userId, request.JSON.message, qId)
		commentsMap=[comments:commentsList]
		
		render commentsMap as JSON
	}
	
	private Map validateWriteCommentRequest(def request, qId, gameId){
		if (!qId || !gameId || !request.JSON.message || !request.JSON.sessionToken){
			response.status =404
			def resp = [
				code: 101,
				error: "invalid parameters"
				]
			return resp
		}
		return [:]
	}

}
