package com.doozi.scorena.useraccount

import java.util.List;

import org.springframework.transaction.annotation.Transactional

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
		log.info "listPastGamesData(): begins..."
		
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = 	customGameService.getAllPastGames()
		List pastGamesResult = []
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		
		log.info "listPastGamesData(): ends with pastGamesResult = ${pastGamesResult}"
		
		return pastGamesResult
	}
	
	/**
	 * show user's all past bet games 
	 * @param userId
	 * @return list of past bet games
	 */
	def listBettedGames(def userId){
		log.info "listBettedGames(): begins with userId = ${userId}"
		
		if (userId == null) {
			log.error "listBettedGames(): invalid userId!"
			return [message:"invalid userId!(UserHistoryService::listPastGames)"]
		}
		List userHistoryGames = []
		List pastGames = gameService.listPastGames(userId, "all", "all")
		for (Map pastGame : pastGames){
			if (pastGame.userInfo.placedBet == true) {
				userHistoryGames.add(pastGame)
			}
		}
		
		log.info "listBettedGames(): ends with userHistoryGames = ${userHistoryGames}"
		
		return userHistoryGames
	}
	
	/**
	 * show user's bet questions
	 * @param gameId
	 * @param usrId
	 * @return list of details of bet questions
	 */
	def listBettedQuestions(def userId, def gameId) {
		log.info "listBettedQuestions(): begins with userId = ${userId}, gameId = ${gameId}"
		
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
		
		log.info "listBettedQuestions(): ends with userBettedQuestionsDetailsList = ${userBettedQuestionsDetailsList}"
		return userBettedQuestionsDetailsList
	}
	
}
