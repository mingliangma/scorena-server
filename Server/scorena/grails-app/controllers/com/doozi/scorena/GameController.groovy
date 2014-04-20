package com.doozi.scorena

import grails.converters.JSON
import grails.web.JSONBuilder


// /v1/sports/soccer/premier/upcomingevents - current day's game information
//		get

// /v1/sports/soccer/premier/topevents - get feature games
//		get

// /v1/sports/soccer/premier/historyevents - last seven days


//response: team names, home team, away team, date (UTC?), away # people, home # people


//1) game list
//2) question list + preview question content
//3) question content + social friendlist (home friend request)
//3b social friend list from away side 

class GameController {

    def index() {
//		String theDate = "12/03/2014 16:00:00";
//		def newdate = new Date().parse("d/M/yyyy H:m:s", theDate)
//		def game = new Game(league: "EPL", away: "Chelsea", home:"Man Unitied", type:"soccer", country:"england", date:newdate)
//		if (game.save()){
//			System.out.println("user successfully saved")
//			render game as JSON
//		}else{
//			System.out.println("user save failed")
//			render status:404
//		}
	}
	def gameService
	def viewService
	

	
	def getUpcomingGames(){
		def upcomingGames = gameService.listUpcomingGames()
		render upcomingGames as JSON
	}
	
	def getPastGames(){
		def upcomingGames = gameService.listPastGames()
		render upcomingGames as JSON
	}
	
	def getGame(){
		println params.gameId
		if (params.gameId){
			def questions = gameService.getGame2(params.gameId)
			render questions as JSON
		}
	}
	
	def getFeatureEvents(){
		String theDate = "12/03/2014 16:00:00";
		def date1 = new Date().parse("d/M/yyyy H:m:s", theDate)
		theDate = "17/03/2014 15:00:00";
		def date2 = new Date().parse("d/M/yyyy H:m:s", theDate)
		theDate = "17/03/2014 20:00:00";
		def date3 = new Date().parse("d/M/yyyy H:m:s", theDate)
		
		JSONBuilder jSON = new JSONBuilder ()
		JSON content = jSON.build {
			games = array {
				unsued {
					home = 	"Stoke City"
					away = "Chelsea"					
					date = date1
					league = "EPL"
					type = "soccer"
					quesiton = {
						content = "Who will score the first goal?"
						pick1 = "Stoke City"
						pick2 = "Chelsea"
					}
				}		
			}
		}
		
		response.status = 200
		render  content
	}
	
	def upcomingEplSportsDb(){
		def upcomingGames = viewService.getUpcomingEplMatches()
		render upcomingGames as JSON
	}
	
	def upcomingChampSportsDb(){
		def upcomingGames = viewService.getUpcomingChampMatches()
		render upcomingGames as JSON
	}
	
	def pastEplSportsDb(){
		def pastGames = viewService.getPastEplMatches()
		render pastGames as JSON
	}
	
	def pastChampSportsDb(){
		def pastGames = viewService.getPastChampMatches()
		render pastGames as JSON
	}
}
