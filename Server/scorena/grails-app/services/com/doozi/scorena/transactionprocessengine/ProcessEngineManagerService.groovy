package com.doozi.scorena.transactionprocessengine

import grails.transaction.Transactional


class ProcessEngineManagerService {
	static transactional = false
	def newGameResultFetcherService
	def processEngineImplService
	
    def startProcessEngine() {
		println "ProcessEngineManagerService::startProcessEngine(): starts at "+new Date()
		newGameResultFetcherService.getUnprocessedPastGame()
		processEngineImplService.processGamePayout()
		println "ProcessEngineManagerService::startProcessEngine(): ends"
    }
}
