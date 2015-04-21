package com.doozi.scorena
import com.doozi.scorena.tournament.*;
import com.doozi.scorena.transaction.*;
import com.doozi.scorena.score.*;

class Account {
	String userId
	String username
	int currentBalance	
	int previousBalance
	int accountType
	int currentScore
	int previousScore
	
	String displayName
	String avatarCode
	String pictureUrl
	
	
	//static belongsTo = [user: User]
	static hasMany = [enrollment: Enrollment, trans: AbstractTransaction, score: AbstractScore, banner:UserBanner]

	static constraints = {
		userId unqiue: true
		avatarCode nullable: true
		pictureUrl nullable: true
	}
	
	static mapping = {	
	}
	
	static marshalling={
		shouldOutputIdentifier false
		shouldOutputVersion false
		shouldOutputClass false
		ignore "enrollment", "trans", "score", "banner",  "previousBalance", "accountType", "currentScore", "previousScore"
	}
 
}
