package com.doozi.scorena.push

import com.doozi.scorena.FriendSystem
import com.doozi.scorena.Question
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.useraccount.UserService
import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder

class PushController {
	def pushService
	def rest = new RestBuilder()
	def friendSystemService
	def betTransactionService
	def gameService
	
	def updateChannel()
	{
		def installation = pushService.updateGameChannel(rest, params.objectId, params.eventkey)
		render installation
	}
	
	def removeChannel()
	{
		def installation = pushService.removeGameChannel(rest, params.objectId, params.eventkey)
		render installation
	}
	
	def getUserInstallationID()
	{
		def installation = pushService.getInstallationByUserID(params.userId)
	//	render installation 
		render installation as JSON
	}
	
	/*
	def sendEndofGamePush()
	{
		def installation = pushService.endOfGamePush(rest, params.eventKey, params.userId, params.msg)
		render installation
	}
	
	*/
	
	/*def sendCommentPush()
	{
		String userId = params.userId
		String qId = params.qId
		
		def friends = friendSystemService.listFollowers(userId)
		def userList = betTransactionService.listAllBetsByQId(qId.toLong())
		Question q = Question.findById(qId)
		
		def game = gameService.getGame(q.eventKey)
		
		String home = game.home.teamname
		String away = game.away.teamname
		
		System.out.println("home : " + home + " away: " + away)
		
		ArrayList user = new ArrayList()
		
		if (userList != [])
		{
			for(BetTransaction action:userList )
			{
				user.add(action.account.userId)
				System.out.println(action.account.userId)
			}
			
			System.out.println("Friends")
			System.out.println( "size : " + friends.size())
			if (!friends.empty)
			{
				for(FriendSystem friend: friends)
				{
					if (!user.contains(friend.userId) && friend.userId != null)
					{
						user.add(friend.userId)
						System.out.println(friend.userId)
					}
				}
			}
		}
		
		else
		{
			System.out.println("no Users ")
			
			System.out.println("Friends")
			System.out.println( "size : " + friends.size())
			if (!friends.empty)
			{
				for(FriendSystem friend: friends)
				{
					if (!user.contains(friend.userId) && friend.userId != null)
					{
						
						user.add(friend.userId)
						System.out.println(friend.userId)
					}
				}
			}
			
		}
		
		System.out.println("qId, users list: " + user.toString())
		
		render friends as JSON
	}*/
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.info "${e.toString()}", e
	}
}
