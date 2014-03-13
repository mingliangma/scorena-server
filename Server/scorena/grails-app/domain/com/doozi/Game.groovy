package com.doozi

class Game {
	String type
	String home
	String away
	String league
	String country
	Date date
	
	static hasOne = [gameEvent: GameEvent, gameResult:GameResult]
	static hasMany = [question: Question, bet: BetTransaction]
	
	
    static constraints = {
		gameEvent nullable: true, unique: true
		gameResult nullable: true, unique: true
	}
	
	def listUpcomingGameInAWeek(){
		def today = new Date();
		def upcomingGames = Game.findAll {
			date >= today && date <= (today + 7)
		}
		System.out.println("upcoming games: "+ upcomingGames.toListString());
		
	}
    
}
