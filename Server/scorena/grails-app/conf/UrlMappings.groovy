class UrlMappings {

//	PUT implies putting a resource - completely replacing whatever is available at the given URL with a different thing. By definition, a PUT is idempotent. Do it as many times as you like, and the result is the same. x=5 is idempotent. You can PUT a resource whether it previously exists, or not (eg, to Create, or to Update)!
//	POST updates a resource, adds a subsidiary resource, or causes a change. A POST is not idempotent, in the way that x++ is not idempotent.
//	
//	By this argument, PUT is for creating when you know the URL of the thing you will create. POST can be used to create when you know the URL of the "factory" or manager for the category of things you want to create.
//	
//	so: POST /expense-report
//	or: PUT  /expense-report/10929
	
	static mappings = {
//		"/$controller/$action?/$id?"{
//			constraints {
//				// apply constraints here
//			}
//		}
		
		"/v1/users"(controller:"user") {
			action = [GET:"list"]
		}
		
		"/v1/users/new"(controller:"user") {
			action = [POST:"createNewUser"]
		}
		
		"/v1/login"(controller:"user"){
			action = [GET:"login"]
		}
		
		"/v1/users/$userId?"(controller:"user") {
			action = [GET:"getUserProfile", PUT:"updateUserProfile", DELETE:"deleteUserProfile" ]
		}	
		
		"/v1/sports/soccer/leagues/epl/games/feature"(controller:"game"){
			action = [GET:"getFeatureEvents"]
		}
		
		"/v1/sports/soccer/leagues/epl/games/upcoming"(controller:"game"){
			action = [GET:"getUpcomingGames"]
		}
		
		"/v1/sports/soccer/leagues/epl/games/past"(controller:"game"){
			action = [GET:"getPastGames"]
		}
		
		"/v1/sports/soccer/leagues/epl/games/$gameId?"(controller:"game"){
			action = [GET:"getGame"]
		}
		
		"/v1/sports/soccer/leagues/epl/games/$gameId?/qs"(controller:"question"){
			action = [GET:"listQuestions"]
		}

		"/v1/sports/soccer/leagues/epl/games/$gameId?/qs/$qId?"(controller:"question"){
			action = [GET:"getQuestionDetails"]
		}
		
		"/v1/sports/soccer/leagues/epl/wagers/new"(controller:"bet"){
			action = [POST:"placeBet"]
		}
		

        "/"(view:"/index")
        "500"(view:'/error')
	}
}
