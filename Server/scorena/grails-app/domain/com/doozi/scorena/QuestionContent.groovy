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
	public static final String CUSTOM = "custom"
	public static final String DISABLE = "disable"
	
	static hasMany = [question: Question]

	
    static constraints = {
		indicator1 nullable: true
		indicator2 nullable: true
		question nullable: true
    }
}
