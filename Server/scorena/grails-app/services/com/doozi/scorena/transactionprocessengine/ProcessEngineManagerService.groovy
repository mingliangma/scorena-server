package com.doozi.scorena.transactionprocessengine

import org.springframework.transaction.annotation.Transactional


class ProcessEngineManagerService {
	static transactional = false
	def newGameResultFetcherService
	def processEngineImplService
	def sportsDataService
	def customGameService
	def pushService
	
    def startProcessEngine() {
		log.info "startProcessEngine(): begins with " + new Date()
		println "ProcessEngineManagerService::startProcessEngine(): starts at "+new Date()
		
		//Get past games from both sports DB game table and Scorena DB custom game table
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = customGameService.getAllPastGames()
		
		def gameRecordAdded = newGameResultFetcherService.getUnprocessedPastGame(pastGames, pastCustomGames)
		Map processResult = processEngineImplService.processNewGamesPayout()
		processEngineImplService.processNewGamesScore()
		def gameRecordsProcessed = processResult.gameRecordsProcessed
		pushService.sendEndGamePush(processResult.userTotalGamesProfit, processResult.gameIdToGameInfoMap)
		
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
