package com.doozi.scorena.score

import java.util.Date;

import com.doozi.scorena.Account
import com.doozi.scorena.utils.PickStatus
import com.doozi.scorena.Question
import com.doozi.scorena.processengine.GameProcessRecord
import com.doozi.scorena.transaction.LeagueTypeEnum
import com.doozi.scorena.transaction.PayoutTransaction

import grails.transaction.Transactional

//@Transactional
class ScoreService {
	
	def sportsDataService
	
	List<AbstractScore> listBadgeScoresByUserIdAndPastGames(String userId, List eventKeys){
		String query = "from AbstractScore as s where (s.class=com.doozi.scorena.score.NoMetalScore or s.class=com.doozi.scorena.score.GoldMetalScore "+
		"or s.class=com.doozi.scorena.score.SilverMetalScore or s.class=com.doozi.scorena.score.BronzeMetalScore) and s.account.userId=:userId and s.eventKey in (:eventKeys)"
				
		List<AbstractScore> metalScoreTransactionList = AbstractScore.findAll(query, [userId:userId,eventKeys: eventKeys])
	}
	
	List<AbstractScore> listBadgeScoresByGameId(String eventKey){
		
		
		String query = "from AbstractScore as s where (s.class=com.doozi.scorena.score.NoMetalScore or s.class=com.doozi.scorena.score.GoldMetalScore "+
		"or s.class=com.doozi.scorena.score.SilverMetalScore or s.class=com.doozi.scorena.score.BronzeMetalScore) and s.eventKey=:eventKey order by s.rank"
				
		List<AbstractScore> metalScoreTransactionList = AbstractScore.findAll(query, [eventKey: eventKey])
	}

    def processQuestionScore(List<PayoutTransaction> pTransactionList, GameProcessRecord gameProcessRecord) {
		println "processQuestionScore() starts, with pTransactionList size="+pTransactionList.size()
		for (PayoutTransaction ptransaction: pTransactionList){
			//win
			if (ptransaction.playResult == PickStatus.USER_WON){
				insertQuestionScore(ScoreConstant.QUESTION_SCORE, new Date(), ptransaction.eventKey, ptransaction.account, ptransaction.question, gameProcessRecord.startDateTime)
			}				
		}
		println "processQuestionScore() ends"				
    }
	
	def processGoldSilverBronzeScore(List<PayoutTransaction> pTransactionList, GameProcessRecord gameProcessRecord) {
		println "processGoldSilverBronzeScore() starts, with pTransactionList size="+pTransactionList.size()
		int tSize = pTransactionList.size()
		if (tSize <=0)
			return
			
		String eventKey = pTransactionList[0].eventKey
		
		Map profitMap = [:]
		Map accountMap = [:]
		for (PayoutTransaction ptransaction : pTransactionList){

			int accountId =  ptransaction.account.id
			
			if (profitMap.get(accountId)){
				int profit = profitMap.get(accountId).get("totalProfit")
				profit =  profit + ptransaction.profit
				
				int totalWager = profitMap.get(accountId).get("totalWager")
				totalWager =  totalWager + ptransaction.initialWager
				
				profitMap.putAt(accountId, ["totalProfit":profit, "totalWager":totalWager])
			}else{

				profitMap.putAt(accountId, ["totalProfit":ptransaction.profit, "totalWager":ptransaction.initialWager])
				accountMap.putAt(accountId, ptransaction.account)
			}
		}
		
		int numPeople = accountMap.size()
		int GoldLastTransactionIndex = 0
		int SilverLastTransactionIndex = 1
		int BronzeLastTransactionIndex = 2		
		if ((ScoreConstant.GOLD_TOP_PERCENTAGE/100) * numPeople >= 2){
			GoldLastTransactionIndex = Math.floor((ScoreConstant.GOLD_TOP_PERCENTAGE/100) * numPeople)-1
			SilverLastTransactionIndex = Math.floor((ScoreConstant.SILVER_TOP_PERCENTAGE/100) * numPeople)-1
			BronzeLastTransactionIndex = Math.floor((ScoreConstant.BRONZE_TOP_PERCENTAGE/100) * numPeople)-1
		}
		
//		
//		println "GoldLastTransactionIndex="+GoldLastTransactionIndex
//		println "SilverLastTransactionIndex="+SilverLastTransactionIndex
//		println "BronzeLastTransactionIndex="+BronzeLastTransactionIndex

		//sort by map values in reverse. eg [c:3, b:2, a:1]
		Map sortedProfitMap = profitMap.sort {a, b -> 

			if (a.value.totalProfit < b.value.totalProfit){
				return 1
			}else if (a.value.totalProfit > b.value.totalProfit){
				return -1
			}else{
				if (a.value.totalWager < b.value.totalWager){
					return 1
				}else if (a.value.totalWager > b.value.totalWager){
					return -1
				}else{
					return 0
				}
			}
		}

		int lastGoldProfit = 0
		int lastGoldWager = 0
		
		int lastSilverProfit = 0
		int lastSilverWager = 0
		
		int lastBronzeProfit = 0
		int lastBronzeWager = 0
		
		
		sortedProfitMap.eachWithIndex() { it, k->
			
			int rank = k+1

			//it.key is accountId, it.value is profit			
			if (k <= GoldLastTransactionIndex || (lastGoldProfit == it.value.totalProfit && lastGoldWager == it.value.totalWager)){			
				insertGoldScore(ScoreConstant.GOLD_SCORE, new Date(), eventKey, accountMap[it.key], rank/numPeople*100, it.value.totalProfit, gameProcessRecord.startDateTime, rank)			
				lastGoldProfit = it.value.totalProfit
				lastGoldWager = it.value.totalWager
				
//				println "===================GOLD==================="
//				println "top percentage=" +rank/numPeople*100
//				println "profit="+ it.value.totalProfit
//				println "=========================================="
			}else if (k <= SilverLastTransactionIndex || (lastSilverProfit == it.value.totalProfit && lastSilverWager == it.value.totalWager)){
				
				insertSilverScore(ScoreConstant.SILVER_SCORE, new Date(), eventKey, accountMap[it.key], rank/numPeople*100, it.value.totalProfit, gameProcessRecord.startDateTime, rank)
				lastSilverProfit = it.value.totalProfit
				lastSilverWager = it.value.totalWager
			
//				println "===================SILVER==================="
//				println "top percentage=" +rank/numPeople*100
//				println "profit="+ it.value.totalProfit
//				println "=========================================="
				
			} else if (k <= BronzeLastTransactionIndex || (lastBronzeProfit == it.value.totalProfit && lastBronzeWager == it.value.totalWager)){
			
				insertBronzeScore(ScoreConstant.BRONZE_SCORE, new Date(), eventKey, accountMap[it.key], rank/numPeople*100, it.value.totalProfit, gameProcessRecord.startDateTime, rank)
				lastBronzeProfit = it.value.totalProfit
				lastBronzeWager = it.value.totalWager
				
//				println "===================BRONZE==================="
//				println "top percentage=" +rank/numPeople*100
//				println "profit="+ it.value.totalProfit
//				println "=========================================="
			}else{
				insertNoScore(ScoreConstant.NO_SCORE, new Date(), eventKey, accountMap[it.key], rank/numPeople*100, it.value.totalProfit, gameProcessRecord.startDateTime, rank)
			}
		}
		println "processGoldSilverBronzeScore() ends"
	}
	
