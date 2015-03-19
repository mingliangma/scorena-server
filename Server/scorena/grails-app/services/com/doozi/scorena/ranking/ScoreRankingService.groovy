package com.doozi.scorena.ranking
import java.util.List;
import java.util.Map;

import com.doozi.scorena.*
import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.score.QuestionScore
import com.doozi.scorena.transaction.LeagueTypeEnum

import com.doozi.scorena.utils.*

import grails.converters.JSON
import grails.validation.ValidationException
import org.springframework.transaction.annotation.Transactional

import org.codehaus.groovy.grails.web.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


//The ranking page service
class ScoreRankingService {
	def parseService
	def sportsDataService
	def helperService
	def friendSystemService
	
	public static final int USERID_RANKING_QUERY_INDEX = 0
	public static final int USER_SCORE_RANKING_QUERY_INDEX = 1
	public static final int LEAGUE_RANKING_QUERY_INDEX = 2
	
	public static final int RANKING_TYPE_ALL = 0
	public static final int RANKING_TYPE_MONTH = 1
	public static final int RANKING_TYPE_LEAGUE = 2
	public static final int RANKING_TYPE_MONTH_AND_LEAGUE = 3
	public static final int RANKING_TYPE_MONTH_AND_LEAGUE_AND_USER = 4
	public static final int RANKING_TYPE_MONTH_AND_USER = 5
	public static final int RANKING_TYPE_TOURNAMENT = 6

	private def getRanking(int rankingType, String month, List<LeagueTypeEnum> league, String userId, long tournamentId){
		log.info "getRanking(): begins with rankingType=${rankingType}, month=${month}, league=${league}, userId=${userId}"
		
		def userRanking 
		// searches database
		switch (rankingType) {
			case RANKING_TYPE_ALL:
				userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score), league from AbstractScore as s group by s.account.id order by sum(score) desc")
				break
			case RANKING_TYPE_MONTH:
				String dateRangeQueryString = getRankingDateString(month)
				userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score), league from AbstractScore as s where s.gameStartTime between "+dateRangeQueryString+" group by s.account.id order by sum(score) desc")
				break
			case RANKING_TYPE_LEAGUE:
				userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score), league from AbstractScore as s where s.league in (:leagues) group by s.account.id order by sum(score) desc", 
					[leagues: league])
				break
			case RANKING_TYPE_MONTH_AND_LEAGUE:
				String dateRangeQueryString = getRankingDateString(month)
				userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score), league from AbstractScore as s where s.gameStartTime between "+dateRangeQueryString+" AND s.league in (:leagues) group by s.account.id order by sum(score) desc", 
					[leagues: league])
				break
			case RANKING_TYPE_MONTH_AND_LEAGUE_AND_USER:
				String dateRangeQueryString = getRankingDateString(month)
				userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score), league from AbstractScore as s where s.gameStartTime between "+dateRangeQueryString+" AND s.league in (:leagues) AND s.account.userId=(:userId) group by s.account.id order by sum(score) desc",
					[leagues: league, userId: userId])
				break
			case RANKING_TYPE_MONTH_AND_USER:
				String dateRangeQueryString = getRankingDateString(month)
				userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score), league from AbstractScore as s where s.gameStartTime between "+dateRangeQueryString+" AND s.account.userId=(:userId) group by s.account.id order by sum(score) desc",
					[userId: userId])
				break
			case RANKING_TYPE_TOURNAMENT:
				userRanking = AbstractScore.executeQuery("SELECT e.account.userId, "+ 
									"(SELECT (CASE WHEN SUM(score) = null THEN 0 ELSE SUM(score) END) FROM AbstractScore s WHERE "+
										"s.account.id = e.account.id AND "+
										"s.gameStartTime > (CASE WHEN e.tournament.startDate > e.enrollmentDate THEN e.tournament.startDate ELSE e.enrollmentDate END) AND "+
										"s.gameStartTime < e.tournament.expireDate AND "+
										"s.league IN (select sl.leagueName from SubscribedLeague as sl where sl.tournament.id = (:tournamentId))) AS tscore "+
								"FROM Enrollment AS e where e.tournament.id = (:tournamentId) AND e.enrollmentStatus = 'ENROLLED' " + 
								"ORDER BY tscore desc",
								[tournamentId: tournamentId])
				break
		}

		
		// return user ranking
		log.info "getRanking(): ends with userRankingSize = ${userRanking.size()}"
		return userRanking
