package com.doozi.scorena.useraccount

import java.util.List;

import com.doozi.scorena.transaction.PayoutTransaction
import grails.transaction.Transactional

@Transactional
class UserStatsService {

Map getUserStats(userPayoutTrans, accountId){
	Map userStats = userStatsService.getBetStats(userPayoutTrans, account.id)
	//todo netgain/total wager in type 1
	
	int netGainPercentageDenominator = 1
	if (account.currentBalance > 0){
		netGainPercentageDenominator = account.currentBalance
	}
	
	userStats.weekly.netGainPercent = ((userStats.weekly.netGain / (netGainPercentageDenominator))*100).toInteger()
	userStats.monthly.netGainPercent = ((userStats.monthly.netGain / (netGainPercentageDenominator))*100).toInteger()
	userStats.all.netGainPercent = ((userStats.all.netGain / (netGainPercentageDenominator))*100).toInteger()
	return
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

	def getLeagueStats(accountId){
		def leagues=[:]
	
		List<UserLeagueStats> stats = getUserLeaguesStats(accountId)
		
		
		for (UserLeagueStats userStat : stats){
			def leagueName = userStat.getLeague()
			def leagueMap = [:] 
			leagueMap = leagues.get(leagueName)
			if (leagueMap==null){
				leagueMap = [netGain:"0",wins:0,netLose:"0",losses:0,ties:0]
				leagues.put(leagueName, leagueMap)
			}
			if (userStat.getPlayResult()==PickStatus.USER_LOST){
				String netGain = ""
				if (userStat.getNetGain()>0)
					netGain="+"+userStat.getNetGain().toString()
				else
					netGain=userStat.getNetGain().toString()
					
				leagueMap.netLose=netGain
				leagueMap.losses=userStat.getNumGames()
			}
			
			if (userStat.getPlayResult()==PickStatus.USER_WON){
				String netGain = ""
				if (userStat.getNetGain()>0)
					netGain="+"+userStat.getNetGain().toString()
				else
					netGain=userStat.getNetGain().toString()
					
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
			String leagueName = sportsDataService.getLeagueNameFromEventKey(userStat[1])			
			UserLeagueStats uls = new UserLeagueStats(userStat[0],leagueName, userStat[2], userStat[3], userStat[4])			
			stats.add(uls)			
		}
		
		return stats
	}
}
