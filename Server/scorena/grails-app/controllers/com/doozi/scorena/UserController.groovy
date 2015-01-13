package com.doozi.scorena

import grails.converters.JSON


// v1/sports/userprofile/{UserType or Status}/events

//eg. v1/sports/userprofile/newuser 
//eg. v1/sports/userprofile/existinguser
//eg. v1/sports/userprofile/superuser


//curl -i -X POST -H "Content-Type: application/json" -d '{"username":"mastermml8", "email":"masterml8@gmail.com", "password":"lkffd"}' localhost:8080/scorena/users/new

//curl -v -X GET  -G --data-urlencode 'username=michealLiu' --data-urlencode 'password=11111111' localhost:8080/scorena/v1/login

class UserController {
	def userService
	def rankingService
	def userHistoryService
	
	def list(){
		def user="this is testing user"
		render user as JSON
	}

	def socialNetworkUserPostSetup(){
		log.info "socialNetworkUserPostSetup(): begins..."
		
		println request.JSON
		if ( !request.JSON.sessionToken ){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "socialNetworkUserPostSetup(): result = ${result}"
			return
		}
		
		Map result = userService.createSocialNetworkUser(request.JSON.sessionToken)

		if (result.code){
			response.status = 404
			render result as JSON
			log.error "socialNetworkUserPostSetup(): result = ${result}"
			return 
		}
		response.status = 201
		render result as JSON
		log.info "socialNetworkUserPostSetup(): ends with result = ${result}"
	}
	
	//curl -i -v -X POST -H "Content-Type: application/json" -d '{"username":"candiceli", "email":"candi@gmail.com", "password":"asdfasdf", "gender":"female", "region":"Toronto"}' localhost:8080/scorena/v1/users/new
	def createNewUser(){
		log.info "createNewUser(): begins..."
		
		if (!request.JSON.username || !request.JSON.email|| !request.JSON.password){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "createNewUser(): result = ${result}"
			return
		}
		
		//def image = request.getFile('profilePicture')
		
		def resp = userService.createUser(request.JSON.username, request.JSON.email, request.JSON.password, request.JSON.gender, request.JSON.region)
		
		if (resp.code){
			response.status =400
			render resp as JSON
			log.error "createNewUser(): resp = ${resp}"
			return
		}

		response.status = 201
		render resp as JSON
		log.info "createNewUser(): ends with resp = ${resp}"
	}
	
	//curl -v -X GET  -G --data-urlencode 'username=Ming' --data-urlencode 'password=11111111' localhost:8080/scorena/v1/login
	def login(){
		log.info "login(): begins..."
		
//		println params
		if (!params.username||!params.password){
			response.status = 404
			def result = [error: "invalid parameters"]			
			render result as JSON
			log.error "login(): result = ${result}"
			return
		}
		
		def resp = userService.login(params.username.decodeURL().toLowerCase(), params.password.decodeURL())
		if (resp.code){
			response.status =400
			render resp as JSON
			log.error "login(): resp = ${resp}"
			return
		}
		render resp as JSON
		log.info "login(): ends with resp = ${resp}"
	}
	
	def passwordReset(){
		log.info "passwordReset(): begins..."
		println request.JSON.toString()
		
		if (!request.JSON.email){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "passwordReset(): result = ${result}"
			return
		}
		
		def resp = userService.passwordReset(request.JSON.email.toString())
		if (resp.code){
			response.status =400
			render resp as JSON
			log.error "passwordReset(): resp = ${resp}"
			return
		}
		render resp as JSON
		log.info "passwordReset(): ends with resp = ${resp}"
	}
	
