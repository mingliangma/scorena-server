package com.doozi.scorena.innersocialnetwork

import grails.transaction.Transactional

import com.doozi.scorena.Account
import com.doozi.scorena.FriendSystem

@Transactional
class FriendSystemService {

    def friendRequest(String userId1, String userId2) {
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		int status = 0
		Date createdTime = new Date()
		
		if(user1 != null && user2 != null) {
			FriendSystem friend = new FriendSystem(user1:user1,user2:user2,createdTime:createdTime)
			friend.save()
		}
	}
	
	def confirmFriendRequest(String requestId, String userId1, String userId2) {
		FriendSystem friend = FriendSystem.findById(requestId)
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		Date updatedTime = new Date()
		int status = 1
		
		if(friend != null && user1 != null && user2 != null) {
			if(friend.user1 == user1 && friend.user2 == user2) {
				friend.status = status
				friend.updatedTime = updatedTime
				friend.save()
			}
		}
	}
	
	List listFriends(userId) {
		Account user = Account.findByUserId(userId)
		//SQL Query find friend in FriendSystem where status=1
		String friendListQuery = "SELECT user1.username, user2.username FROM FriendSystem"+
		"WHERE status=1 AND (user1=user OR user2=user) AND (user1!=user OR user2 != user)"
		final int USERNAME1_QUERY_INDEX = 0
		final int USERNAME2_QUERY_INDEX = 1
		
		if(user != null) {
			//find all friend of user by SQL Query
			def allFriendResult = FriendSystem.executeQuery(friendListQuery)
			
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
	}
}