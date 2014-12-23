package com.doozi.scorena.ranking
import java.util.List;
import java.util.Map;

import com.doozi.scorena.*
import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.score.QuestionScore

import com.doozi.scorena.utils.*

import grails.converters.JSON
import org.springframework.transaction.annotation.Transactional

import org.codehaus.groovy.grails.web.json.JSONObject

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL


//The ranking page service
@Transactional
class ScoreRankingService {
	def parseService
	def sportsDataService
	def helperService
	
	public static final int USERID_RANKING_QUERY_INDEX = 0
	public static final int USER_SCORE_RANKING_QUERY_INDEX = 1
	Map days_31 = [January:"01",March:"03",May:"05",July:"07",August:"08",October:"10" , December:"12"]
	Map days_30 = [April:"04",June:"06",September:"09",November:"11"]

	// gets the overall ranking of all users 
	def getRanking()
	{
		try
		{
			// searches database
			def userRankingAll = AbstractScore.executeQuery("select s.account.userId, sum(score) from AbstractScore as s group by s.account.id order by sum(score) desc")
			
			// if userRanking is null, returns error
			if (userRankingAll == null || userRankingAll == "")
			{
				return [code:400, error: "No Results"]
			}
			// return user ranking
			return returnScores(userRankingAll,"","","",0)
		}
		catch (Exception e){
			return [code: 400, error: e.getMessage()]
		}
	}
	
