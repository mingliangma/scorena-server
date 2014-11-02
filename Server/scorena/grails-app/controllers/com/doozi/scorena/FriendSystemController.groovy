package com.doozi.scorena

import java.util.List;
import grails.converters.JSON

class FriendSystemController {

    def friendSystemService
	
	def friendRequest() {
		String userId1 = params.userId1
		String userId2 = params.userId2
		friendSystemService.friendRequest(userId1, userId2)
		
		return
	}
	
	def confirmFriendRequest() {
		String requestId = params.requestId
		String userId1 = params.userId1
		String userId2 = params.userId2
		friendSystemService.confirmFriendRequest(requestId, userId1, userId2)
		
		return
	}
	
	def listFriends() {
		String userId = params.userId
		List allFriendList = []
		Map allFriendMap = []

		allFriendList = friendSystemService.listFriends(userId)
		allFriendMap = [allFriend:allFriendList]
		
		render allFriendMap as JSON
	}
}
