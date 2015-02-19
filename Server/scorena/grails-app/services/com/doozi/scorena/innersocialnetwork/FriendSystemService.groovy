package com.doozi.scorena.innersocialnetwork

import org.springframework.transaction.annotation.Transactional

import com.doozi.scorena.Account
import com.doozi.scorena.FriendSystem

@Transactional
class FriendSystemService {
	
	def helperService
	def parseService
	
	final int USERID_QUERY_INDEX = 0
	final int BEINGFOLLOWED_USERID_QUERY_INDEX = 1
	final int FOLLOWER_COUNTER_QUERY_INDEX = 0
	final int FOLLOWING_COUNTER_QUERY_INDEX = 0
	
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
	
	int getFollowingCounter(userId){
		String followingCounterQuery = "select count(*) from FriendSystem where user.userId=?"
		List followingCounter = FriendSystem.executeQuery(followingCounterQuery,[userId])
		return followingCounter[FOLLOWING_COUNTER_QUERY_INDEX]
	}
	
	int getFollowerCounter(userId){
		String followerCounterQuery = "select count(*) from FriendSystem where following.userId=?"
		List followerCounter = FriendSystem.executeQuery(followerCounterQuery,[userId])
		return followerCounter[FOLLOWER_COUNTER_QUERY_INDEX]
	}
	
	List listFollowers(userId) {
		log.info "listFriends(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriends(): null userId"
			return []
		}
		List<Map> allFriendProfileList = []
		List allFriendList = listFollowerUserId(userId)
		
		int allFriendListSize = allFriendList.size()
		if (allFriendListSize != 0) {
			allFriendProfileList = getUserProfile(allFriendList)
		}
		
		log.info "listFriends(): ends with allFriendProfileList = ${allFriendProfileList}"
		
