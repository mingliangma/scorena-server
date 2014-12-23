package com.doozi.scorena.ranking

import java.util.List;
import java.util.Map;

import com.doozi.scorena.*
import com.doozi.scorena.score.AbstractScore

import org.springframework.transaction.annotation.Transactional

//@Transactional
class RankingService {
	def parseService
	def scoreService
	
	public static final int USERNAME_RANKING_QUERY_INDEX = 0
	public static final int USERID_RANKING_QUERY_INDEX = 1
	public static final int NETGAIN_RANKING_QUERY_INDEX = 2
	public static final int CURRENTBALANCE_RANKING_QUERY_INDEX =3
	
	
	Map getRanking(userId){
		log.info "getRanking(): begins with userId = ${userId}"
		
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
		
		def result = [weekly: rankingResultWk, all: rankingResultAll]
		log.info "getRanking(): ends with result = ${result}"
		
		return [weekly: rankingResultWk, all: rankingResultAll]
	}
	
	List getGameRanking(String gameId){
		log.info "getGameRankin(): begins with gameId = ${gameId}"
		
		List<AbstractScore> metalScoreTransactionList = scoreService.listBadgeScoresByGameId(gameId)
		
		if (metalScoreTransactionList.size() ==0){
			return []
		}
		
		List gameRanking =[]
		Map userIdMap = [:]
		for (AbstractScore score: metalScoreTransactionList){
			gameRanking.add(getAccountInfoMap(score.account.userId, score.account.username, score.profitCollected, score.rank, score.class.getSimpleName()))
			userIdMap.put(score.account.userId, "")
		}
		
		//Retieve user profile from parse, then restructure the result data into UserProfileUserIdAsKeyMap Map with format
		// [(userId):userprofile]
		Map userProfileResults = parseService.retrieveUserList(userIdMap)
		Map UserProfileUserIdAsKeyMap = getUserProfileUserIdAsKeyMap(userProfileResults.results)
		
		for (Map gameRankingEntry: gameRanking){
			String accountUserId = gameRankingEntry.userId
			Map userProfile = UserProfileUserIdAsKeyMap.get(accountUserId)

			gameRankingEntry.pictureURL = ""
			
			if (userProfile != null){
				if (userProfile.display_name != null && userProfile.display_name != "")
					gameRankingEntry.username = userProfile.display_name
			
				if (userProfile.pictureURL != null && userProfile.pictureURL != "")
					gameRankingEntry.pictureURL = userProfile.pictureURL
			}
			
		}
		
		log.info "getGameRankin(): ends with gameRanking = ${gameRanking}"
		
		return gameRanking
	}
	public Map getAccountInfoMap(String userId, String username, long netgain, int rank, String badge){
		String netGain = ""
		if (netgain>0)
			netGain="+"+netgain.toString()
		else
			netGain=netgain.toString()
			
		return [userId: userId, username: username, gain: netGain, rank: rank, badge: badge]
	}
	
	public Map getAccountInfoMap(String userId, String username, long netgain, int rank){
		String netGain = ""
		if (netgain>0)
			netGain="+"+netgain.toString()
		else
			netGain=netgain.toString()
			
		return [userId: userId, username: username, gain: netGain, rank: rank]
	}
	
	public Map getUserProfileUserIdAsKeyMap(List userProfileList){
		Map UserProfileUserIdAsKeyMap = [:]
		for (Map userProfile: userProfileList){
			UserProfileUserIdAsKeyMap.put(userProfile.objectId, userProfile)
		}
		return UserProfileUserIdAsKeyMap
	}
}
