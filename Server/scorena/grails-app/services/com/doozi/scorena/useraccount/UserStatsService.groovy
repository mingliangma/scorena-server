package com.doozi.scorena.useraccount

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.doozi.scorena.transaction.LeagueTypeEnum;
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.score.AbstractScore

import grails.transaction.Transactional

import com.doozi.scorena.utils.*
//@Transactional
class UserStatsService {

def sportsDataService
def helperService
Map getUserStats(def userScores,def userPayoutTrans,String month, def account){
	Map userStats = getAllStats(userScores,userPayoutTrans, month,account.id)
//	Map userStats = getBetStats(userPayoutTrans, account.id)
	//todo netgain/total wager in type 1
	
	int netGainPercentageDenominator = 1
	if (account.currentBalance > 0){
		netGainPercentageDenominator = account.currentBalance
	}
	
//	userStats.weekly.netGainPercent = ((userStats.weekly.netGain / (netGainPercentageDenominator))*100).toInteger()
	userStats.monthly.netGainPercent = ((userStats.monthly.netGain / (netGainPercentageDenominator))*100).toInteger()
	userStats.all.netGainPercent = ((userStats.all.netGain / (netGainPercentageDenominator))*100).toInteger()
	return userStats
}

/*
 * Returns all user stats in a JSON string; includes data for total stats and monthly stat based on current month or specified month
 * 
 * [totalScore:[total:val, leagues: [leaguename[Score:val] ], ...   ]
 *  monthScore:[total:val, leagues: [leaguename[Score:val] ], ...   ]
 * totalMedal:[question:val, bronze:val, silver:val, gold:val ,leagues:[leaguename[question:val, bronze:val, silver:val, gold:val]], ... ]
 * monthMedal:[question:val, bronze:val, silver:val, gold:val ,leagues:[leaguename[question:val, bronze:val, silver:val, gold:val]], ... ]
 * all:[netGain:val, wins:val, losses:val, ties:val, leagues:[leaguename[netGain:val, wins:val, losses:val, ties:val]],...]
 * monthly:[netGain:val, wins:val, losses:val, ties:val, leagues:[leaguename[netGain:val, wins:val, losses:val, ties:val]],...]
 * ]
 * 
 */
def getAllStats(def userScores,def userPayoutTrans, def month, def accountId)
{
	def firstOfMonth
	def lastOfMonth
	
	// month check
	if(month == null || month == "")
	{
		 firstOfMonth = helperService.getFirstDateOfCurrentMonth()
		 lastOfMonth = helperService.getLastDateOfCurrentMonth()
		 month = helperService.getMonth()
	}
	else
	{
		 firstOfMonth = helperService.getFirstOfMonth(month)
		 lastOfMonth = helperService.getLastOfMonth(month)
	}
	
	// if an incorrect month is entered then first and last are taken as the current month
	if (firstOfMonth == null || lastOfMonth == null )
	{
		firstOfMonth = helperService.getFirstDateOfCurrentMonth()
		lastOfMonth = helperService.getLastDateOfCurrentMonth()
		month = helperService.getMonth()
	}

	
	def gameStats = [totalScore:[total:0,leagues:getLeagueScores(userScores)],
		totalMedal:[question:0,bronze:0,silver:0,gold:0,leagues:getLeagueMedals(userScores)],
		all:[netGain:0, wins:0, losses:0, ties:0, leagues:getLeagueStats(accountId)],
		month:helperService.getMonthString(month, firstOfMonth),
		monthScore:[total:0,leagues:getLeagueScoresMonth(userScores,firstOfMonth,lastOfMonth)],
		monthMedal:[question:0,bronze:0,silver:0,gold:0,leagues:getLeagueMedalsMonth(userScores,firstOfMonth,lastOfMonth)],
		monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:getLeagueStats(accountId)]
	//	weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:getLeagueStats(accountId)]
		]
	
