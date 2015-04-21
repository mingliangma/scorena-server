package com.doozi.scorena.useraccount.model

class UserData {
	
	String userId = ""
	int currentBalance = 0
	String avatarCode = ""
	String pictureUrl = ""
	String displayName = ""
	Date lastUpdate
	
	public UserData(String userId, int currentBalance, String avatarCode, String pictureUrl, String displayName){
		this.userId = userId
		this.currentBalance = currentBalance		
		this.pictureUrl =pictureUrl
		this.displayName =displayName
		if(avatarCode != null){
			this.avatarCode = avatarCode
		}
		if(pictureUrl != null){
			this.pictureUrl = pictureUrl
		}
		this.lastUpdate = new Date()
	}
}
