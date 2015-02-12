package com.doozi.scorena.communication

import org.springframework.transaction.annotation.Transactional

import java.util.List

import com.doozi.scorena.FriendSystem
import com.doozi.scorena.Question
import com.doozi.scorena.Account
import com.doozi.scorena.transaction.BetTransaction
import com.mysql.jdbc.log.Log;

import grails.plugins.rest.client.RestBuilder


/**
 * @author HDJ
 *
 */


class CommentService {

	def helperService
	def parseService
	def friendSystemService
	def betTransactionService
	def pushService
	def gameService
	def rest = new RestBuilder()
	
	/**
	 * @brief get comments information of the question
	 * @param qId:ID of question
	 * @return List of comments:[body,userId,userName,timeCreated]
	 */
	List getExistingComments(qId){

		log.info "getExistingComments(): begins with qId = ${qId}"

		List commentsList=[]
		Question q = Question.findById(qId)
		def message

		if (q==null){
			message = "invalid question ID"
			log.error "${message}"
			return [tips:message]
		}

		q.comments.each{
			Account user= Account.findById(it.posterId);
			if(user == null){
				message = "invalid user ID"
				log.error "${message}"
				return [tips:message]
			}

			def userId = user.userId
			def username = user.username
			def dateCreated=it.dateCreated
			dateCreated = helperService.getOutputDateFormat(dateCreated)

			def rest = new RestBuilder()
			def resp = parseService.retrieveUser(rest, userId)
			if (resp.status != 200) {
				message = "get user profile failed!"
				log.error "${message}"
				return [tips:message]
			}
			def userProfile = resp.json

			if (userProfile.display_name != null && userProfile.display_name != "") {
				username = userProfile.display_name
			}
			def pictureURL = userProfile.pictureURL

			def comments=[body:it.body, userId:userId, name:username, timeCreated:dateCreated, pictureURL:pictureURL]
			commentsList.add(comments)
		}

		log.info "getExistingComments(): ends with commentsList = ${commentsList}"

		return commentsList
	}

	/**
	 * @brief add comments to question
	 * @param userId:userId of the account commenting
	 * @param message:content of comments
	 * @param qId:question ID of the question to be commented
	 * @return List of comments:[body,userId,userName,timeCreated]
	 */
	@Transactional
	List writeComments(userId,message,qId){

		log.info "writeComments(): begins with userId = ${userId}, qId = ${qId}, message = ${message}"

		List commentsList=[]
		Question q = Question.findById(qId)
		Account user= Account.findByUserId(userId)
		boolean isValidUserId = false
		def errorMessage

		def friends = friendSystemService.listFollowers(userId)
		def userList = betTransactionService.listAllBetsByQId(q.id)
		def game = gameService.getGame(q.eventKey)
		def userInstallation = parseService.retrieveUser(rest, userId)
		def userData = userInstallation.json
		
		String status = game.gameStatus
		String home = game.home.teamname
		String away = game.away.teamname

		System.out.println(status)
		
		String msg = userData.display_name.toString() + " has just commented on the "+ away +" vs "+ home+" game."

		ArrayList pushUsers = new ArrayList()
		
		if (q==null){
			errorMessage = "invalid question ID"
			log.error "${errorMessage}"
			return [tips: errorMessage]
		}

		if (user==null){
			errorMessage = "invalid user ID"
			log.error "${errorMessage}"
			return [tips: errorMessage]
		}

		if (message==null){
			errorMessage = "null comment"
			log.error "${errorMessage}"
			return [tips: errorMessage]
		}

		if (user != null){
			isValidUserId = true
		}

		if(isValidUserId){
			q.addComment(user, message)
			if (userList != [])
			{
				for(BetTransaction action:userList )
				{
					pushUsers.add(action.account.userId)
				}
				
				/*
				if (!friends.empty)
				{
					for(FriendSystem friend: friends)
					{
						if (!pushUsers.contains(friend.userId) && friend.userId != null)
						{
							pushUsers.add(friend.userId)
						}
					}
				} */
			}
			
		/*	else
			{
				if (!friends.empty)
				{
					for(FriendSystem friend: friends)
					{
						if (!pushUsers.contains(friend.userId) && friend.userId != null)
						{
							pushUsers.add(friend.userId)
						}
					}
				}
			} */
			
			if (!pushUsers.empty)
			{
				pushService.userCommentPush(rest,pushUsers.toString(), q.eventKey, status,qId,msg )
			}
		}

		commentsList = getExistingComments(qId)

		log.info "writeComments(): ends with commentsList = ${commentsList}"

		return commentsList
	}
}