	def deleteUserProfile(){
		log.info "deleteUserProfile(): begins..."
		
		String sessionToken = request.getHeader("sessionToken")
		
		if (!sessionToken||!params.userId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "deleteUserProfile(): result = ${result}"
			return
		}
		
		def resp = userService.deleteUser(sessionToken, params.userId.toString())
		if (resp.code){
			response.status =400
			render resp as JSON
			log.error "deleteUserProfile(): resp = ${resp}"
			return
		}
		render resp as JSON
		log.info "deleteUserProfile(): ends with resp = ${resp}"
	}
	
	
	/*curl -v -X GET localhost:8080/scorena/v1/users/R3yN1lprBc
	or
	
	curl -v -X GET localhost:8080/scorena/v1/users/R3yN1lprBc/month/November
	
	or 
	curl -v -X GET localhost:8080/scorena/v1/users/R3yN1lprBc/month/11
	
	or 
	curl -v -X GET localhost:8080/scorena/v1/users/R3yN1lprBc/month/1
	
	or 
	curl -v -X GET localhost:8080/scorena/v1/users/R3yN1lprBc/month/01
	*/
	def getUserProfile(){
		log.info "getUserProfile(): begins..."
		
		if (!params.userId){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "getUserProfile(): result = ${result}"
			return
		}
		
		def resp = userService.getUserProfile(params.userId.toString(),params.month)
		if (resp.code){
			response.status =400
			render resp as JSON
			log.error "getUserProfile(): resp = ${resp}"
			return
		} 
		render resp as JSON
		log.info "getUserProfile(): ends with resp = ${resp}"
	}
	
	
	//curl -i -v -X PUT -H "sessionToken:jqxy6cutose3wemz1ko5731hg"  -H "Content-Type: application/json" -d '{"email":"ming@gmail.com"}' localhost:8080/scorena/v1/users/tSftavHQP6
	def updateUserProfile(){
		log.info "updateUserProfile(): begins..."
		
		String sessionToken = request.getHeader("sessionToken")

		
		if (!sessionToken||!params.userId || !request.JSON){
			response.status = 404
			def result = [error: "invalid parameters"]
			render result as JSON
			log.error "updateUserProfile(): result = ${result}"
			return
		}
		
		if (request.JSON.username){
			response.status = 404
			def result = [error: "username cannot be updated"]
			render result as JSON
			log.error "updateUserProfile(): result = ${result}"
			return
		}
		
		def resp = userService.updateUserProfile(sessionToken, params.userId.toString(), request.JSON as JSON)
		
		if (resp.code){
			response.status =400
			render resp as JSON
			log.error "updateUserProfile(): resp = ${resp}"
			return
		}
		
		render resp as JSON
		log.info "updateUserProfile(): ends with resp = ${resp}"
		
	}
	
	//deprecated 
	def getRanking(){
		log.info "getRanking(): begins..."

		if (!params.userId || params.userId==""){
			response.status = 404
			def result = [error: "userId is required"]
			render result as JSON
			log.error "getRanking(): result = ${result}"
			return
		}
		
		Map rankingResult = rankingService.getRanking(params.userId)
		if (rankingResult.code){
			response.status = 404
			render rankingResult as JSON
			log.error "getRanking(): rankingResult = ${rankingResult}"
			return
		}
		render rankingResult as JSON
		log.info "getRanking(): ends with rankingResult = ${rankingResult}"
		
	}
	
	def getCoins(){
		log.info "getCoins(): begins..."
		
		if (!params.userId){
			response.status = 404
			def result = [error: "userId is required"]
			render result as JSON
			log.error "getCoins(): result = ${result}"
			return
		}
		
		def getCoinsResult = userService.getCoins(params.userId)
		if (getCoinsResult.code){
			response.status = 404
			render getCoinsResult as JSON
			log.error "getCoins(): getCoinsResult = ${getCoinsResult}"
			return
		}
		render getCoinsResult as JSON
		log.info "getCoins(): ends with getCoinsResult = ${getCoinsResult}"
		return
	}
	
	def getBalance(){
		log.info "getBalance(): begins..."
		
		if (!params.userId){
			response.status = 404
			def result = [error: "userId is required"]
			render result as JSON
			log.error "getBalance(): result = ${result}"
			return
		}
		def balanceResult = userService.getUserBalance(params.userId)
		if (balanceResult.code){
			response.status = 404
			render balanceResult as JSON
			log.error "getBalance(): balanceResult = ${balanceResult}"
			return
		}
		render balanceResult as JSON
		log.info "getBalance(): balanceResult = ${balanceResult}"
	}
	
//	def show(User user){
//		if (user == null){
//			render status:404
//		}else{
//			render user as JSON
//		}
//	}
//	
//	def list(){
//		render User.list() as JSON
//	}
//	def save() {}
//	def update() {}
	
	def getUserHistoryGames() {
		log.info "getUserHistoryGames(): begins..."
		
		if (!params.userId){
			response.status = 404
			def result = [error: "userId is required"]
			render result as JSON
			log.error "getUserHistoryGames(): result = ${result}"
			return
		}
		
		def userId = params.userId
		def userHistoryGames = userHistoryService.listBettedGames(userId)
		render userHistoryGames as JSON
		
		log.info "getUserHistoryGames(): ends with userHistoryGames = ${userHistoryGames}"
		
		return
	} 
	
	def getUserHistoryQuestions() {
		log.info "getUserHistoryQuestions(): begins..."
		
		if (!params.userId){
			response.status = 404
			def result = [error: "userId is required"]
			render result as JSON
			log.error "getUserHistoryQuestions(): result = ${result}"
			return
		}
		
		if (!params.gameId){
			response.status = 404
			def result = [error: "gameId is required"]
			render result as JSON
			log.error "getUserHistoryQuestions(): result = ${result}"
			return
		}
		
		def userId = params.userId
		def gameId = params.gameId
		
		def userHistoryQuestions = userHistoryService.listBettedQuestions(userId, gameId)
		render userHistoryQuestions as JSON
		
		log.info "getUserHistoryQuestions(): ends with userHistoryQuestions = ${userHistoryQuestions}"
		
		return
	}
	
	def handleException(Exception e) {
		render e.toString()
		log.info "${e.toString()}"
	}

}