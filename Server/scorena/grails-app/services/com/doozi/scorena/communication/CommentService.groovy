package com.doozi.scorena.communication

import grails.transaction.Transactional
import java.util.List

import com.doozi.scorena.Question
import com.doozi.scorena.Account

import grails.plugins.rest.client.RestBuilder


/**
 * @author HDJ
 *
 */

@Transactional
class CommentService {

	def helperService
	def parseService
	
	/**
	 * @brief get comments information of the question
	 * @param qId:ID of question
	 * @return List of comments:[body,userId,userName,timeCreated]
	 */
    List getExistingComments(qId)
	{
		List commentsList=[]
		Question q = Question.findById(qId)
		
		if (q==null){
			return [message: "invalid question ID"]
		}
		
		q.comments.each{
			Account user= Account.findById(it.posterId);
			if(user == null){
				return [message:"invalid user ID"]
			}
			
			def userId = user.userId
			def username = user.username
			def dateCreated=it.dateCreated
			dateCreated = helperService.getOutputDateFormat(dateCreated)
			
			def rest = new RestBuilder()
			def resp = parseService.retrieveUser(rest, userId)
			if (resp.status != 200) {
				println "get user profile failed! (CommentService::getExistingComments)"
				return [message: "get user profile failed!"]
			}
			def userProfile = resp.json
			
			if (userProfile.display_name != null && userProfile.display_name != "") {
				username = userProfile.display_name
			}
			def pictureURL = userProfile.pictureURL
			
			def comments=[body:it.body, userId:userId, name:username, timeCreated:dateCreated, pictureURL:pictureURL]
			commentsList.add(comments)
		}
		
		return commentsList
	}
	
	/**
	 * @brief add comments to question
	 * @param userId:userId of the account commenting
	 * @param message:content of comments
	 * @param qId:question ID of the question to be commented
	 * @return List of comments:[body,userId,userName,timeCreated]
	 */
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
		
		commentsList = getExistingComments(qId)
		return commentsList	
	}
}