	// Payout data check and Score data check
	if (userPayoutTrans==null || userPayoutTrans.size()==0 || userScores==null || userScores.size() ==0 )
	{
		return gameStats
	}
	
	// loops through scores data and updates map accordingly
	for (AbstractScore scoreStat: userScores)
	{
		// calculates total score and total medals for map
		gameStats.totalScore.total += scoreStat.score
				
				switch(scoreStat.class.getSimpleName())
				{
					case "QuestionScore":
					gameStats.totalMedal.question += 1
					break;
					
					case "BronzeMetalScore":
					gameStats.totalMedal.bronze += 1
					break;
					
					case "SilverMetalScore":
					gameStats.totalMedal.silver += 1
					break;
					
					case "GoldMetalScore":
					gameStats.totalMedal.gold += 1
					break;
				}
				
				// calculates total score and medals by month
				if (scoreStat.createdAt > firstOfMonth && scoreStat.createdAt < lastOfMonth )
				{
					gameStats.monthScore.total += scoreStat.score

					switch(scoreStat.class.getSimpleName())
					{
						case "QuestionScore":
						gameStats.monthMedal.question += 1
						break;
						
						case "BronzeMetalScore":
						gameStats.monthMedal.bronze += 1
						break;
						
						case "SilverMetalScore":
						gameStats.monthMedal.silver += 1
						break;
						
						case "GoldMetalScore":
						gameStats.monthMedal.gold += 1
						break;
					}
				}
	}
	
	// loops through payout data and  updates map accordingly
	for (PayoutTransaction tran: userPayoutTrans)
	{
		if (tran.createdAt > firstOfMonth && tran.createdAt < lastOfMonth)
		{
			gameStats.monthly.netGain+=(tran.transactionAmount - tran.initialWager)
			gameStats.all.netGain+=(tran.transactionAmount - tran.initialWager)
			if (tran.winnerPick==0){
				gameStats.monthly.ties+=1
				gameStats.all.ties+=1
				continue
			}else if(tran.winnerPick != tran.pick){
				gameStats.monthly.losses+=1
				gameStats.all.losses+=1
			}else if (tran.winnerPick == tran.pick){
				gameStats.monthly.wins+=1
				gameStats.all.wins+=1

			}else{
				println "ERROR: UserService::getBetStats(): should not go in here"
			}
			continue
		}
		gameStats.all.netGain+=(tran.transactionAmount - tran.initialWager)
		if (tran.winnerPick==0){
			gameStats.all.ties+=1
			continue
		}else if(tran.winnerPick != tran.pick){
			gameStats.all.losses+=1
		}else if (tran.winnerPick == tran.pick){
			gameStats.all.wins+=1

		}else{
			println "ERROR: UserService::getBetStats(): should not go in here"
		}
	}
	
	return gameStats
	
}


