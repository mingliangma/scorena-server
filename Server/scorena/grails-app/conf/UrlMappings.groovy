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
		
		"/v1/users/requestPasswordReset"(controller:"user") {
			action = [POST:"passwordReset"]
		}
		
		"/v1/users/new"(controller:"user") {
			action = [POST:"createNewUser"]
		}
		
		"/v1/users/new/fb"(controller:"user") {
			action = [POST:"socialNetworkUserPostSetup"]
		}
		
		"/v1/login"(controller:"user"){
			action = [GET:"login"]
		}
		
		"/v1/users/$userId?"(controller:"user") {
			action = [GET:"getUserProfile", PUT:"updateUserProfile", DELETE:"deleteUserProfile" ]
		}	
		
		"/v1/users/$userId?/month/$month?"(controller:"user") {
			action = [GET:"getUserProfile"]
		}	
		
		"/v1/users/$userId?/getCoins"(controller:"user") {
			action = [GET:"getCoins"]
		}
		
		"/v1/users/$userId?/balance"(controller:"user") {
			action = [GET:"getBalance"]
		}
		
		"/v1/users/$userId?/history"(controller:"user") {
			action = [GET:"getUserHistoryGames"]
		}
		
		"/v1/users/$userId?/history/$gameId?/qs"(controller:"user") {
			action = [GET:"getUserHistoryQuestions"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/feature"(controller:"game"){
			action = [GET:"getFeatureGames"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/upcoming"(controller:"game"){
			action = [GET:"getUpcomingGames"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/past"(controller:"game"){
			action = [GET:"getPastGames"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/$gameId?"(controller:"game"){
			action = [GET:"getGame"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/$gameId?/ranking"(controller:"game"){
			action = [GET:"getGameRanking"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/$gameId?/qs"(controller:"question"){
			action = [GET:"listQuestions"]
		}		

		"/v1/sports/$sportType/leagues/$leagueType/games/$gameId?/qs/$qId?"(controller:"question"){
			action = [GET:"getQuestionDetails"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/wagers/new"(controller:"bet"){
			action = [POST:"placeBet"]
		}
		
		"/v1/sports/ranking"(controller:"Ranking"){
			action = [GET:"getRank"]
		}
		
		"/v1/sports/processgame"(controller:"game"){
			action = [GET:"processGame"]
		}
		
		"/v1/sports/testgame"(controller:"game"){
			action = [GET:"testGames"]
		}
		
		"/v1/admin/game/customquestion"(controller:"customQuestion"){
			action = [POST:"createCustomQuestion"]
		}
		
		"/v1/admin/game/customquestionresult"(controller:"customQuestion"){
			action = [POST:"createCustomQuestionResult"]
		}
		
		"/v1/admin/game/customgame"(controller:"customGame"){
			action = [POST:"createCustomGame"]
		}
		
		"/v1/admin/game/autocreatequestions"(controller:"question"){
			action = [GET:"createQuestions"]
		}
		
		"/v1/admin/simulatebet"(controller:"simulateBet"){
			action = [GET:"simulateBet"]
		}
		
		"/v1/admin/simulatecomment"(controller:"simulateBet"){
			action = [GET:"simulateComment"]
		}
		
		"/v1/admin/tournament/new"(controller:"tournament"){
			action = [POST:"createTournament"]
		}
		
		"/v1/admin/push/addChannel"(controller:"push"){
			action = [GET:"updateChannel"]
		}
		
		"/v1/admin/push/removeChannel"(controller:"push"){
			action = [GET:"removeChannel"]
		}
		
		"/v1/admin/push/getUser"(controller:"push"){
			action = [GET:"getUserInstallationID"]
		}
				
		"/v1/iap/getNonce"(controller:"IAP"){
			action = [POST:"getNonce"]
		}
		
		"/v1/iap/validateApple"(controller:"IAP"){
			action = [POST:"verifyApple"]
		}
		
		"/v1/iap/validateAndroid"(controller:"IAP"){
			action = [POST:"verifyAndroid"]
		}
		
		"/v1/iap/activatePurchse"(controller:"IAP"){
			action = [POST:"activateAndroid"]
		}
		
		
		"/v1/banners/generateCurrentTop"(controller:"UserBanner"){
			action = [GET:"generateCurrentMonthBanner"]
		}
		
		"/v1/banners/generatePastTop"(controller:"UserBanner"){
			action = [GET:"generatePastMonthTopBanner"]
		}
		
		"/v1/banners/clearPastCurrent"(controller:"UserBanner"){
			action = [GET:"clearPastCurrentBanners"]
		}
		
		"/v1/banners/getBanners"(controller:"UserBanner"){
			action = [GET:"getUserBanners"]
		}
		
		
//		"/v1/sports/all/tournament/list"(controller:"tournament"){
//			action = [GET:"listTournaments"]
//		}
		
		"/v1/sports/all/tournament/worldcup"(controller:"tournament"){
			action = [GET:"getWorldCupTournament"]
		}
		
		"/v1/sports/all/tournament/enroll/$tournamentId?"(controller:"tournament"){
			action = [GET:"enrollTournament"]
		}
		
		"/v1/sports/$sportType/leagues/$leagueType/games/$gameId?/qs/$qId?/comments"(controller:"comment") {
			action = [GET:"getExistingComments", POST:"writeComments"]
		}
		
		"/v1/users/$userId?/followers"(controller:"friendSystem") {
			action = [GET:"listFollowers"]
		}
		
		"/v1/users/$userId?/followings"(controller:"friendSystem") {
			action = [GET:"listFollowings", POST:"followRequest"]
		}
		
		"/v1/users/$userId?/isfollowing/$followingUserId?"(controller:"friendSystem") {
			action = [GET:"isFollowing"]
		}
		
		"/v1/test"(controller:"testServiceMethod") {
			action = [GET:"updateScore"]
		}

		"/v1/admin/cq"(view:"/CustomQuestionWebsite")
		
		"/v1/admin/qr"(view:"/CustomQuestionResultWebsite")
		"/v1/userprofilewebsite"(view:"/userprofile")
//        "/"(view:"/index")
        "500"(view:'/error')
	}
}
