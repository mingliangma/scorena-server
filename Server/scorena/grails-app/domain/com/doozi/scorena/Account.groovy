package com.doozi.scorena
import com.doozi.scorena.tournament.*;
import com.doozi.scorena.transaction.*;

class Account {
	String userId
	String username
	int currentBalance	
	int previousBalance
	
	
	//static belongsTo = [user: User]
	static hasMany = [enrollment: Enrollment, trans: ScorenaTransaction]

	static constraints = {
		userId unqiue: true
	}
	
	static mapping = {	
	}
 
}
