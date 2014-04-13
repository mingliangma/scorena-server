package com.doozi.scorena.transactionprocessengine

import grails.transaction.Transactional

@Transactional
class NewGameResultFetcherService {

    def printNowTime(def time) {
		println "current time is "+time
    }
	
	
}

