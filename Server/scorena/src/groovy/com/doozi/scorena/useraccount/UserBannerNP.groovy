package com.doozi.scorena.useraccount

import java.util.Date;

import com.doozi.scorena.transaction.LeagueTypeEnum;



/**
 * @author mingliangma
 *UserBannerNP is the non-persistence domain object for userbanner. It does not save into database.
 */
class UserBannerNP {
	LeagueTypeEnum league // league code
	int type			  // 0 -> current month, 1 -> past month, 2 -> seasonal
	int rank			  // user rank in league, rank <=10
	String bannerDateString		  // month and year of banner
}
