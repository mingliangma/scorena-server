package com.doozi.scorena

class GameResult {
	String gameEventId
	
	String homeTeamId
	int homeScore
	int homePeriod1Score
	int homePeriod2Score
	int homeYellowCard
	int homeRedCard
	String homeLineFormation
	
	
	String awayTeamId
	int awayScore
	int awayPeriod1Score
	int awayPeriod2Score
	int awayYellowCard
	int awayRedCard
	String awayLineFormation
	
	boolean transCleared
	
	static belongsTo = [game: Game]
    static constraints = {
    }
}