		return allFriendProfileList
	}
	
	List listFollowings(userId) {
		log.info "listFriends(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriends(): null userId"
			return []
		}
		List<Map> allFriendProfileList = []
		List allFriendList = listFollowingUserId(userId)
		
		int allFriendListSize = allFriendList.size()
		if (allFriendListSize != 0) {
			allFriendProfileList = getUserProfile(allFriendList)
		}
		
		log.info "listFriends(): ends with allFriendProfileList = ${allFriendProfileList}"
		
		return allFriendProfileList
	}
	
	List listFollowerUserId(userId){
		log.info "listFriendUserIds(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriendUserIds(): null userId"
			return []
		}
		
		Account user = Account.findByUserId(userId)
		//SQL Query find friend in FriendSystem where status=1
		String friendListQuery = "SELECT user.userId, following.userId FROM FriendSystem WHERE following=? "
		List allFollowerUserIdList = []
		
		if(user != null) {
			//find all follower of user by SQL Query
			def allFollowerResult = FriendSystem.executeQuery(friendListQuery,[user])			
			for(int i=0;i<allFollowerResult.size();i++) {
				
				List follower = allFollowerResult[i]				
				allFollowerUserIdList.add(follower[USERID_QUERY_INDEX])
			}
		}
		
		log.info "listFriendUserIds(): ends with allFriendUserIdList = ${allFollowerUserIdList}"
		
		return allFollowerUserIdList
	}
	
	List listFollowingUserId(userId){
		log.info "listFriendUserIds(): begins with userId = ${userId}"
		
		if (userId==null || userId==""){
			log.error "listFriendUserIds(): null userId"
			return []
		}
		
		Account user = Account.findByUserId(userId)
		//SQL Query find friend in FriendSystem where status=1
		String friendListQuery = "SELECT user.userId, following.userId FROM FriendSystem WHERE user=?"

		List allFollowingUserIdList = []
		
		if(user != null) {
			//find all follower of user by SQL Query
			def allFollowingResult = FriendSystem.executeQuery(friendListQuery,[user])
			for(int i=0;i<allFollowingResult.size();i++) {
				
				List follower = allFollowingResult[i]				
				allFollowingUserIdList.add(follower[BEINGFOLLOWED_USERID_QUERY_INDEX])
			}
		}
		
		log.info "listFriendUserIds(): ends with allFriendUserIdList = ${allFollowingUserIdList}"
		
		return allFollowingUserIdList
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

	//deprecated
//    List friendRequest(String userId1, String userId2) {
//		log.info "friendRequest(): begins with userId1 = ${userId1}, userId2 = ${userId2}"
//		
//		Account user1 = Account.findByUserId(userId1)
//		Account user2 = Account.findByUserId(userId2)
//		int status = 0
//		Date createdTime = new Date()
//		//createdTime = helperService.getOutputDateFormat(createdTime)
//		Date updatedTime = new Date()
//		//updatedTime = helperService.getOutputDateFormat(updatedTime)
//		
//		def tips = []
//		
//		if(user1 != null && user2 != null) {
//			//before new a friend request record, we need to check it is requested before or not
//			//duplication check
//			String duplicationRequestQuery = "SELECT user1.username,user2.username,status FROM FriendSystem "+
//			"WHERE (user1=? AND user2=?)"
//			
//			def duplicationResult = FriendSystem.executeQuery(duplicationRequestQuery,[user1,user2])
//			int duplicationResultSize = duplicationResult.size()
//			
//			if(duplicationResultSize == 0) {
//				def friend = new FriendSystem(user1:user1,user2:user2,status:status,createdTime:createdTime,updatedTime:updatedTime)
//				friend.save()
//				
//				if (!friend.save()) {
//					friend.errors.each {
//						println it
//						tips.add(it)
//						log.error "friendRequest(): ${it}"
//					}
//				}
//				else {
//					println("request records successfully!")
//					log.info "friendRequest(): request records successfully!"
//					tips = []
//				}
//			}
//			else {
//				println("duplicated friend request!")
//				log.error "friendRequest(): duplicated friend request!"
//				tips = ["duplicated friend request!"]
//			}
//		}
//		else {
//			println("invalid userId!")
//			log.error "friendRequest(): invalid userId!"
//			tips = ["invalid userId!"]
//		}
//		
//		log.info "friendRequest(): ends with tips = ${tips}"
//		
//		return tips
//	}
	
	//deprecated
//	List confirmFriendRequest(String requestId, String userId1, String userId2) {
//		log.info "confirmFriendRequest(): begins with requestId = ${requestId}, userId1 = ${requestId}, userId2 = ${userId2}"
//		
//		FriendSystem friend = FriendSystem.findById(requestId)
//		Account user1 = Account.findByUserId(userId1)
//		Account user2 = Account.findByUserId(userId2)
//		Date updatedTime = new Date()
//		//updatedTime = helperService.getOutputDateFormat(updatedTime)
//		int status = 1
//		
//		def tips = []
//		
//		if(friend != null && user1 != null && user2 != null) {
//			if(friend.user1 == user1 && friend.user2 == user2) {
//				friend.status = status
//				friend.updatedTime = updatedTime
//				friend.save()
//				
//				if (!friend.save()) {
//					friend.errors.each {
//						println it
//						tips.add(it)
//					}
//				}
//				else {
//					println("confirmation records successfully!")
//					log.info "confirmFriendRequest(): confirmation records successfully!"
//					tips = []
//				}
//			}
//			else {
//				println("wrong requestId!")
//				log.error "confirmFriendRequest(): wrong requestId!"
//				tips = ["wrong requestId!"]
//			}
//		}
//		else {
//			System.out.print("invalid userId or requestId!")
//			log.error "confirmFriendRequest(): invalid userId or requestId!"
//			tips = ["invalid userId or requestId!"]
//		}
//		
//		log.info "confirmFriendRequest(): ends with tips = ${tips}"
//		
//		return tips
//	}
//	
//	List listFriendUserIds(userId){
//		log.info "listFriendUserIds(): begins with userId = ${userId}"
//		
//		if (userId==null || userId==""){
//			log.error "listFriendUserIds(): null userId"
//			return []
//		}
//		
//		Account user = Account.findByUserId(userId)
//		//SQL Query find friend in FriendSystem where status=1
//		String friendListQuery = "SELECT user1.userId, user2.userId FROM FriendSystem "+
//		"WHERE status=1 AND (user1=? OR user2=?) AND (user1!=? OR user2!=?)"
//		final int USERID1_QUERY_INDEX = 0
//		final int USERID2_QUERY_INDEX = 1
//		List allFriendUserIdList = []
//		
//		if(user != null) {
//			//find all friend of user by SQL Query
//			def allFriendResult = FriendSystem.executeQuery(friendListQuery,[user,user,user,user])
//			
//			int allFriendSize = allFriendResult.size()
//			
//			for(int i=0;i<allFriendSize;i++) {
//				List friendSystem = allFriendResult[i]
//				
//				if(friendSystem[USERID1_QUERY_INDEX]==user.userId && friendSystem[USERID2_QUERY_INDEX]!=user.userId) {
//					String friendUserId = friendSystem[USERID2_QUERY_INDEX]
//					allFriendUserIdList.add(friendUserId)
//				}
//				
//				if(friendSystem[USERID2_QUERY_INDEX]==user.userId && friendSystem[USERID1_QUERY_INDEX]!=user.userId) {
//					String friendUserId = friendSystem[USERID1_QUERY_INDEX]
//					allFriendUserIdList.add(friendUserId)
//				}
//			}
//		}
//		
//		log.info "listFriendUserIds(): ends with allFriendUserIdList = ${allFriendUserIdList}"
//		
//		return allFriendUserIdList
//	}
//	
//	List listFriends(userId) {
//		log.info "listFriends(): begins with userId = ${userId}"
//		
//		if (userId==null || userId==""){
//			log.error "listFriends(): null userId"
//			return []
//		}
//		List<Map> allFriendProfileList = []
//		List allFriendList = listFriendUserIds(userId)
//		
//		int allFriendListSize = allFriendList.size()
//		if (allFriendListSize != 0) {
//			allFriendProfileList = getUserProfile(allFriendList)
//		}
//		
//		log.info "listFriends(): ends with allFriendProfileList = ${allFriendProfileList}"
//		
//		return allFriendProfileList			
//	}
	
	List<Map> getUserProfile (List userIdList) {
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
			
			def userDataMap = [userId:userProfile.objectId, pictureURL:userProfile.pictureURL, name:userProfile.username, currentBalance:currentBalance]
			
			if (userProfile.display_name != null && userProfile.display_name != "")
				userDataMap.name = userProfile.display_name
			
			if (userProfile.pictureURL != null && userProfile.pictureURL != "")
				userDataMap.pictureURL = userProfile.pictureURL
				
			userProfileList.add(userDataMap)
		}
		
		log.info "getUserProfile(): ends with userProfileList = ${userProfileList}"
		
		return userProfileList
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
	
	/**
	 * judge user1 and user2 are friends or not
	 * @param userId1
	 * @param userId2
	 * @return boolean
	 */
//	Boolean isFriend(String userId1, String userId2) {
//		log.info "isFriend(): begins with userId1 = ${userId1}, userId2 = ${userId2}"
//		
//		Account user1 = Account.findByUserId(userId1)
//		Account user2 = Account.findByUserId(userId2)
//		Boolean isFriend = false
//		
//		String friendListQuery = "SELECT user1.username, user2.username FROM FriendSystem "+
//		"WHERE status=1 AND (user1=? OR user2=?) AND (user1=? OR user2=?)"
//		
//		if(user1 != null && user2 != null) {
//			def isFriendResult = FriendSystem.executeQuery(friendListQuery,[user1,user1,user2,user2])
//			int isFriendResultSize = isFriendResult.size()
//			if(isFriendResultSize > 0) 
//				isFriend = true
//			else 
//				isFriend = false
//		}
//		else {
//			def error = ["invalid userID!(isFriend)"]
//			println "error:" + error
//			log.error "isFriend(): ${error}"
//		}
//		
//		log.info "isFriend(): ends with isFriend = ${isFriend}"
//		
//		return isFriend
//	}
}