package com.doozi.scorena.transactionprocessengine

import grails.transaction.Transactional
import com.doozi.scorena.*

@Transactional
class NewGameResultFetcherService {
	def sportsDataService
    def printNowTime(def time) {
		//println "current time is "+time
    }
	
	def getUnprocessedPastGame(){
		println "NewGameResultFetcherService::getUnprocessedPastGame(): starts"
		def pastGames = sportsDataService.getPastEplMatches()
		def earliestPastGameDate = pastGames.get(0).date
		def processGameRecords = GameProcessRecord.findAllByStartDateTimeGreaterThanEquals(earliestPastGameDate)
		def gameRecordAdded = 0
		for (def pastGame: pastGames){
			def unprocessed = true
			
			for (def gameRecord: processGameRecords){
				if (gameRecord.eventKey == pastGame.gameId){
					unprocessed = false
					break	
				}
			}	
			
			if (unprocessed == true){
				println "NewGameResultFetcherService::getUnprocessedPastGame(): Event " + pastGame.gameId + " is added to the GameProcessRecord table"
				def newProcessRecord = new GameProcessRecord(eventKey: pastGame.gameId, transProcessStatus: 0, startDateTime: pastGame.date, lastUpdate: new Date())
				newProcessRecord.save()		
				gameRecordAdded++
			}
		}
		println "NewGameResultFetcherService::getUnprocessedPastGame(): ends"
		return gameRecordAdded
			
	}	
}

