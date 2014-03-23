package com.doozi

class User {
	
    String FID
    String email
    String displayName
	String password
	
	//Account account
	//static hasOne = [account: Account]
	//static hasMany = [bet: BetTransaction]
	

    static constraints = {
//		email blank: false, nullable:false
//		displayName blank: false, nullable:false
//		password blank: false, nullable:false
//		bet nullable: true
    }
}
