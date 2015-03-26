package com.doozi.scorena.innersocialnetwork

import org.springframework.transaction.annotation.Transactional
import grails.async.Promise
import static grails.async.Promises.*

import com.doozi.scorena.Account
import com.doozi.scorena.FriendSystem
import grails.plugins.rest.client.RestBuilder

@Transactional
class FriendSystemService {
	
	def helperService
	def parseService
	def pushService
	def rest = new RestBuilder()
	
	final int USERID_QUERY_INDEX = 0
	final int BEINGFOLLOWED_USERID_QUERY_INDEX = 1
	final int FOLLOWER_COUNTER_QUERY_INDEX = 0
	final int FOLLOWING_COUNTER_QUERY_INDEX = 0
	
	final int CONNECTION_TYPE_FOLLOWING = 0
	final int CONNECTION_TYPE_FOLLOWER = 1
	final int CONNECTION_TYPE_ALL = 2
	
	List followAUser(String meUserId, String followingUserId){
		log.info "followAUser(): begins with meUserId = ${meUserId}, followingUserId = ${followingUserId}"
		Account meAccount = Account.findByUserId(meUserId)
		Account followingAccount = Account.findByUserId(followingUserId)
		return followAUser(meAccount, followingAccount)
	}
	
	List followAUser(Account meAccount, Account followingAccount){
		log.info "followAUser(): begins with meAccount = ${meAccount.userId}, followingAccount = ${followingAccount.userId}"

		int status = 0
		Date createdTime = new Date()
		//createdTime = helperService.getOutputDateFormat(createdTime)
		Date updatedTime = createdTime
		//updatedTime = helperService.getOutputDateFormat(updatedTime)
		
		def tips = []
		
		if(meAccount != null && followingAccount != null) {
			//before new a friend request record, we need to check it is requested before or not
			//duplication check
			String duplicationRequestQuery = "FROM FriendSystem WHERE (user=? AND following=?)"
			
			def duplicationResult = FriendSystem.findAll(duplicationRequestQuery,[meAccount,followingAccount])
			int duplicationResultSize = duplicationResult.size()
			
			if(duplicationResultSize == 0) {
				def following = new FriendSystem(user:meAccount,following:followingAccount,status:status,createdTime:createdTime,updatedTime:updatedTime)
				following.save()
				
				if (!following.save()) {
					following.errors.each {
						tips.add(it)						
					}
					log.error "friendRequest(): ${tips}"
				}
				else {
					log.info "friendRequest(): request records successfully!"
					tips = []
					
					def follower = parseService.retrieveUser(rest, meAccount.userId)
					def followerData = follower.json
					
					
					
					Promise p = task {
						String msg = followerData.display_name.toString() + " is now following you on Scorena."
						pushService.sendFollowPush(rest,followingAccount.userId,meAccount.userId,msg)
					}
					p.onComplete { result ->
						println "follow a user invitation notification promise returned $result"
					}
					
				}
			}
			else {
				log.error "friendRequest(): duplicated friend request!"
				tips = ["duplicated friend request!"]
			}
		}
		else {
			log.error "friendRequest(): invalid userId!"
			tips = ["invalid userId!"]
		}
		
		log.info "friendRequest(): ends with tips = ${tips}"
		
		return tips
	}
	
	List listFollowingUserId(String userId){
		return listConnectionsUserids(CONNECTION_TYPE_FOLLOWING, userId)
	}
	
	List listFollowers(userId) {
		log.info "listFollowers(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriends(): null userId"
			return []
		}
		List<Map> allFriendProfileList = []
		List allFriendList = listConnectionsUserids(CONNECTION_TYPE_FOLLOWER, userId)
		
		int allFriendListSize = allFriendList.size()
		if (allFriendListSize != 0) {
			allFriendProfileList = getUserProfile(allFriendList)
		}
		
