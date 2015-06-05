package com.doozi.scorena


//question type team-0: Which team will win
//question type truefalse-0: will the total score be more than 2.5 goals?

class QuestionContent {
	
	String content
	String indicator1
	String indicator2
	String sport
	String questionType
	String teamIndicator
	String playerIndicator
	
	public static final String WHOWIN = "team-0"
	public static final String SCOREGREATERTHAN = "truefalse-0"
	public static final String SCOREGREATERTHAN_SOCCER = "truefalse-0"
	public static final String SCOREGREATERTHAN_BASKETBALL = "truefalse-1"	
	
	public static final String CUSTOM = "custom" 
	public static final String CUSTOMSURVEY = "customsurvey"
	public static final String HIGHERFIELDGOAL_BASKETBALL= "team-1"
	public static final String HIGHERREBOUNDS_BASKETBALL = "team-2"
	public static final String AUTOCUSTOM_NBA1 = "autocustom-team-nba-1"
	public static final String AUTOCUSTOM_SOCCER1 = "autocustom-team-soccer-1"
	public static final String AUTOCUSTOM_PREFIX = "autocustom"
	public static final String DISABLE = "disable"
	
	public static final String BASEBALL_TEAM_HIT_MORE_HR = "baseball-teamHitMoreHR"
	public static final String BASEBALL_PLAYER_HIT_FIRST_HR = "baseball-playerHitFirstHR"
	public static final String BASEBALL_TF_PLAYER_HIT_HR = "baseball-willPlayerHitHR"
	public static final String BASEBALL_TF_TOTAL_HR_HIGHER_THAN = "baseball-totalHRHigherThan"
	public static final String BASEBALL_TF_HR_BEFORE_INNING = "baseball-HRBeforeInning"
	public static final String BASEBALL_TF_SB = "baseball-willThereBeSB"
	public static final String BASEBALL_TF_EXTRAINNING = "baseball-willThereBeExtraInning"
	public static final String BASEBALL_TEAM_MORE_HIT = "baseball-teamHasMoreHit"
	public static final String BASEBALL_TEAM_MORE_SO = "baseball-teamHasMoreSO"
	
	
	static hasMany = [question: Question]

	
    static constraints = {
		indicator1 nullable: true
		indicator2 nullable: true
		teamIndicator nullable: true
		playerIndicator nullable: true
		question nullable: true
    }
}
