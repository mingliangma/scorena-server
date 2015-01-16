package com.doozi.scorena


//question type team-0: Which team will win
//question type truefalse-0: will the total score be more than 2.5 goals?

class QuestionContent {
	
	String content
	String indicator1
	String indicator2
	String sport
	String questionType
	
	public static final String WHOWIN = "team-0"
	public static final String SCOREGREATERTHAN = "truefalse-0"
	public static final String SCOREGREATERTHAN_SOCCER = "truefalse-0"
	public static final String SCOREGREATERTHAN_BASKETBALL = "truefalse-1"
	public static final String CUSTOM = "custom" 
	public static final String AUTOCUSTOM_NBA1 = "autocustom-team-nba-1"
	public static final String AUTOCUSTOM_SOCCER1 = "autocustom-team-soccer-1"
	public static final String AUTOCUSTOM_PREFIX = "autocustom"
	public static final String DISABLE = "disable"
	
	static hasMany = [question: Question]

	
    static constraints = {
		indicator1 nullable: true
		indicator2 nullable: true
		question nullable: true
    }
}
