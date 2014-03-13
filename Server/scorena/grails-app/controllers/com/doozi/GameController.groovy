package com.doozi

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
	
	def getUpcomingGames(){
		def upcomingGames = gameService.listUpcomingGames()
		render upcomingGames as JSON
	}
	
	def getFeatureEvents(){
		String theDate = "12/03/2014 16:00:00";
		def date1 = new Date().parse("d/M/yyyy H:m:s", theDate)
		theDate = "17/03/2014 15:00:00";
		def date2 = new Date().parse("d/M/yyyy H:m:s", theDate)
		theDate = "17/03/2014 20:00:00";
		def date3 = new Date().parse("d/M/yyyy H:m:s", theDate)
		
		JSONBuilder jSON = new JSONBuilder ()
		JSON json = jSON.build {
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
				
				unsued {					
					home = 	"Aston Villa"
					away = "Liverpool FC"
					date = date2
					league = "EPL"
					type = "soccer"
					quesiton = {
						content = "Who will score more?"
						pick1 = "Aston Villa"
						pick2 = "Liverpool FC"						
					}
				
				}
				
				unsued {
					home = 	"Stoke City"
					away = "Norwich City"					
					date = date3
					league = "EPL"
					type = "soccer"
					quesiton = {
						content = "Who will win between the two?"
						pick1 = "Stoke City"
						pick2 = "Norwich City"
					}				
				}
				
				unsued {
					home = 	"Success"
					away = "Fail"
					date = date3
					league = "EPL"
					type = "soccer"
					quesiton = {
						content = "is Scorena going to be successful"
						pick1 = "Success"
						pick2 = "More Success"
					}
				}
				
			}
		}
		
		System.out.println(json.toString())
		render json

	}
}