def getBetStats(userPayoutTrans, accountId){
		
//		def stats = [all:[netGain:0, wins:0, losses:0, ties:0, leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]], 
//			monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]], 
//			weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:[premier:[netGain:0,wins:0,netLose:0,losses:0,ties:0],champ:[netGain:0,wins:0,netLose:0,losses:0,ties:0],brazil:[netGain:0,wins:0,netLose:0,losses:0,ties:0]]]]
		
		def stats = [all:[netGain:0, wins:0, losses:0, ties:0, leagues:getLeagueStats(accountId)],
			monthly:[netGain:0, wins:0, losses:0, ties:0,leagues:getLeagueStats(accountId)],
			weekly:[netGain:0, wins:0, losses:0, ties:0,leagues:getLeagueStats(accountId)]]
		
		
		if (userPayoutTrans==null || userPayoutTrans.size()==0){
			return stats
		}
		
		def firstDateOfCurrentWeek = getFirstDateOfCurrentWeek()
		def firstDateOfCurrentMonth = getFirstDateOfCurrentMonth()
		
		for (PayoutTransaction tran: userPayoutTrans){
			
			if (tran.createdAt > firstDateOfCurrentWeek){
				
				stats.all.netGain += (tran.transactionAmount - tran.initialWager)
				stats.monthly.netGain+=(tran.transactionAmount - tran.initialWager)
				stats.weekly.netGain+=(tran.transactionAmount - tran.initialWager)
				if (tran.winnerPick==0){
					stats.all.ties+=1
					stats.monthly.ties+=1
					stats.weekly.ties+=1					
				}else if(tran.winnerPick != tran.pick){
					stats.all.losses+=1
					stats.monthly.losses+=1
					stats.weekly.losses+=1
				}else if (tran.winnerPick == tran.pick){
					stats.all.wins+=1
					stats.monthly.wins+=1
					stats.weekly.wins+=1					
									
				}else{
					println "ERROR: UserService::getBetStats(): should not go in here"
				}
				continue
			}
			
			if (tran.createdAt > firstDateOfCurrentMonth){
				stats.monthly.netGain+=(tran.transactionAmount - tran.initialWager)
				stats.all.netGain+=(tran.transactionAmount - tran.initialWager)
				if (tran.winnerPick==0){					
					stats.monthly.ties+=1
					stats.all.ties+=1
					continue
				}else if(tran.winnerPick != tran.pick){					
					stats.monthly.losses+=1
					stats.all.losses+=1
				}else if (tran.winnerPick == tran.pick){					
					stats.monthly.wins+=1
					stats.all.wins+=1										

				}else{
					println "ERROR: UserService::getBetStats(): should not go in here"
				}
				continue
			}
			stats.all.netGain+=(tran.transactionAmount - tran.initialWager)
			if (tran.winnerPick==0){				
				stats.all.ties+=1
				continue
			}else if(tran.winnerPick != tran.pick){
				stats.all.losses+=1
			}else if (tran.winnerPick == tran.pick){
				stats.all.wins+=1

			}else{
				println "ERROR: UserService::getBetStats(): should not go in here"
			}						
		}
		
		
		return stats
	}



