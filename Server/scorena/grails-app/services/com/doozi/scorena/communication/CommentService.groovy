package com.doozi.scorena.communication

import com.doozi.scorena.Question
import com.doozi.scorena.Account
import grails.transaction.Transactional

import java.util.List

@Transactional
class CommentService {

	def helperService
	
    List getExistingComments(qId)
	{
		List commentsList=[]
		Question q = Question.findById(qId)
		
		if (q==null){
			return [message: "invalid question ID"]
		}
		
		q.comments.each{
			Account user= Account.findById(it.posterId);
			if(user==null){
				return [message:"invalid user ID"]
			}
			
			def dateCreated=it.dateCreated
			dateCreated = helperService.getOutputDateFormat(dateCreated)
			
			def comments=[body:it.body,userId:user.userId,userName:user.username,timeCreated:dateCreated]
			commentsList.add(comments)
		}
		
		return commentsList
	}
	
	List writeComments(userId,message,qId){
		List commentsList=[]
		Question q = Question.findById(qId)
		Account user= Account.findByUserId(userId)
		boolean isValidUserId = false
		
		if (q==null){
			return [tips: "invalid question ID"]
		}
		
		if (user==null){
			return [tips: "invalid user ID"]
		}
		
		if (message==null){
			return [tips: "null comment"]
		}
		
		if (user != null){
			isValidUserId = true
		}
		
		if(isValidUserId){
			q.addComment(user, message)
		}
		
		commentsList=getExistingComments(qId)
		return commentsList	
	}
}
