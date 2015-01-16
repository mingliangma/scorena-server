package com.doozi.scorena.transactionprocessengine

import org.springframework.transaction.annotation.Transactional


class ProcessEngineManagerService {
	static transactional = false
	def newGameResultFetcherService
	def processEngineImplService
	def sportsDataService
	def customGameService
	
    def startProcessEngine() {
		log.info "startProcessEngine(): begins with " + new Date()
		println "ProcessEngineManagerService::startProcessEngine(): starts at "+new Date()
		
		//Get past games from both sports DB game table and Scorena DB custom game table
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = customGameService.getAllPastGames()
		
		def gameRecordAdded = newGameResultFetcherService.getUnprocessedPastGame(pastGames, pastCustomGames)
		def gameRecordsProcessed = processEngineImplService.processNewGamesPayout()
		processEngineImplService.processNewGamesScore()
		def result = [gameRecordAdded:gameRecordAdded, gameRecordsProcessed:gameRecordsProcessed]
		println "ProcessEngineManagerService::startProcessEngine(): result =  "+result
		log.info "startProcessEngine(): result = ${result}"
		println "ProcessEngineManagerService::startProcessEngine(): ends at "+ new Date()
		log.info "startProcessEngine(): ends at "+ new Date()
		return result
    }
}
