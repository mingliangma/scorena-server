package com.doozi.scorena.tournament

/**
 * @author mingliangma
 * OWNER: the user created the tournament
 * ENROLLED_ACCEPT: the user joined the tournament by accepting the invitation
 * ENROLLED_SEARCH: the user joined the tournament from the search result
 * INVITED: invitation sent to the users, and it will remain in the invitation list
 * DECLINED: user declied the invitation, this tournament is not showed in the invitation list
 */
public enum EnrollmentStatusEnum {
	ENROLLED, INVITED, DECLINED
}
