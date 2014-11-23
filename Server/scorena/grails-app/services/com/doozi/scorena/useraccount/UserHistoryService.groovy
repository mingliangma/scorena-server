package com.doozi.scorena.useraccount

import java.util.List;

import grails.transaction.Transactional

import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.score.AbstractScore

@Transactional
class UserHistoryService {

	def sportsDataService
	def customGameService
	def betTransactionService
	def scoreService
	def gameUserInfoService
	
	def listPastGamesData(){
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult = []
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		return pastGamesResult
	}
	
	def listPastGames(def userId){
		if (userId == null) {
			return [message:"invalid userId!(UserHistoryService::listPastGames)"]
		}
		
		List userHistoryGames = []
		List pastGamesResult = listPastGamesData()
		List pastGameIds = getGameIdsFromGameData(pastGamesResult)
		List<BetTransaction> betsInPastGames = betTransactionService.listBetTransByGameIds(pastGameIds)
		
		List<AbstractScore> metalScoreTransactionList = scoreService.listBadgeScoresByUserIdAndPastGames(userId, pastGameIds)
		
		println "metalScoreTransactionList size = "+metalScoreTransactionList.size()
		
		for (def pastGame: pastGamesResult){
			if (pastGame.gameStatus != "post-event"){
				println "gameService::listPastGames():wrong event: "+ pastGame
			}
			
			List<BetTransaction> allBetsInGame = getAllBetsByGameId(pastGame.gameId, betsInPastGames)
			pastGame.numPeople = getNumUsersInGame(allBetsInGame)
			
			AbstractScore scoreTransaction = getScoreTransactionByGameId(pastGame.gameId, metalScoreTransactionList)
		
			List<BetTransaction> userBetsInTheGame = getUserBetsFromGame(userId, allBetsInGame)				
			pastGame.userInfo = gameUserInfoService.getPastGamesUserInfo(pastGame, userBetsInTheGame, userId, scoreTransaction)
			
			if (pastGame.userInfo.placedBet == true) {
				userHistoryGames.add(pastGame)
			}
		}
		return userHistoryGames
	}
	
	private List<String> getGameIdsFromGameData(List games){
		List gameIds = []
		for (Map game: games){
			gameIds.add(game.gameId)
		}
		return gameIds
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
	
	private int getNumUsersInGame(List<BetTransaction> userBetsInGame){
		Map userMap =[:]
		for (BetTransaction bet: userBetsInGame){
			if (!userMap.containsKey(bet.account.id)){
				userMap.put(bet.account.id, "")
			}
		}
		return userMap.size()
	}
	
	private AbstractScore getScoreTransactionByGameId(String eventKey, List<AbstractScore> metalScoreTransactionList){
		for (AbstractScore scoreTransaction: metalScoreTransactionList){
			if (scoreTransaction.eventKey == eventKey){
				return scoreTransaction
			}
		}
		return null
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
