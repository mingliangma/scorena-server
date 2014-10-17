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
		
		//validate all required input parameters exist
		Map validateResult = validateWriteCommentRequest(request)
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
		
		commentsList=commentService.writeComments(request.JSON.userId,request.JSON.message,request.JSON.qId)
		commentsMap=[comments:commentsList]
		
		render commentsMap as JSON
	}
	
	private Map validateWriteCommentRequest(def request){
		if (!request.JSON.userId||!request.JSON.qId|| !request.JSON.message || !request.JSON.sessionToken){
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
