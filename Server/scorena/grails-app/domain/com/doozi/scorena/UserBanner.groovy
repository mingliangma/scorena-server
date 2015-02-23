package com.doozi.scorena

import java.util.Date;
import com.doozi.scorena.transaction.LeagueTypeEnum;


class UserBanner {

	LeagueTypeEnum league // league code
	int type			  // 0 -> current month, 1 -> past month, 2 -> seasonal 
	int rank			  // user rank in league, rank <=10
	String bannerDateString 		  // month and year of banner
	Date created_at		  // date banner created 
	Date updated_at	 	  // date banner updated 
	
	static belongsTo = [account: Account]
	
    static constraints = {
    }
}
