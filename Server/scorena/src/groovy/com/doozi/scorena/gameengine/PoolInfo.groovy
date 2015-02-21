package com.doozi.scorena.gameengine

import com.doozi.scorena.transaction.BetTransaction

class PoolInfo {
	int pick1Amount = 0
	int pick2Amount = 0
	int pick1NumPeople = 0
	int pick2NumPeople = 0
	int highestBetAmount 
	int highestBetPick 
	String highestBetUserId 
	boolean friendsExist = false 
	int friendBetAmount 
	int friendBetPick 
	String friendBetUserId 
	String friendPictureUrl
	String friendAvatarCode
	
	Date lastUpdate
	List<BetTransaction> betTransList
	
	}
