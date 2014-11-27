package com.doozi.scorena.useraccount

import java.util.List;

import grails.transaction.Transactional

import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.Question
import com.doozi.scorena.Account

import grails.plugins.rest.client.RestBuilder

/**
 * show user's betting game & question history
 * @author HDJ
 *
 */
@Transactional
class UserHistoryService {

	def sportsDataService
	def customGameService
	def betTransactionService
	def scoreService
	def gameUserInfoService
	def gameService
	def questionService
	
	/**
	 * show all past game
	 * @return all past game
	 */
	def listPastGamesData(){
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult = []
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		return pastGamesResult
	}
	
	/**
	 * show user's all past bet games 
	 * @param userId
	 * @return list of past bet games
	 */
	def listBettedGames(def userId){
		if (userId == null) {
			return [message:"invalid userId!(UserHistoryService::listPastGames)"]
		}
		
		List userHistoryGames = []
		List pastGamesResult = listPastGamesData()
		List pastGameIds = gameService.getGameIdsFromGameData(pastGamesResult)
		List<BetTransaction> betsInPastGames = betTransactionService.listBetTransByGameIds(pastGameIds)
		
		List<AbstractScore> metalScoreTransactionList = scoreService.listBadgeScoresByUserIdAndPastGames(userId, pastGameIds)
		
		println "metalScoreTransactionList size = "+metalScoreTransactionList.size()
		
		for (def pastGame: pastGamesResult){
			if (pastGame.gameStatus != "post-event"){
				println "userHistoryService::listPastGames():wrong event: "+ pastGame
			}
			
			List<BetTransaction> allBetsInGame = gameService.getAllBetsByGameId(pastGame.gameId, betsInPastGames)
			pastGame.numPeople = gameService.getNumUsersInGame(allBetsInGame)
			
			AbstractScore scoreTransaction = gameService.getScoreTransactionByGameId(pastGame.gameId, metalScoreTransactionList)
		
			List<BetTransaction> userBetsInTheGame = gameService.getUserBetsFromGame(userId, allBetsInGame)				
			pastGame.userInfo = gameUserInfoService.getPastGamesUserInfo(pastGame, userBetsInTheGame, userId, scoreTransaction)
			
			if (pastGame.userInfo.placedBet == true) {
				userHistoryGames.add(pastGame)
			}
		}
		return userHistoryGames
	}
	
	/**
	 * show user's betted questions
	 * @param gameId
	 * @return list of details of betted questions
	 */
	def listBettedQuestions(def userId, def gameId) {
		def questionDetailsList = []
		def userBettedQuestionsDetailsList = []
		
		questionDetailsList = questionService.listQuestionsWithPoolInfo(gameId, userId)
		if (questionDetailsList != null && questionDetailsList != "") {
			for (def questionDetails: questionDetailsList) {
				if (questionDetails.userInfo != null && questionDetails.userInfo != "") {
					if (questionDetails.userInfo.placedBet == true) {
						userBettedQuestionsDetailsList.add(questionDetails)
					}
				}
			}
		}
		
		return userBettedQuestionsDetailsList
	}
	
}
