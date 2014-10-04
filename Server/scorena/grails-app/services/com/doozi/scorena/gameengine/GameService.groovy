package com.doozi.scorena.gameengine

import com.doozi.scorena.Question;

import grails.transaction.*

@Transactional
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
	
	List listUpcomingGames(){
		List upcomingGames = sportsDataService.getAllUpcomingGames()
		List upcomingCustomGames = customGameService.getAllUpcomingGames()
		List upcomingGamesResult=[]		
		upcomingGamesResult.addAll(upcomingCustomGames)
		upcomingGamesResult.addAll(upcomingGames)
		return upcomingGamesResult
	}
	
	List listUpcomingGames(def userId){
				
		List upcomingGamesResult=listUpcomingGames()
		
		def playedGames = betTransactionService.listDistinctBetEventKeyByUserId(userId)
		
		for (def upcomingGame: upcomingGamesResult){
			upcomingGame.userInfo = gameUserInfoService.getUpcomingGamesUserInfo(upcomingGame.gameId, playedGames, userId)
		}
		return upcomingGamesResult
	}
	
	def listPastGames(){
		List pastGames = sportsDataService.getAllPastGames()	
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult=[]	
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		return pastGamesResult
	}
	
	def listPastGames(def userId){

		List pastGamesResult=listPastGames()
		
		def playedGames = betTransactionService.listDistinctBetEventKeyByUserId(userId)

		for (def pastGame: pastGamesResult){
			def gameId = pastGame.gameId
			if (pastGame.gameStatus != "post-event"){
				println "gameService::listPastGames():wrong event: "+ pastGame
			}
			pastGame.userInfo=gameUserInfoService.getPastGamesUserInfo(pastGame.gameId, playedGames, userId)			
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
}