		log.info "listFollowers(): ends with allFriendProfileList = ${allFriendProfileList}"
		
		return allFriendProfileList
	}
	
	List listFollowings(userId) {
		log.info "listFollowings(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriends(): null userId"
			return []
		}
		List<Map> allFriendProfileList = []
		List allFriendList = listConnectionsUserids(CONNECTION_TYPE_FOLLOWING, userId)
		
		int allFriendListSize = allFriendList.size()
		if (allFriendListSize != 0) {
			allFriendProfileList = getUserProfile(allFriendList)
		}
		
		log.info "listFollowings(): ends with allFriendProfileList = ${allFriendProfileList}"
		
		return allFriendProfileList
	}
	
	List listConnections(String userId){
		log.info "listAllConnections(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriends(): null userId"
			return []
		}
		
		List<Map> allFriendProfileList = []
		List allConnections = listConnectionsUserids(CONNECTION_TYPE_FOLLOWING, userId)
		allConnections.addAll(listConnectionsUserids(CONNECTION_TYPE_FOLLOWER, userId))
		List uniqueConnections = allConnections.unique()
		
		int allFriendListSize = uniqueConnections.size()
		if (allFriendListSize > 0) {
			allFriendProfileList = getUserProfile(uniqueConnections)
		}
		return allFriendProfileList
	}
	
	Map listFollowingUserIdInMap(userId){
		log.info "listFollowingUserIdInMap(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFollowingUserIdInMap(): null userId"
			return []
		}
		
		Account user = Account.findByUserId(userId)
		//SQL Query find friend in FriendSystem where status=1
		String friendListQuery = "SELECT user.userId, following.userId FROM FriendSystem WHERE user=?"

		Map allFollowingUserIdMap = [:]
		
		if(user != null) {
			//find all follower of user by SQL Query
			def allFollowingResult = FriendSystem.executeQuery(friendListQuery,[user])
			for(int i=0;i<allFollowingResult.size();i++) {
				
				List follower = allFollowingResult[i]
				allFollowingUserIdMap[follower[BEINGFOLLOWED_USERID_QUERY_INDEX]] = follower[BEINGFOLLOWED_USERID_QUERY_INDEX]
			}
		}
		
		log.info "listFollowingUserIdInMap(): ends with allFollowingUserIdMap = ${allFollowingUserIdMap}"
		
		return allFollowingUserIdMap
	}
	
	boolean isFollowing(String userId, String followingUserId){
		log.info "isFollowing(): begins with userId = ${userId} and followingUserId = ${followingUserId}"
		Account user = Account.findByUserId(userId)
		Account followingUser = Account.findByUserId(followingUserId)
		//SQL Query find friend in FriendSystem where status=1
		boolean isFollowing

		if(user != null & followingUser!=null) {
			String duplicationRequestQuery = "FROM FriendSystem WHERE (user=? AND following=?)"
			List resultList = FriendSystem.findAll(duplicationRequestQuery,[user,followingUser])

			if (resultList.size() == 0)
				isFollowing = false
			else
				isFollowing = true
		}
		
		log.info "isFollowing(): is ${userId} following ${followingUserId}: ${isFollowing}"
		return isFollowing
	}
	
	/**
	 * add user2 directly to friend of user1
	 * @param userId1
	 * @param userId2:facebook friend of user1
	 * @return []:successfully
	 */
	def addFacebookFriend(String userId1, String userId2) {
		log.info "addFacebookFriend(): begins with userId1 = ${userId1}, userId2 = ${userId2}"
		
		Account user1 = Account.findByUserId(userId1)
		Account user2 = Account.findByUserId(userId2)
		int status = 1
		Date createdTime = new Date()
		//createdTime = helperService.getOutputDateFormat(createdTime)
		Date updatedTime = new Date()
		//updatedTime = helperService.getOutputDateFormat(updatedTime)
		
		def tips = []
		
		if(user1 != null && user2 != null) {
			List result1 = followAUser(user1, user2)
			if (result1 != []){
				return result1
			}
			
			List result2 = followAUser(user2, user1)
			if (result2 != []){
				return result2
			}
			
			log.info "addFacebookFriend() now friends: userId=${userId1} and userId ${userId2}"
		}
		else {
			log.error "addFacebookFriend(): invalid userId!"
			tips = ["invalid userId!"]
		}
		
		log.info "addFacebookFriend(): ends with tips = ${tips}"
		
		return tips
	}
	
	private int getFollowingCounter(userId){
		String followingCounterQuery = "select count(*) from FriendSystem where user.userId=?"
		List followingCounter = FriendSystem.executeQuery(followingCounterQuery,[userId])
		return followingCounter[FOLLOWING_COUNTER_QUERY_INDEX]
	}
	
	private int getFollowerCounter(userId){
		String followerCounterQuery = "select count(*) from FriendSystem where following.userId=?"
		List followerCounter = FriendSystem.executeQuery(followerCounterQuery,[userId])
		return followerCounter[FOLLOWER_COUNTER_QUERY_INDEX]
	}
	
	private List listConnectionsUserids(int connectionType, String userId){
		log.info "listConnections(): begins with userId = ${userId}, connectionType = ${connectionType}"
		
		if (userId==null || userId==""){
			log.error "listFriendUserIds(): null userId"
			return []
		}		
		
		Account user = Account.findByUserId(userId)
		
		//SQL Query find friend in FriendSystem where status=1
		String query = ""
		switch(connectionType){
			case CONNECTION_TYPE_FOLLOWING:
				query = "SELECT following.userId FROM FriendSystem WHERE user=?"
				break
			case CONNECTION_TYPE_FOLLOWER:
				query = "SELECT user.userId FROM FriendSystem WHERE following=?"
				break
		}
		
		List UserIdList = []
		
		if(user != null) {
			//find all follower of user by SQL Query
			def friendsResult = FriendSystem.executeQuery(query,[user])
			for(int i=0;i<friendsResult.size();i++) {
				
				def connection = friendsResult[i]
				UserIdList.add(connection)
			}
		}
		
		log.info "listConnections(): ends"
		
		return UserIdList
	}
	
	private List<Map> getUserProfile (List userIdList) {
		log.info "getUserProfile(): begins with userIdList = ${userIdList}"
		
		def userProfileList = []
		def userProfileResultList = []
		
		Map userProfileResults = parseService.retrieveUserList(userIdList)
		if (userProfileResults.error){
			println "Error: FriendSystemService::getUserProfile(): in retrieving user "+userProfileResults.error
			log.error "getUserProfile(): in retrieving user ${userProfileResults.error}"
			return []
		}

		userProfileResultList = userProfileResults.results
		
		for (Map userProfile: userProfileResultList){
			String userId = userProfile.objectId
			Account user = Account.findByUserId(userId)
			if (user == null) {
				log.error "getUserProfile(): invalid userId!"
				return ["Error: invalid userId!(FriendSystemService::getUserProfile)"]
			}
			int currentBalance = user.currentBalance
			
			def userDataMap = [userId:userProfile.objectId, pictureURL:userProfile.pictureURL, avatarCode:userProfile.avatarCode, name:userProfile.username, currentBalance:currentBalance]
			
			if (userProfile.display_name != null && userProfile.display_name != "")
				userDataMap.name = userProfile.display_name
			
			if (userProfile.pictureURL != null && userProfile.pictureURL != "")
				userDataMap.pictureURL = userProfile.pictureURL
			
			if (userProfile.avatarCode != null && userProfile.avatarCode != "")
				userDataMap.avatarCode = userProfile.avatarCode
				
			userProfileList.add(userDataMap)
		}
		
		log.info "getUserProfile(): ends with userProfileList = ${userProfileList}"
		
		return userProfileList
	}
}