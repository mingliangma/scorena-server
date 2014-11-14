package com.doozi.scorena

import java.util.List;
import java.util.Map;

import grails.converters.JSON

class FriendSystemController {

    def friendSystemService
	def userService
	
	def friendRequest() {
		String userId1 = params.userId
		
		//validate all required input parameters exist
		Map validateResult = validateFriendRequest(request, userId1)
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
		
		String userId2 = request.JSON.userId2
		
		def tipsList = friendSystemService.friendRequest(userId1, userId2)
		
		if(tipsList != []) {
			response.status = 404
			def resp = [
				code: 101,
				error: tipsList
				]
			render resp as JSON
		}
		else {
			def tipsMap = [tips:tipsList]
			render tipsMap as JSON
		}
		
		return
	}
	
	def confirmFriendRequest() {
		String userId2 = params.userId
		
		//validate all required input parameters exist
		Map validateResult = validateConfirmFriendRequest(request, userId2)
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
		
		String requestId = request.JSON.requestId
		String userId1 = request.JSON.userId1
		
		def tipsList = friendSystemService.confirmFriendRequest(requestId, userId1, userId2)
		
		if(tipsList != []) {
			response.status = 404
			def resp = [
				code: 101,
				error: tipsList
				]
			render resp as JSON
		}
		else {
			def tipsMap = [tips:tipsList]
			render tipsMap as JSON
		}
		
		return
	}
	
	def listFriends() {
		String userId = params.userId
		def allFriendList
		def allFriendMap
		
		if(!userId) {
			response.status = 404
			def resp = [
				code: 101,
				error: "invalid userID!"
				]
			render resp as JSON
			return 
		}
		
		allFriendList = friendSystemService.listFriends(userId)
		allFriendMap = [allFriend:allFriendList]
		
		render allFriendMap as JSON
		
		return
	}
	
	/**
	 * @brief validate the request parameters
	 * @param request.json.userId2,sessionToken
	 * @return
	 */
	private Map validateFriendRequest(def request, userId1){
		if (!userId1 || !request.JSON.userId2 || !request.JSON.sessionToken){
			response.status = 404
			def resp = [
				code: 101,
				error: "invalid parameters"
				]
			return resp
		}
		return [:]
	}
	
	/**
	 * @brief validate the request parameters
	 * @param request.json.userId1,requestId,sessionToken
	 * @return
	 */
	private Map validateConfirmFriendRequest(def request, userId2){
		if (!userId2 || !request.JSON.requestId || !request.JSON.userId1 || !request.JSON.sessionToken){
			response.status = 404
			def resp = [
				code: 101,
				error: "invalid parameters"
				]
			return resp
		}
		return [:]
	}
}
