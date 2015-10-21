package com.doozi.scorena.transactionprocessengine

import org.springframework.transaction.annotation.Transactional
import com.doozi.scorena.transaction.*


class ProcessEngineManagerService {
	static transactional = false
	def newGameResultFetcherService
	def processEngineImplService
	def sportsDataService
	def customGameService
	def notificationService
	def challengeService
	def gameService
	
    def startProcessEngine() {
		log.info "startProcessEngine(): begins with " + new Date()
		
		//Get past games from both sports DB game table and Scorena DB custom game table
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = customGameService.getAllPastGames()
		List pastTennisGames = sportsDataService.getAllPastTennisGames()
		List pastMLBGames = sportsDataService.getAllPastMLBGames()
		List pastNBADraftGames = sportsDataService.getAllPastNBADraftGames()
		List pastChampGames = sportsDataService.getAllPastChampionGames()
		List pastCFLGames = sportsDataService.getAllPastCFLGames()
		
		List allPastGames = []
		allPastGames.addAll(pastCustomGames)
		allPastGames.addAll(pastGames)
		allPastGames.addAll(pastTennisGames)
		allPastGames.addAll(pastMLBGames)
		allPastGames.addAll(pastNBADraftGames)
		allPastGames.addAll(pastChampGames)
		allPastGames.addAll(pastCFLGames)
		
		def gameRecordAdded = newGameResultFetcherService.getUnprocessedPastGame(allPastGames)
		Map processResult = processEngineImplService.processNewGamesPayout()
		processEngineImplService.processNewGamesScore()
		def gameRecordsProcessed = processResult.gameRecordsProcessed
		
		int challengeUpdatecounter = challengeService.processChallenge(processResult.userTotalGamesProfit)
		notificationService.gameResultNotification(processResult.userTotalGamesProfit, processResult.gameIdToGameInfoMap)
		
		def result = [gameRecordAdded:gameRecordAdded, gameRecordsProcessed:gameRecordsProcessed, challengeUpdatecounter:challengeUpdatecounter]
		log.info "startProcessEngine(): result = ${result}"
		log.info "startProcessEngine(): ends at "+ new Date()
		return result
    }
	
	
	def surveyProcessEngine(){
		log.info "ProcessEngineManagerService::surveyProcessEngine(): starts at "+new Date()
		processEngineImplService.processUserSruvey("nbacustomevent-survey-530531")
		log.info "surveyProcessEngine(): ends at "+ new Date()
	}
	
	def processNBAFinalGames(){
		String gameId = "nba2014-0041400406"
		
		List<PayoutTransaction> payoutTransctions = PayoutTransaction.findAllByEventKey(gameId)
		Map userIdToProfit = [:]
		for (PayoutTransaction pt: payoutTransctions){
			if (userIdToProfit[pt.account.userId]){
				int totalProfit = userIdToProfit[pt.account.userId] 
				userIdToProfit[pt.account.userId] = totalProfit + pt.profit
			}else{
				userIdToProfit[pt.account.userId] = pt.profit
			}
		}
		Map userTotalGamesProfit = [:]
		userTotalGamesProfit[gameId] = userIdToProfit
		println userTotalGamesProfit
		
		Map game = gameService.getGame(gameId)
		Map gameIdToGameInfoMap = [:]
		gameIdToGameInfoMap[gameId] = game
		
		notificationService.gameResultNotification(userTotalGamesProfit, gameIdToGameInfoMap)
	}
	
}
