package com.doozi.scorena

import grails.converters.JSON

class CommentController {

    def commentService
	def userService
	
	def index() { }
	
	def writeComments(){
		def commentsList
		
		/*if (params.qId && params.message && params.userId && userService.accountExists(params.userId)){
			commentsList=commentService.writeComments(params.userId,params.message,params.qId)
		}*/
		
		commentsList=commentService.writeComments(params.userId,params.message,params.qId)
		
		render commentsList as JSON
	}
	
	def getExistingComments(){
		def commentsList
		
		/*if (params.qId){
			commentsList=commentService.getExistingComments(params.qId)
		}*/
		
		commentsList=commentService.getExistingComments(params.qId)
		
		render commentsList as JSON
	}
}
