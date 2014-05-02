package com.doozi.scorena.transactionprocessengine

import grails.transaction.Transactional


class ProcessEngineManagerService {
	static transactional = false
	def newGameResultFetcherService
	def processEngineImplService
	
    def startProcessEngine() {
		println "ProcessEngineManagerService::startProcessEngine(): starts at "+new Date()
		def gameRecordAdded = newGameResultFetcherService.getUnprocessedPastGame()
		def gameRecordsProcessed = processEngineImplService.processGamePayout()
		println "ProcessEngineManagerService::startProcessEngine(): ends"
		return [gameRecordAdded:gameRecordAdded, gameRecordsProcessed:gameRecordsProcessed]
    }
}