	// gets the user ranking by month based on the month and year supplied  
	def getRankingByMonth(String month)
	{	
		try 
		{
			def firstOfMonth = helperService.getFirstOfMonth(month)
			
			// if date_search is null
			if (firstOfMonth == null) //(date_search == null  )
			{
				return [code: 400, error: "Invalid month"]
			}
			
			String rankMonth = helperService.getMonthByMonth(month)
			
			String year = firstOfMonth.toString().substring(24,28)

			
			// evaluates month and year
			String date_search = evalDate(rankMonth,year)
			
			if(date_search == null  )
			{
				return [code: 400, error: "Invalid month"]
			}
			
			// searches database
			def userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score) from AbstractScore as s where s.gameStartTime between "+ date_search +" group by s.account.id order by sum(score) desc")

			
			// if userRanking is null, returns error
			if (userRanking == null || userRanking == "")
			{
				return [code: 400, error: "No Results"]
			}
			
			// return user ranking
			return returnScores(userRanking,rankMonth,year,"",1)
		}
		catch (Exception e)
		{
			return [code: 400, error: e.getMessage()]
		}
	}

	
	// gets the user ranking by league
	def getRankingByLeague(String league)
	{
		try
		{
			// gets sports code
			//String leagueCode = sportsDataService.getLeagueCodeFromEventKey(league)
			
			// if league Code is null, return error
		/*	if(leagueCode == null)
			{
				return [code: 400, message: "Invalid leagueCode"]
			}
			*/
			// searches database
			def userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score) from AbstractScore as s where s.league = '"+ league+"' group by s.account.id order by sum(score) desc")
			
			// if userRanking is null, returns error
			if (userRanking == null || userRanking == "")
			{
				return [code: 400, error: "No Results"]
			}
			
			// return user ranking 
			return returnScores(userRanking,"","",league,2)
		}
		catch (Exception e)
		{
			return [code: 400, error: e.getMessage()]
		}
	}
	
	// gets user ranking by league and month
	def getRankingByLeagueAndMonth(String month,String league)
	{	
		try
		{
			
			def firstOfMonth = helperService.getFirstOfMonth(month)

			// if date_search is null
			if (firstOfMonth == null) //(date_search == null  )
			{
				return [code: 400, error: "Invalid month"]
			}
			
			String rankMonth = helperService.getMonthByMonth(month)
			String year = firstOfMonth.toString().substring(24,28)

			
			// evaluates month and year
			String date_search = evalDate(rankMonth,year)
			
			if(date_search == null  )
			{
				return [code: 400, error: "Invalid month"]
			}
			
			
			// gets sports code
			//String leagueCode = sportsDataService.getLeagueCodeFromEventKey(league)
			
			// if league Code is null, return error
		/*	if(leagueCode == null)
			{
				return [code: 400, message: "Invalid leagueCode"]
			}
			*/
			// searches database
			def userRanking = AbstractScore.executeQuery("select s.account.userId, sum(score) from AbstractScore as s where s.gameStartTime between "+ date_search +" AND s.league = '"+league+"'  group by s.account.id order by sum(score) desc")
			
			// if userRanking is null, return error
			if (userRanking == null || userRanking == "")
			{
				return [code: 400, error: "No Results"]
			}
			
			// return user ranking
			return returnScores(userRanking,rankMonth,year,league,3)
		}
		catch (Exception e)
		{
			return [code: 400, error: e.getMessage()]
		}
	}
	
	
	// returns date query string based on month and year
	public String evalDate(String month, String year)
	{
		String date_search = ""
		String MM = ""
		String DD = ""
	
		if (month.equals("February"))
		{
			MM = "02"
			
			if (leapyear(Integer.parseString(year)))
			{
				DD = "29"
			}
			else
			{
				DD = "28"
			}
			
			date_search = "'"+year+"-"+MM+"-01 00:00:00' AND '"+year+"-"+MM+"-"+DD+" 23:59:59'"
			
			return date_search
		}
		
		else
		{
			Map datecheck = getMonth(month)
						
			MM = datecheck['month']
			DD = datecheck['day']
			
			// checks if month is 
			if(MM == null)
			{
				return null
			}
			
			date_search =  "'"+year+"-"+MM+"-01 00:00:00' AND '"+year+"-"+MM+"-"+DD+" 23:59:59'"
			
			return date_search
		}
	}
	
	// returns map value for month date
	public Map getMonth(String month)
	{		
		if (!days_31.containsKey(month))
		{
			String mm = days_30[month]
			return [ month:mm, day:"30"]
		}
		
		if (!days_30.containsKey(month))
		{
			String mm = days_31[month]
			return [month:mm, day:"31"]
		}
	}
	
	// checks if year is a leap year
	public boolean leapyear(int year)
	{
		if(year%400 == 0)
		{
			return true
		}
		
		if(year%100 == 0)
		{
			return false
		}
		
		if(year%4 == 0)
		{
			return true
		}
		
		return false;
	}
	

	// returns user ranking map
	private Map getAccountInfoMap(String userId, long score, int rank){

		return [userId: userId, score:score  , rank: rank]
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
	private Map returnScores(List userRankingAll, String month, String year, String league, int code)
	{
		List rankingResultAll =[]
		
		int rankingAllSize = userRankingAll.size()

		//userIdMap is used to contain all the user Ids from both weekly and all ranking.
		//The variable is later used to retrieve displayName and pictureURL from Parse
		Map userIdMap = [:]
		
		for (int i=0; i<rankingAllSize; i++){
			List rankEntry = userRankingAll[i]
			rankingResultAll.add(getAccountInfoMap(rankEntry[USERID_RANKING_QUERY_INDEX],rankEntry[USER_SCORE_RANKING_QUERY_INDEX] ,i+1))
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
		
		if (code == 0)
		{			
			return [type:"Overall",rankScores: rankingResultAll]
		}
		
		else if (code == 1)
		{
			if(rankingAllSize == 0)
			{
				return [type:"Overall", date:month+" "+year ,rankScores: rankingResultAll]
			}
			
			return [type:"Month", date:month+" "+year ,rankScores: rankingResultAll]
		}
		else if (code == 2)
		{
			return [type:"League",league:league, rankScores: rankingResultAll]
		}
		else if (code == 3)
		{
			return [type:"League&Month",date:month+" "+year ,league:league,rankScores: rankingResultAll]
		}
	}	
	
	
}