package com.doozi.scorena.useraccount

import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.transaction.LeagueTypeEnum

import grails.transaction.Transactional

import com.doozi.scorena.Account;
import com.doozi.scorena.UserBanner;
import com.doozi.scorena.useraccount.UserBannerNP
import com.mysql.jdbc.log.Log;

@Transactional
class UserBannerService {

	def scoreRankingService
	def helperService

	public static final int CURRENT_MONTH_BANNER = 0
	public static final int PAST_MONTH_BANNER = 1
	public static final int SEASONAL_BANNER = 2
	
    private List<UserBannerNP> generateCurrentMonthBanner(String userId) 
	{
		Map currentRanking = [:]
		String month = helperService.getMonth()
		String year = Calendar.getInstance().get(Calendar.YEAR).toString()
		
		
		String bannerDate = helperService.getShortMonthYear()
		List<UserBannerNP> bannerResultList = []
		
		// gets current month ranking for leagues 
		
	//	Map currentMonthRankingResponse = scoreRankingService.getRankingByMonth(month)
	//	List<UserBanner> currentMonthRanking = currentMonthRankingResponse.rankScores
		
		for (LeagueTypeEnum league: LeagueTypeEnum.values())
		{
			Map currentMonthRankingResponse = scoreRankingService.getRankingByLeagueAndMonth(month, league.toString())
			
			if (currentMonthRankingResponse.rankScores)
			{
				for (Map user: currentMonthRankingResponse.rankScores)
				{
					if (user.rank <= 10 && user.userId.equals(userId))
					{
						bannerResultList.add(new UserBannerNP(league:currentMonthRankingResponse.league, type:CURRENT_MONTH_BANNER, rank:user.rank, bannerDateString:bannerDate))
					}
				}
			}
		
		}
				
		bannerResultList.addAll(getPastUserBanners(userId))
		
		
		log.info "tempLeague="+bannerResultList
		return bannerResultList
    }
	
	private Map generatePastMonthBanner()
	{
		Map lastMonthTopRanking = [:]
		String lastmonth = helperService.getPreviousMonthAndYear()
		String month = helperService.getPreviousMonth()
		
		for (LeagueTypeEnum league: LeagueTypeEnum.values())
		{
			List<AbstractScore> tempLeague = []
			
			Map currentMonthRanking = scoreRankingService.getRankingByLeagueAndMonth(month, league.toString())
			
			if (currentMonthRanking.rankScores) // find top 10 users
			{
				for (Map user: currentMonthRanking.rankScores)
				{
					if (user.rank <= 10)
					{
						tempLeague.add(user)
					}
				}
			}
			
			
			def pastRanking = UserBanner.findAll("from UserBanner where league='"+league+"' and type="+PAST_MONTH_BANNER+" and bannerDateString ='"+lastmonth+"' order by rank asc")
			
			if (!pastRanking)
			{
				if (currentMonthRanking.rankScores) // has user ranking
				{
					for (Map user: tempLeague)
					{
							Account userAccount = Account.findByUserId(user.userId)
							def newCurrentMonthBanner = new UserBanner(league:league, type:PAST_MONTH_BANNER,rank:user.rank,bannerDateString:lastmonth,created_at:new Date(),updated_at:new Date())
							userAccount.addToBanner(newCurrentMonthBanner)

							if (!userAccount.save(failOnError:true)){
								System.out.println("---------------banner save failed")
								log.error "generateCurrentMonthBanner: Status: 1, Process: Insert Banner, Message: Banner failed to save"
								return null
								}
					}
					lastMonthTopRanking.put(league, tempLeague)
				}
			}
		}
		
		return [Month:(lastmonth), Ranking:(lastMonthTopRanking)]
	}
	
	private void clearPastCurrentBanners()
	{
		String lastmonth = helperService.getPreviousMonthAndYear()
		
		for (LeagueTypeEnum league: LeagueTypeEnum.values())
		{
			def pastRanking = UserBanner.findAll("from UserBanner where league='"+league+"' and type="+CURRENT_MONTH_BANNER+" and bannerDateString ='"+lastmonth+"' order by rank asc")
			
			if (pastRanking)
			{
				pastRanking.each{
					it.delete()
				}	
			}
		}
	}
	
	private List<UserBannerNP> getPastUserBanners(String userId)
	{
		List<UserBannerNP> bannerListResult = []
		List<UserBanner> userBanners = UserBanner.findAll("from UserBanner as s where s.account.userId ='"+userId+"'")
		
		userBanners.each{
			bannerListResult.add(new UserBannerNP(rank:it.rank, league:it.league.name(), bannerDateString:it.bannerDateString, type:PAST_MONTH_BANNER ))
		}
				
		return bannerListResult
	}
	
	def generateSeasonal(String league)
	{
		
	}
	
	
	
	
}
