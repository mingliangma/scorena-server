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
		def result = [gameRecordAdded:gameRecordAdded, gameRecordsProcessed:gameRecordsProcessed]
		println "ProcessEngineManagerService::startProcessEngine(): result =  "+result
		println "ProcessEngineManagerService::startProcessEngine(): ends"
		return result
    }
}
