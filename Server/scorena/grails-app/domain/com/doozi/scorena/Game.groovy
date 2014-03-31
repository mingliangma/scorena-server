package com.doozi.scorena

class Game {
	String gameEventId
	Date startDate
	int gameWeek
	
	String type
	String league
	String country
	String city
	String siteKey
	String siteName
	
	String homeTeamId
	String homeTeamNameFirst
	String homeTeamNameLast
	String awayTeamId
	String awayTeamNameFirst
	String awayTeamNameLast
	
	Date createdAt
	
	
	static hasOne = [gameEvent: GameEvent, gameResult:GameResult]
	static hasMany = [question: Question, bet: BetTransaction]
	
	
    static constraints = {
		gameEvent nullable: true, unique: true
		gameResult nullable: true, unique: true
		homeTeamNameLast nullable: true
		awayTeamNameLast nullable: true
	}
	
	def listUpcomingGameInAWeek(){
		def today = new Date();
		def upcomingGames = Game.findAll {
			startDate >= today && startDate <= (today + 7)
		}
		System.out.println("upcoming games: "+ upcomingGames.toListString());
		
	}
    
}
