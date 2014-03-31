package com.doozi.scorena.transactionProcessEngine

import grails.transaction.Transactional

@Transactional
class NewGameResultFetcherService {

    def printNowTime(def time) {
		println "current time is "+time
    }
}

