package com.doozi.scorena.gameengine

import com.doozi.scorena.Question;
import com.doozi.scorena.transaction.BetTransaction

import grails.transaction.*

class GameService {
	
	static transactional = false
	
	def sportsDataService
	def questionService
	def betTransactionService
	def helperService
	def customGameService
	def getUpcomingGamesUserInfo
	def gameUserInfoService
	
	public static final String POSTEVENT = "post-event"
	public static final String PREEVENT = "pre-event"
	public static final String INTERMISSION = "intermission"
	public static final String MIDEVENT = "mid-event"
	
	List listUpcomingNonCustomGames(){
		List upcomingGames = sportsDataService.getAllUpcomingGames()
		List upcomingGamesResult=[]
		upcomingGamesResult.addAll(upcomingGames)
		return upcomingGamesResult
	}
	
	List listUpcomingGamesData(){
		List upcomingGames = sportsDataService.getAllUpcomingGames()
		List upcomingCustomGames = customGameService.getAllUpcomingGames()
		List upcomingGamesResult=[]		
		upcomingGamesResult.addAll(upcomingCustomGames)
		upcomingGamesResult.addAll(upcomingGames)
		return upcomingGamesResult
	}
	
	List listUpcomingGames(def userId){
				
		List upcomingGamesResult=listUpcomingGamesData()		
		List upcomingGameIds = getGameIdsFromGameData(upcomingGamesResult)
		List<BetTransaction> betsInUpcomingGames = betTransactionService.listBetTransByGameIds(upcomingGameIds)
		
		
		for (def upcomingGame: upcomingGamesResult){
			List<BetTransaction> allBetsInGame = getAllBetsByGameId(upcomingGame.gameId, betsInUpcomingGames)
			upcomingGame.numPeople = getNumUsersInGame(allBetsInGame)
			if (userId != null){
				List<BetTransaction> userBetsInTheGame = getUserBetsFromGame(userId, allBetsInGame)
				upcomingGame.userInfo = gameUserInfoService.getUpcomingGamesUserInfo(upcomingGame.gameId, userBetsInTheGame, userId)
			}
			
		}
		return upcomingGamesResult
	}
	
	def listPastGamesData(){
		List pastGames = sportsDataService.getAllPastGames()	
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult=[]	
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		return pastGamesResult
	}
	
	def listPastGames(def userId){

		List pastGamesResult=listPastGamesData()
		List pastGameIds = getGameIdsFromGameData(pastGamesResult)
		List<BetTransaction> betsInPastGames = betTransactionService.listBetTransByGameIds(pastGameIds)

		for (def pastGame: pastGamesResult){
			if (pastGame.gameStatus != "post-event"){
				println "gameService::listPastGames():wrong event: "+ pastGame
			}
			List<BetTransaction> allBetsInGame = getAllBetsByGameId(pastGame.gameId, betsInPastGames)
			pastGame.numPeople = getNumUsersInGame(allBetsInGame)
			if (userId != null){
				List<BetTransaction> userBetsInTheGame = getUserBetsFromGame(userId, allBetsInGame)
				pastGame.userInfo=gameUserInfoService.getPastGamesUserInfo(pastGame.gameId, userBetsInTheGame, userId)
			}				
		}
		return pastGamesResult
	}
	
	def listFeatureGames(userId){
		List featureGames = questionService.listFeatureQuestions(userId)
		return featureGames
	}
	
	def listFeatureGames(){
		List featureGames = questionService.listFeatureQuestions()
		return featureGames
	}
	
	Map getGame(String gameId){
		
		if (gameId.startsWith(customGameService.CUSTOM_EVENT_PREFIX))
			return customGameService.getGame(gameId)
		else	
			return sportsDataService.getGame(gameId)
		
	}
	
	private List<String> getGameIdsFromGameData(List games){
		List gameIds = []
		for (Map game: games){
			gameIds.add(game.gameId)
		}
		return gameIds
	}
	
	private int getNumUsersInGame(List<BetTransaction> userBetsInGame){
		Map userMap =[:]
		for (BetTransaction bet: userBetsInGame){
			if (!userMap.containsKey(bet.account.id)){
				userMap.put(bet.account.id, "")
			}
		}
		return userMap.size()
	}
	
	private List<BetTransaction> getAllBetsByGameId(String gameId, List<BetTransaction> betsInGames){
		List<BetTransaction> allBetsInTheGame = []
		for (BetTransaction bet: betsInGames){
			if (bet.eventKey == gameId){
				allBetsInTheGame.add(bet)
			}
		}
		return allBetsInTheGame
	}
	
	private List<BetTransaction> getUserBetsFromGame(String userId, List<BetTransaction> betsInGames){
		List<BetTransaction> userBetsInTheGame = []
		for (BetTransaction bet: betsInGames){
			if (bet.account.userId == userId){
				userBetsInTheGame.add(bet)
			}
		}
		return userBetsInTheGame
	}
}
