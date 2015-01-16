package com.doozi.scorena

/**
 * @author HDJ/Thomas
 *
 */
class FriendSystem {
	Account user1	//user1:the user who send the friend request
	Account user2	//user2:the user who need to accept the friend request 
	int status	//status of 0 or 1(0:not accepted yet,1:accepted)
	Date createdTime	//the time user1 first send request to user2
	Date updatedTime	//the time user2 accept the request
	
	static constraints = {
		//0:user1 & user2 not friend yet,1:user1 & user 2 friend
		status inList: [0,1]
		user1 nullable : false
		user2 nullable : false
	}
}
