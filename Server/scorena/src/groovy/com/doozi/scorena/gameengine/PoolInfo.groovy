package com.doozi.scorena.gameengine

import com.doozi.scorena.transaction.BetTransaction

class PoolInfo {
	int pick1Amount = 0
	int pick2Amount = 0
	int pick1NumPeople = 0
	int pick2NumPeople = 0
	int highestBetAmount = 0
	int highestBetPick = 0
	String highestBetUserId =""
	
	Date lastUpdate
	List<BetTransaction> betTransList
	
	}