// updates nested map of user score stats for each league user is present in, in total
	def getLeagueScores(def stats)
	{
		if(stats == null || stats.size==0)
		{
			return [score:0]
		}
		
		def scores = [:]
		for(AbstractScore scoreStat:stats)
		{
			def leagueName = sportsDataService.getLeagueCodeFromEventKey( scoreStat.getEventKey())
			def scoreMap = [:]
			scoreMap = scores.get(leagueName)
			if(scoreMap == null)
			{
				scoreMap = [score:0]
				scores.put(leagueName, scoreMap)
			}
				scoreMap.score += scoreStat.getScore()
		}
		return scores
	}
	
	// updates nested map of user score stats for each league user is present in, by specified month-first and last date
	def getLeagueScoresMonth(def stats, def first, def last)
	{
		if(stats == null || stats.size==0)
		{
			return [score:0]
		}
		
		def scores = [:]
		
		for(AbstractScore scoreStat:stats)
		{
			def leagueName = sportsDataService.getLeagueCodeFromEventKey( scoreStat.getEventKey())
			def scoreMap = [:]
			scoreMap = scores.get(leagueName)
			if(scoreMap == null)
			{
				scoreMap = [score:0]
				scores.put(leagueName, scoreMap)
			}
			
			if (scoreStat.getCreatedAt() > first && scoreStat.getCreatedAt() < last )
			{
				scoreMap.score += scoreStat.getScore()
			}
		}
		return scores
	}
	
	// updates nested map of user medal stats for each league user is present in, in total
	def getLeagueMedals(def stats)
	{
		if(stats == null || stats.size==0)
		{
			return [question:0, bronze:0, silver:0,gold:0]
		}
		def medals = [:]
		
		for(AbstractScore medalStat:stats)
		{
			def leagueName =  sportsDataService.getLeagueCodeFromEventKey( medalStat.getEventKey())
			def medalMap = [:]
			medalMap = medals.get(leagueName)
			if(medalMap == null)
			{
				medalMap = [question:0, bronze:0, silver:0,gold:0]
				medals.put(leagueName, medalMap)
			}
				switch(medalStat.class.getSimpleName())
				{
					case "QuestionScore":
					medalMap.question += 1
					break;
					
					case "BronzeMetalScore":
					medalMap.bronze += 1
					break;
					
					case "SilverMetalScore":
					medalMap.silver += 1
					break;
					
					case "GoldMetalScore":
					medalMap.gold += 1
					break;
				}
		}
		return medals
	}

	// updates nested map of user score stats for each league user is present in, by specified month- first and last date
	def getLeagueMedalsMonth(def stats, def first,def last)
	{
		if(stats == null || stats.size==0)
		{
			return [question:0, bronze:0, silver:0,gold:0]
		}
		def medals = [:]
		
		for(AbstractScore medalStat:stats)
		{
			def leagueName =  sportsDataService.getLeagueCodeFromEventKey( medalStat.getEventKey())
			def medalMap = [:]
			medalMap = medals.get(leagueName)
			if(medalMap == null)
			{
				medalMap = [question:0, bronze:0, silver:0,gold:0]
				medals.put(leagueName, medalMap)
			}
			
			if (medalStat.getCreatedAt() > first && medalStat.getCreatedAt() < last  )
			{
				switch(medalStat.class.getSimpleName())
				{
					case "QuestionScore":
					medalMap.question += 1
					break;
					
					case "BronzeMetalScore":
					medalMap.bronze += 1
					break;
					
					case "SilverMetalScore":
					medalMap.silver += 1
					break;
					
					case "GoldMetalScore":
					medalMap.gold += 1
					break;
				}
			}
		}
		return medals
	}
	
	

	def getLeagueStats(def accountId){
		def leagues=[:]
	
		List<UserLeagueStats> stats = getUserLeaguesStats(accountId)
		
		
		for (UserLeagueStats userStat : stats){
			def leagueName = userStat.getLeague()
			def leagueMap = [:] 
			leagueMap = leagues.get(leagueName)
			if (leagueMap==null){
				leagueMap = [netGain:0,wins:0,netLose:0,losses:0,ties:0]
				leagues.put(leagueName, leagueMap)
			}
			if (userStat.getPlayResult()==PickStatus.USER_LOST){
				int netGain 
				if (userStat.getNetGain()>0)
					netGain=userStat.getNetGain()
				else
					netGain=userStat.getNetGain()
					
				leagueMap.netLose=netGain
				leagueMap.losses=userStat.getNumGames()
			}
			
			if (userStat.getPlayResult()==PickStatus.USER_WON){
				int netGain
				if (userStat.getNetGain()>0)
					netGain=userStat.getNetGain()
				else
					netGain=userStat.getNetGain()
					
				leagueMap.netGain=netGain
				leagueMap.wins=userStat.getNumGames()
			}
			
			if (userStat.getPlayResult()==PickStatus.USER_TIE){					
				leagueMap.ties=userStat.getNumGames()
			}			
		}
		
		
		return leagues
	}

	private List<UserLeagueStats> getUserLeaguesStats(def accountId){
		String query = "select t.account.id, "+
		"substring(t.eventKey,1,12) AS league, "+
		"t.playResult, "+
		"(sum(t.transactionAmount) - sum(t.initialWager)) AS netGain, "+
		"count(t.account.id) AS numGames "+
		"from PayoutTransaction as t where t.account.id=? group by 1, 2,3"
		List userLeagueStatsFromDB = PayoutTransaction.executeQuery(query, [accountId])
		List<UserLeagueStats> stats = []
		
		for (List userStat : userLeagueStatsFromDB){
			String leagueName = sportsDataService.getLeagueCodeFromEventKey(userStat[1])			
			UserLeagueStats uls = new UserLeagueStats(userStat[0],leagueName, userStat[2], userStat[3], userStat[4])			
			stats.add(uls)			
		}
		
		return stats
	}	
}
