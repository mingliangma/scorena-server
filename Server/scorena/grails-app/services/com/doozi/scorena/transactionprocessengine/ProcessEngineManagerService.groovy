package com.doozi.scorena.transactionprocessengine

import org.springframework.transaction.annotation.Transactional


class ProcessEngineManagerService {
	static transactional = false
	def newGameResultFetcherService
	def processEngineImplService
	def sportsDataService
	def customGameService
	def notificationService
	
    def startProcessEngine() {
		log.info "startProcessEngine(): begins with " + new Date()
		println "ProcessEngineManagerService::startProcessEngine(): starts at "+new Date()
		
		//Get past games from both sports DB game table and Scorena DB custom game table
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = customGameService.getAllPastGames()
		List pastTennisGames = sportsDataService.getAllPastTennisGames()
		List pastMLBGames = sportsDataService.getAllPastMLBGames()
		List pastNBADraftGames = sportsDataService.getAllPastNBADraftGames()
		List pastChampGames = sportsDataService.getAllPastChampionGames()
		
		List allPastGames = []
		allPastGames.addAll(pastCustomGames)
		allPastGames.addAll(pastGames)
		allPastGames.addAll(pastTennisGames)
		allPastGames.addAll(pastMLBGames)
		allPastGames.addAll(pastNBADraftGames)
		allPastGames.addAll(pastChampGames)
		
		def gameRecordAdded = newGameResultFetcherService.getUnprocessedPastGame(allPastGames)
		Map processResult = processEngineImplService.processNewGamesPayout()
		processEngineImplService.processNewGamesScore()
		def gameRecordsProcessed = processResult.gameRecordsProcessed
		notificationService.gameResultNotification(processResult.userTotalGamesProfit, processResult.gameIdToGameInfoMap)
		
		def result = [gameRecordAdded:gameRecordAdded, gameRecordsProcessed:gameRecordsProcessed]
		log.info "startProcessEngine(): result = ${result}"
		log.info "startProcessEngine(): ends at "+ new Date()
		return result
    }
	
	def surveyProcessEngine(){
		log.info "ProcessEngineManagerService::surveyProcessEngine(): starts at "+new Date()
		processEngineImplService.processUserSruvey("nbacustomevent-survey-530531")
		log.info "surveyProcessEngine(): ends at "+ new Date()
	}
}
