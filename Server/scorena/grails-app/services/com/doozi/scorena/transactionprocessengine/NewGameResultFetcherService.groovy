package com.doozi.scorena.transactionprocessengine

import org.springframework.transaction.annotation.Transactional
//import com.doozi.scorena.*
import com.doozi.scorena.processengine.*

//@Transactional
class NewGameResultFetcherService {
	def sportsDataService
	def customGameService
	def helperService
    def printNowTime(def time) {
		//println "current time is "+time
    }
	
	
	/**
	 * Search for new past games that needed to process payout to players. These games are added to the GameProcessRecord. 
	 * 
	 * @return the number of game payout records added to the GameProcessRecord table that need to be processed
	 */
	def getUnprocessedPastGame(List pastGames, List pastCustomGames){
		log.info "getUnprocessedPastGame(): begins with pastGames = ${pastGames}, pastCustomGames = ${pastCustomGames}"
		println "NewGameResultFetcherService::getUnprocessedPastGame(): starts"
		
		List allPastGames = []
		allPastGames.addAll(pastCustomGames)
		allPastGames.addAll(pastGames)
		
		//Compare the pastGames and pastCustomGames list and calculate the earliest date of a game that is in both list.
		Date ealiestGameDate = calculateEarliestGameDate(pastGames, pastCustomGames)		
		
		//get all Game process records that are greater than ealiestGameDate
		List processGameRecords = GameProcessRecord.findAllByStartDateTimeGreaterThanEquals(ealiestGameDate-2)

		//add a new game process record of the past game if it is not exist in the GameProcessRecord table
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
				def newProcessRecord = new GameProcessRecord(eventKey: pastGame.gameId, transProcessStatus: TransactionProcessStatusEnum.NOT_PROCESSED, 
					scoreProcessStatus: ScoreProcessStatusEnum.NOT_PROCESSED, startDateTime: helperService.parseDateFromString(pastGame.date), lastUpdate: new Date(), errorMessage: "")
				if (!newProcessRecord.save(flush: true)){
					newProcessRecord.errors.each {
						println it
					}
				}	
				gameRecordAdded++
			}
		}
		println "NewGameResultFetcherService::getUnprocessedPastGame(): ends"
		log.info "getUnprocessedPastGame(): ends with gameRecordAdded = ${gameRecordAdded}"
		return gameRecordAdded
			
	}
	
	/**
	 * Compare the pastGames and pastCustomGames list and calculate the earliest date of a game that is in both list.
	 * @param pastGames
	 * @param pastCustomGames
	 * @return earliest game date
	 */
	private Date calculateEarliestGameDate(List pastGames, List pastCustomGames){
		log.info "calculateEarliestGameDate(): begins with pastGames = ${pastGames}, pastCustomGames = ${pastCustomGames}"
		
		Date earliestPastGameDate = new Date()
		Date earliestPastCustomGameDate = new Date()
		Date ealiestGameDate
		
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
		
		log.info "calculateEarliestGameDate(): ends with ealiestGameDate = ${ealiestGameDate}"
		
		return ealiestGameDate
	}
}