	private def insertGoldScore(int score, Date createdAt, String eventKey, Account account, BigDecimal topPercentage, int profitCollected,  Date gameStartTime, int rank){
		
		LeagueTypeEnum l = sportsDataService.getLeagueCodeFromEventKey(eventKey)
		
		GoldMetalScore gs = new GoldMetalScore(gameStartTime:gameStartTime, league:sportsDataService.getLeagueCodeFromEventKey(eventKey), score: score, 
			createdAt: createdAt, eventKey:eventKey, topPercentage:topPercentage, profitCollected:profitCollected, rank: rank)

		account.addToScore(gs)
		
		int result = 0
		if (!account.save()){
			account.errors.each {
		        println it
		    }		
 			result = -1
		}
		
		return result
	}
	
	private def insertSilverScore(int score, Date createdAt, String eventKey, Account account, BigDecimal topPercentage, int profitCollected,  Date gameStartTime, int rank){
		
		SilverMetalScore ss = new SilverMetalScore(gameStartTime:gameStartTime, league:sportsDataService.getLeagueCodeFromEventKey(eventKey), score: score, 
			createdAt: createdAt, eventKey:eventKey, topPercentage:topPercentage, profitCollected:profitCollected, rank: rank)
		
		account.addToScore(ss)
		
		int result = 0
		if (!account.save()){
		    account.errors.each {
		        println it
		    }			
			result = -1
		}
		
		return result
	}
	
	private def insertBronzeScore(int score, Date createdAt, String eventKey, Account account, BigDecimal topPercentage, int profitCollected,  Date gameStartTime, int rank){
		BronzeMetalScore bs = new BronzeMetalScore(gameStartTime:gameStartTime, league:sportsDataService.getLeagueCodeFromEventKey(eventKey), score: score, 
			createdAt: createdAt, eventKey:eventKey, topPercentage:topPercentage, profitCollected:profitCollected, rank: rank)
			
		account.addToScore(bs)
		
		int result = 0
		if (!account.save()){
		    account.errors.each {
		        println it
		    }
			result = -1
		}
		
		return result
	}
	
	private def insertNoScore(int score, Date createdAt, String eventKey, Account account, BigDecimal topPercentage, int profitCollected,  Date gameStartTime, int rank){
		
		LeagueTypeEnum l = sportsDataService.getLeagueCodeFromEventKey(eventKey)
		
		NoMetalScore ns = new NoMetalScore(gameStartTime:gameStartTime, league:sportsDataService.getLeagueCodeFromEventKey(eventKey), score: score,
			createdAt: createdAt, eventKey:eventKey, topPercentage:topPercentage, profitCollected:profitCollected, rank: rank)

		account.addToScore(ns)
		
		int result = 0
		if (!account.save()){
			account.errors.each {
				println it
			}
			 result = -1
		}
		
		return result
	}
	
	private def insertQuestionScore(int score, Date createdAt, String eventKey, Account account, Question question, Date gameStartTime){
		
		QuestionScore qs = new QuestionScore(score: score, createdAt: createdAt, eventKey:eventKey, gameStartTime:gameStartTime, league:sportsDataService.getLeagueCodeFromEventKey(eventKey))
		account.addToScore(qs)
		question.addToScore(qs)
		
		int result = 0
		if (!account.save()){
			    account.errors.each {
			        println it
			    }			
				result = -1
		}
		
		if (!question.save()){
			    question.errors.each {
			        println it
			    }			
				result = -1
		}
		
		return result
	}
}
