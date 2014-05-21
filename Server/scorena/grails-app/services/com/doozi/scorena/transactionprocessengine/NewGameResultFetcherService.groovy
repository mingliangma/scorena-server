package com.doozi.scorena.transactionprocessengine

import grails.transaction.Transactional
import com.doozi.scorena.*

@Transactional
class NewGameResultFetcherService {
	def sportsDataService
	def customGameService
	def helperService
    def printNowTime(def time) {
		//println "current time is "+time
    }
	
	def getUnprocessedPastGame(){
		println "NewGameResultFetcherService::getUnprocessedPastGame(): starts"
		List pastGames = sportsDataService.getAllPastGames()
		List pastCustomGames = customGameService.getAllPastGames()
		List allPastGames = []
		allPastGames.addAll(pastCustomGames)
		
		def earliestPastGameDate = helperService.parseDateFromString(pastGames.get(pastGames.size()-1).date)
		
		//todo: need to find the earliest custom games
		def earliestPastCustomGameDate = helperService.parseDateFromString(pastCustomGames.get(pastCustomGames.size()-1).date)
		def ealiestGameDate
		if (earliestPastGameDate > earliestPastCustomGameDate){
			ealiestGameDate = earliestPastCustomGameDate
		}else{
			ealiestGameDate = earliestPastGameDate
		}
		
		def processGameRecords = GameProcessRecord.findAllByStartDateTimeGreaterThanEquals(ealiestGameDate)
		
		def gameRecordAdded = 0
		for (def pastGame: allPastGames){
			def unprocessed = true
			
			for (def gameRecord: processGameRecords){
				if (gameRecord.eventKey == pastGame.gameId){
					unprocessed = false
					break	
				}
			}	
			
			if (unprocessed == true){
				println "NewGameResultFetcherService::getUnprocessedPastGame(): Event " + pastGame.gameId + " is added to the GameProcessRecord table"
				def newProcessRecord = new GameProcessRecord(eventKey: pastGame.gameId, transProcessStatus: 0, startDateTime: helperService.parseDateFromString(pastGame.date), lastUpdate: new Date())
				newProcessRecord.save()		
				gameRecordAdded++
			}
		}
		println "NewGameResultFetcherService::getUnprocessedPastGame(): ends"
		return gameRecordAdded
			
	}	
}

