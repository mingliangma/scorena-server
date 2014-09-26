package com.doozi.scorena.useraccount

import java.util.List;
import java.util.Map;

import com.doozi.scorena.*

import grails.transaction.Transactional

//@Transactional
class RankingService {
	def parseService
	
	public static final int USERNAME_RANKING_QUERY_INDEX = 0
	public static final int USERID_RANKING_QUERY_INDEX = 1
	public static final int NETGAIN_RANKING_QUERY_INDEX = 2
	public static final int CURRENTBALANCE_RANKING_QUERY_INDEX =3
	
	
	Map getRanking(userId){
		
		//Get both all and weekly ranking from database. The query response is in the format of 
		//[[username, userId, getGain, currentBalance],[username, userId, getGain, currentBalance],...]
		//then the ranking is added to a rankingResultAll list of Map with format
		//[[userId: userId, username: username, gain: netGain, rank: rank], [userId: userId, username: username, gain: netGain, rank: rank], ...]
		
		String allRankingQuery = "SELECT a.username, a.userId, (sum(t.transactionAmount) - sum(t.initialWager)) AS netGain, "+
		"a.currentBalance from Account a, PayoutTransaction t where a.id = t.account.id " +
		"group by 1 order by netGain desc"
		
		String weeklyRankingQuery = "SELECT a.username, a.userId, (sum(t.transactionAmount) - sum(t.initialWager)) AS netGain, "+
			"a.currentBalance from Account a, PayoutTransaction t where a.id = t.account.id and week(t.createdAt,1) = week(curdate(),1) " +
			"group by 1 order by netGain desc"
			
			
		def userRankingAll = Account.executeQuery(allRankingQuery)
		def userRankingWk = Account.executeQuery(weeklyRankingQuery)
		
		List rankingResultAll =[]
		List rankingResultWk =[]
		
		int rankingAllSize = userRankingAll.size()
		int rankingWkSize = userRankingWk.size()

		//userIdMap is used to contain all the user Ids from both weekly and all ranking. 
		//The variable is later used to retrieve displayName and pictureURL from Parse
		Map userIdMap = [:]
		
		for (int i=0; i<rankingAllSize; i++){
			List rankEntry = userRankingAll[i]
			rankingResultAll.add(getAccountInfoMap(rankEntry[USERID_RANKING_QUERY_INDEX], rankEntry[USERNAME_RANKING_QUERY_INDEX]
				, rankEntry[NETGAIN_RANKING_QUERY_INDEX],i+1))
			if (!userIdMap.containsKey(rankEntry[USERID_RANKING_QUERY_INDEX])){
				userIdMap.put(rankEntry[USERID_RANKING_QUERY_INDEX], "")				
			}
		}
		
		for (int i=0; i<rankingWkSize; i++){
			List rankEntry = userRankingWk[i]
			rankingResultWk.add(getAccountInfoMap(rankEntry[USERID_RANKING_QUERY_INDEX], rankEntry[USERNAME_RANKING_QUERY_INDEX]
				, rankEntry[NETGAIN_RANKING_QUERY_INDEX],i+1))
			if (!userIdMap.containsKey(rankEntry[USERID_RANKING_QUERY_INDEX])){
				userIdMap.put(rankEntry[USERID_RANKING_QUERY_INDEX], "")
			}
		}
		
		//Retieve user profile from parse, then restructure the result data into UserProfileUserIdAsKeyMap Map with format
		// [(userId):userprofile] 
		Map userProfileResults = parseService.retrieveUserList(userIdMap)
		Map UserProfileUserIdAsKeyMap = getUserProfileUserIdAsKeyMap(userProfileResults.results)
		
		for (Map rankingAllEntry: rankingResultAll){
			String accountUserId = rankingAllEntry.userId
			Map userProfile = UserProfileUserIdAsKeyMap.get(accountUserId)

			rankingAllEntry.pictureURL = ""
			
			if (userProfile != null){
				if (userProfile.display_name != null && userProfile.display_name != "")
					rankingAllEntry.username = userProfile.display_name
			
				if (userProfile.pictureURL != null && userProfile.pictureURL != "")
					rankingAllEntry.pictureURL = userProfile.pictureURL					
			}
			
		}
		
		for (Map rankingWkEntry: rankingResultWk){
			String accountUserId = rankingWkEntry.userId
			Map userProfile = UserProfileUserIdAsKeyMap.get(accountUserId)
					
			rankingWkEntry.pictureURL = ""
			if (userProfile != null){
				if (userProfile.display_name != null && userProfile.display_name != "")
					rankingWkEntry.username = userProfile.display_name
			
				if (userProfile.pictureURL != null && userProfile.pictureURL != "")
					rankingWkEntry.pictureURL = userProfile.pictureURL
			}
		}
		
		return [weekly: rankingResultWk, all: rankingResultAll]
	}
	
	private Map getAccountInfoMap(String userId, String username, long netgain, int rank){
		String netGain = ""
		if (netgain>0)
			netGain="+"+netgain.toString()
		else
			netGain=netgain.toString()
			
		return [userId: userId, username: username, gain: netGain, rank: rank]
	}
	
	private Map getUserProfileUserIdAsKeyMap(List userProfileList){
		Map UserProfileUserIdAsKeyMap = [:]
		for (Map userProfile: userProfileList){
			UserProfileUserIdAsKeyMap.put(userProfile.objectId, userProfile)
		}
		return UserProfileUserIdAsKeyMap
	}
}