//		return returnScores(userRanking,month,league,rankingType)
	}
	
	// gets the overall ranking of all users 
	Map getAllRanking()
	{
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_ALL, null, null, null, 0)
		return constructScoresRankingResponse(scoreRanking,null,null, RANKING_TYPE_ALL)		
	}
	
	Map getAllRanking(String userId)
	{		
		Map rankingResponse =  getAllRanking()
		return constructRankingWithFollowingIndicatorResponse(userId, rankingResponse, "rankScores")
		
	}
	
	// gets the user ranking by month based on the month and year supplied  
	Map getRankingByMonth(String month)
	{	
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_MONTH, month, null, null, 0)
		return constructScoresRankingResponse(scoreRanking,month,null, RANKING_TYPE_MONTH)
	}

	Map getRankingByMonth(String userId, String month)
	{
		Map rankingResponse =  getRankingByMonth(month)
		return constructRankingWithFollowingIndicatorResponse(userId, rankingResponse, "rankScores")
		
	}
	// gets the user ranking by league
	Map getRankingByLeague(String league)
	{
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_LEAGUE, null, [sportsDataService.getLeagueEnumFromLeagueString(league)], null, 0)
		return constructScoresRankingResponse(scoreRanking,null, league, RANKING_TYPE_LEAGUE)
	}
	
	Map getRankingByLeague(String userId, String league)
	{
		Map rankingResponse =  getRankingByLeague(league)
		return constructRankingWithFollowingIndicatorResponse(userId, rankingResponse, "rankScores")
		
	}
	// gets user ranking by league and month
	Map getRankingByLeagueAndMonth(String month,String league)
	{	
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_MONTH_AND_LEAGUE, month, [sportsDataService.getLeagueEnumFromLeagueString(league)], null, 0)
		return constructScoresRankingResponse(scoreRanking,month,league, RANKING_TYPE_MONTH_AND_LEAGUE)
	}
	
	Map getRankingByLeagueAndMonth(String userId, String month,String league)
	{
		Map rankingResponse =  getRankingByLeagueAndMonth(month, league)
		return constructRankingWithFollowingIndicatorResponse(userId, rankingResponse, "rankScores")
		
	}
	
	// gets user ranking by league and month
	Map getRankingByLeagueAndMonthAndUser(String month,String league, String userId)
	{
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_MONTH_AND_LEAGUE_AND_USER, month, [sportsDataService.getLeagueEnumFromLeagueString(league)], userId, 0)
		return constructScoresRankingResponse(scoreRanking,month,league, RANKING_TYPE_MONTH_AND_LEAGUE_AND_USER)
	}
	
	// gets user ranking by league and month
	Map getRankingByMonthAndUser(String month,String userId)
	{
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_MONTH_AND_USER, month, null, userId, 0)
		return constructScoresRankingResponse(scoreRanking,month,null, RANKING_TYPE_MONTH_AND_USER)
	}
	
	Map getRankingByTournament(long tournamentId)
	{
		List<AbstractScore> scoreRanking = getRanking(RANKING_TYPE_TOURNAMENT, null, null, null, tournamentId)
		return constructScoresRankingResponse(scoreRanking,null,null, RANKING_TYPE_TOURNAMENT)
	}
	
	int getRankNumberByTournamentAndUser(long tournamentId, String userId)
	{
		List scoreRanking = getRanking(RANKING_TYPE_TOURNAMENT, null, null, null, tournamentId)
		
		List rankingResultAll =[]
		int rankingAllSize = scoreRanking.size()

		//userIdMap is used to contain all the user Ids from both weekly and all ranking.
		//The variable is later used to retrieve displayName and pictureURL from Parse
		Map userIdMap = [:]
		
		for (int i=0; i<rankingAllSize; i++){
			List rankEntry = scoreRanking[i]
			
			if (rankEntry[USERID_RANKING_QUERY_INDEX] == userId){
				return i+1
			}
		}
		
		return -1
	}
	
	Map constructRankingWithFollowingIndicatorResponse(String userId, Map rankingResponse, String rankListKey){
		rankingResponse[rankListKey] = constructRankingWithFollowingIndicatorResponse(userId, rankingResponse[rankListKey])
		return rankingResponse
	}
	
	List constructRankingWithFollowingIndicatorResponse(String userId, List rankingResponse){
		log.info "constructRankingWithFollowingIndicatorResponse() begins with userId=${userId}"
		Map followingUserIdMap = friendSystemService.listFollowingUserIdInMap(userId)
		for (userRank in rankingResponse){
			if (followingUserIdMap.containsKey(userRank.userId)){
				userRank.isFollowing = true
			}else{
				userRank.isFollowing = false
			}
		}
		log.info "constructRankingWithFollowingIndicatorResponse() ends"
		return rankingResponse
	}
	
	List constructFollowingRankingResponse(String userId, List rankingResponse){
		log.info "constructRankingWithFollowingIndicatorResponse() begins with userId=${userId}"
		Map followingUserIdMap = friendSystemService.listFollowingUserIdInMap(userId)
		List newRankingResponse = []
		for (userRank in rankingResponse){
			if (followingUserIdMap.containsKey(userRank.userId)){
				userRank.isFollowing = true
				newRankingResponse.add(userRank)
			}else if (userRank.userId == userId){
				userRank.isFollowing = false
				newRankingResponse.add(userRank)
			}
		}
		log.info "constructRankingWithFollowingIndicatorResponse() ends"
		return newRankingResponse
	}

	// returns user ranking map
	private Map getAccountInfoMap(String userId, long score, int rank){

		return [userId: userId, score:score, rank: rank]
	}
	
	// gets user profile Id
	private Map getUserProfileUserIdAsKeyMap(List userProfileList){
		Map UserProfileUserIdAsKeyMap = [:]
		for (Map userProfile: userProfileList){
			UserProfileUserIdAsKeyMap.put(userProfile.objectId, userProfile)
		}
		return UserProfileUserIdAsKeyMap
	}
	
	// Maps score ranking to Parse user Profile
	/**
	 * @param userRankingAll contains ['userId', 'score'] eg. [PqZM2z0hNZ, 1376]
	 * @param month
	 * @param league
	 * @param rankingType
	 * @return [type:"Overall",rankScores: [{
				      "userId" : "loKo1GyC3l",
				      "score" : 1038,
				      "rank" : 10,
				      "username" : "brayden",
				      "pictureURL" : "https://s3-us-west-2.amazonaws.com/userprofilepickture/00026profile.png"
				    },...]
			    ]
	 */
	private Map constructScoresRankingResponse(List userRankingAll, String month, String league, int rankingType)
	{
		log.info "constructScoresRankingResponse(): rankingType=${rankingType}, month=${month}, league=${league}, userRanking_size=${userRankingAll.size()}"
		List rankingResultAll =[]		
		int rankingAllSize = userRankingAll.size()

		//userIdMap is used to contain all the user Ids from both weekly and all ranking.
		//The variable is later used to retrieve displayName and pictureURL from Parse
		Map userIdMap = [:]
		
		for (int i=0; i<rankingAllSize; i++){
			List rankEntry = userRankingAll[i]
			rankingResultAll.add(getAccountInfoMap(rankEntry[USERID_RANKING_QUERY_INDEX],rankEntry[USER_SCORE_RANKING_QUERY_INDEX], i+1))
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
					
				if (userProfile.avatarCode != null && userProfile.avatarCode != "")
					rankingAllEntry.avatarCode =   userProfile.avatarCode 

			}
			
		}

		if (rankingType == RANKING_TYPE_ALL)
		{			
			return [type:"Overall",rankScores: rankingResultAll]
		}
		
		else if (rankingType == RANKING_TYPE_MONTH)
		{

			String longNameMonth = helperService.getMonthByMonth(month)
			Date firstOfMonth = helperService.getFirstOfMonth(month)
			String year = firstOfMonth.toCalendar().get(Calendar.YEAR).toString()
			return [type:"Month", date:longNameMonth+" "+year ,rankScores: rankingResultAll]
		}
		else if (rankingType == RANKING_TYPE_LEAGUE)
		{
			return [type:"League",league:league, rankScores: rankingResultAll]
		}
		else if (rankingType == RANKING_TYPE_MONTH_AND_LEAGUE)
		{
			String longNameMonth = helperService.getMonthByMonth(month)
			Date firstOfMonth = helperService.getFirstOfMonth(month)
			String year = firstOfMonth.toCalendar().get(Calendar.YEAR).toString()
			return [type:"League&Month",date:longNameMonth+" "+year ,league:league,rankScores: rankingResultAll]
		}
		else if (rankingType == RANKING_TYPE_MONTH_AND_LEAGUE_AND_USER)
		{
			String longNameMonth = helperService.getMonthByMonth(month)
			Date firstOfMonth = helperService.getFirstOfMonth(month)
			String year = firstOfMonth.toCalendar().get(Calendar.YEAR).toString()
			return [type:"League&Month&User",date:longNameMonth+" "+year ,league:league,rankScores: rankingResultAll]
		}
		else if (rankingType == RANKING_TYPE_MONTH_AND_USER)
		{
			String longNameMonth = helperService.getMonthByMonth(month)
			Date firstOfMonth = helperService.getFirstOfMonth(month)
			String year = firstOfMonth.toCalendar().get(Calendar.YEAR).toString()
			return [type:"League&Month&User",date:longNameMonth+" "+year ,league:league,rankScores: rankingResultAll]
		}else if (rankingType == RANKING_TYPE_TOURNAMENT)
		{
			return [type:"Tournament", rankScores: rankingResultAll]
		}
	}	
	
	private String getRankingDateString(String month){
		Date firstOfMonth = helperService.getFirstOfMonth(month)
		
		// if date_search is null
		if (firstOfMonth == null) //(date_search == null  )
		{
			log.error "getRankingByMonth(): Invalid month"
			return null
		}
		
		String rankMonth = helperService.getMonthByMonth(month)
		
		String year = firstOfMonth.toCalendar().get(Calendar.YEAR).toString()

		
		// evaluates month and year
		String dateString = helperService.evalDate(rankMonth,year)
		
		if(dateString == null  )
		{
			log.error "getRankingByMonth(): Invalid month"
			return null
		}
		return dateString
	}
}
