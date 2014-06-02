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
		Date earliestPastGameDate = new Date()
		Date earliestPastCustomGameDate = new Date()
		Date ealiestGameDate
		
		allPastGames.addAll(pastCustomGames)
		allPastGames.addAll(pastGames)

		if (pastGames.size()>0)
			earliestPastGameDate = helperService.parseDateFromString(pastGames.get(pastGames.size()-1).date)		
		
		//todo: need to find the earliest custom games by date
		if (pastCustomGames.size()>0)	
			earliestPastCustomGameDate = helperService.parseDateFromString(pastCustomGames.get(0).date)	
		
		if (earliestPastGameDate > earliestPastCustomGameDate){
			ealiestGameDate = earliestPastCustomGameDate
		}else{
			ealiestGameDate = earliestPastGameDate
		}
		
		println "ealiestGameDate: "+ealiestGameDate
		
		List processGameRecords = GameProcessRecord.findAllByStartDateTimeGreaterThanEquals(ealiestGameDate-2)

		def gameRecordAdded = 0
		for (Map pastGame: allPastGames){
			boolean unprocessed = true
			
			for (GameProcessRecord gameRecord: processGameRecords){
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

