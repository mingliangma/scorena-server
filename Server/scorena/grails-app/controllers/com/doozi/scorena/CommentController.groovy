package com.doozi.scorena

import java.util.Map;
import grails.converters.JSON

/**
 * @author HDJ
 *
 */
class CommentController {
    def commentService
	def userService
	
	/**
	 * @brief get comments info of the question
	 * @return json of comments list:[body,userId,userName,timeCreated]
	 */
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
	
	/**
	 * @brief add comments to the question
	 * @return json of comments list:[body,userId,userName,timeCreated]
	 */
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
		
		//add comments to the question
		String userId = validation.objectId
		commentsList=commentService.writeComments(userId, request.JSON.message, qId)
		commentsMap=[comments:commentsList]
		
		render commentsMap as JSON
	}
	
	/**
	 * @brief validate the request parameters
	 * @param request.json.message,sessionToken
	 * @param qId
	 * @param gameId
	 * @return
	 */
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
