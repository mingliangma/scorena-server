package com.doozi.scorena.innersocialnetwork

import grails.transaction.Transactional

import com.doozi.scorena.Account
import com.doozi.scorena.FriendSystem

@Transactional
class FriendSystemService {
	
	def helperService

    List friendRequest(String userId1, String userId2) {
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		int status = 0
		Date createdTime = new Date()
		//createdTime = helperService.getOutputDateFormat(createdTime)
		Date updatedTime = new Date()
		//updatedTime = helperService.getOutputDateFormat(updatedTime)
		
		def tips = []
		
		if(user1 != null && user2 != null) {
			//before new a friend request record, we need to check it is requested before or not
			//duplication check
			String duplicationRequestQuery = "SELECT user1.username,user2.username,status FROM FriendSystem "+
			"WHERE (user1=? AND user2=?)"
			
			def duplicationResult = FriendSystem.executeQuery(duplicationRequestQuery,[user1,user2])
			int duplicationResultSize = duplicationResult.size()
			
			if(duplicationResultSize == 0) {
				def friend = new FriendSystem(user1:user1,user2:user2,status:status,createdTime:createdTime,updatedTime:updatedTime)
				friend.save()
				
				if (!friend.save()) {
					friend.errors.each {
						println it
						tips.add(it)
					}
				}
				else {
					println("request records successfully!")
					tips = []
				}
			}
			else {
				println("duplicated friend request!")
				tips = ["duplicated friend request!"]
			}
		}
		else {
			println("invalid userId!")
			tips = ["invalid userId!"]
		}
		
		return tips
	}
	
	List confirmFriendRequest(String requestId, String userId1, String userId2) {
		FriendSystem friend = FriendSystem.findById(requestId)
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		Date updatedTime = new Date()
		//updatedTime = helperService.getOutputDateFormat(updatedTime)
		int status = 1
		
		def tips = []
		
		if(friend != null && user1 != null && user2 != null) {
			if(friend.user1 == user1 && friend.user2 == user2) {
				friend.status = status
				friend.updatedTime = updatedTime
				friend.save()
				
				if (!friend.save()) {
					friend.errors.each {
						println it
						tips.add(it)
					}
				}
				else {
					println("confirmation records successfully!")
					tips = []
				}
			}
			else {
				println("wrong requestId!")
				tips = ["wrong requestId!"]
			}
		}
		else {
			System.out.print("invalid userId or requestId!")
			tips = ["invalid userId or requestId!"]
		}
		
		return tips
	}
	
	List listFriends(userId) {
		Account user = Account.findByUserId(userId)
		//SQL Query find friend in FriendSystem where status=1
		String friendListQuery = "SELECT user1.username, user2.username FROM FriendSystem "+
		"WHERE status=1 AND (user1=? OR user2=?) AND (user1!=? OR user2 != ?)"
		final int USERNAME1_QUERY_INDEX = 0
		final int USERNAME2_QUERY_INDEX = 1
		
		if(user != null) {
			//find all friend of user by SQL Query
			def allFriendResult = FriendSystem.executeQuery(friendListQuery,[user,user,user,user])
			
			def allFriendList = []
			int allFriendSize = allFriendResult.size()
			
			for(int i=0;i<allFriendSize;i++) {
				List friendSystem = allFriendResult[i]
				
				if(friendSystem[USERNAME1_QUERY_INDEX]==user.username && friendSystem[USERNAME2_QUERY_INDEX]!=user.username) {
					String friendUserName = friendSystem[USERNAME2_QUERY_INDEX]
					allFriendList.add(friendUserName)
				}
				
				if(friendSystem[USERNAME2_QUERY_INDEX]==user.username && friendSystem[USERNAME1_QUERY_INDEX]!=user.username) {
					String friendUserName = friendSystem[USERNAME1_QUERY_INDEX]
					allFriendList.add(friendUserName)
				}
			}
			
			return allFriendList			
		}
		else {
			def error = ["invalid userID!"]
			return error
		}
	}
	
	/**
	 * add user2 directly to friend of user1
	 * @param userId1
	 * @param userId2:facebook friend of user1
	 * @return []:successfully
	 */
	def addFacebookFriend(String userId1, String userId2) {
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		int status = 1
		Date createdTime = new Date()
		//createdTime = helperService.getOutputDateFormat(createdTime)
		Date updatedTime = new Date()
		//updatedTime = helperService.getOutputDateFormat(updatedTime)
		
		def tips = []
		
		if(user1 != null && user2 != null) {
			//before setting up a friend system, we need to check it is set before or not
			//duplication check
			String duplicationRequestQuery = "SELECT user1.username,user2.username,status FROM FriendSystem "+
			"WHERE (user1=? AND user2=? AND status=1) OR (user1=? AND user2=? AND status=1)"
			
			def duplicationResult = FriendSystem.executeQuery(duplicationRequestQuery,[user1,user2,user2,user1])
			int duplicationResultSize = duplicationResult.size()
			
			if(duplicationResultSize == 0) {
				def friend = new FriendSystem(user1:user1,user2:user2,status:status,createdTime:createdTime,updatedTime:updatedTime)
				friend.save()
				
				if (!friend.save()) {
					friend.errors.each {
						println it
						tips.add(it)
					}
				}
				else {
					println("facebook friend system set up successfully!")
					tips = []
				}
			}
			else {
				println("friend system set up before!")
				tips = ["friend system set up before!"]
			}
		}
		else {
			println("invalid userId!")
			tips = ["invalid userId!"]
		}
		
		return tips
	}
	
	/**
	 * judge user1 and user2 are friends or not
	 * @param userId1
	 * @param userId2
	 * @return boolean
	 */
	Boolean isFriend(String userId1, String userId2) {
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		Boolean isFriend = false
		
		String friendListQuery = "SELECT user1.username, user2.username FROM FriendSystem "+
		"WHERE status=1 AND (user1=? OR user2=?) AND (user1=? OR user2=?)"
		
		if(user1 != null && user2 != null) {
			def isFriendResult = FriendSystem.executeQuery(friendListQuery,[user1,user1,user2,user2])
			int isFriendResultSize = isFriendResult.size()
			if(isFriendResultSize > 0) 
				isFriend = true
			else 
				isFriend = false
		}
		else {
			def error = ["invalid userID!(isFriend)"]
			println "error:" + error
		}
		
		return isFriend
	}
}