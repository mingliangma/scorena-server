package com.doozi.scorena.useraccount

import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.transaction.LeagueTypeEnum

import grails.transaction.Transactional

import com.doozi.scorena.Account;
import com.doozi.scorena.UserBanner

@Transactional
class UserBannerService {

	def scoreRankingService
	def helperService

	public static final int CURRENT_MONTH_BANNER = 0
	public static final int PAST_MONTH_BANNER = 1
	public static final int SEASONAL_BANNER = 2
	
    private Map generateCurrentMonthBanner() 
	{
		Map currentRanking = [:]
		String month = helperService.getMonth()
		String year = Calendar.getInstance().get(Calendar.YEAR).toString()
		
		
		String bannerDate = helperService.getShortMonthYear()

		// gets current month ranking for leagues 
		for (LeagueTypeEnum league: LeagueTypeEnum.values())
		{
			List<AbstractScore> rankingLeague, tempLeague = []
			
			def currentMonth = scoreRankingService.getRankingByLeagueAndMonth(month, league.toString())
			
			if (currentMonth.rankScores) // find top 10 users
			{
				for (Map user: currentMonth.rankScores)
				{
					if (user.rank <= 10)
					{
						tempLeague.add(user)
					}
				}
			}
			def pastRanking = UserBanner.findAll("from UserBanner where league='"+league+"' and type="+CURRENT_MONTH_BANNER+" and month='"+bannerDate +"' order by rank asc")

			if (!pastRanking) // no previous current month banner 
			{
				if (currentMonth.rankScores) // has user ranking
				{
					for (Map user: tempLeague)
					{
							Account userAccount = Account.findByUserId(user.userId)
							def newCurrentMonthBanner = new UserBanner(league:league, type:CURRENT_MONTH_BANNER, rank:user.rank, month:bannerDate ,created_at:new Date(),updated_at:new Date())
							userAccount.addToBanner(newCurrentMonthBanner)

							if (!userAccount.save(failOnError:true)){
								System.out.println("---------------banner save failed")
								log.error "generateCurrentMonthBanner: Status: 1, Process: Insert Banner, Message: Banner failed to save"
								return null
								} 
					}
					currentRanking.put(league, tempLeague)
				}
			}
			else
			{	
				println("past banner count "+pastRanking.size() )
				println("current user ranking count "+ tempLeague.size())
				
				if (currentMonth.rankScores) // has user ranking
				{
					// amount of previous current banners is equal to amount of 10 ten ranked users 
					if (pastRanking.size() == tempLeague.size() ) 
					{
						int i = 0
						pastRanking.each{
							Account userAccount = Account.findByUserId(tempLeague.get(i).userId)
							it.rank = tempLeague.get(i).rank
							it.updated_at = new Date()
							it.account = userAccount
							if	(!it.save(failOnError:true))
							{
								System.out.println("---------------Banner save failed")
								log.error "generateCurrentMonthBanner: Status: 1, Process: Update Banner, Message: Banner failed to save"
								return null
							}
							
							i++
						}
						currentRanking.put(league, tempLeague)
					}
					
					// more previous banners then current rank users 
					else if(pastRanking.size() > tempLeague.size())
					{
						int offset = pastRanking.size() - tempLeague.size()
						int i = 0
						pastRanking.each{
							if (i <= tempLeague.size())
							{
								Account userAccount = Account.findByUserId(tempLeague.get(i).userId)
								it.rank = tempLeague.get(i).rank
								it.updated_at = new Date()
								it.account = userAccount
								
								if	(!it.save(failOnError:true))
								{
									System.out.println("---------------Banner save failed")
									log.error "generateCurrentMonthBanner: Status: 1, Process: Update Banner, Message: Banner failed to save"
									return null
								}
								i++
							}
						}
	
						for(int j =i; j < pastRanking.size(); j++)
						{
							pastRanking.get(j).delete()
						}
						currentRanking.put(league, tempLeague)	
					}
					
					else // more ranked users than previous banners 
					{
						int offset = tempLeague.size() - pastRanking.size()
						int i = 0
						pastRanking.each{
								Account userAccount = Account.findByUserId(tempLeague.get(i).userId)
								it.rank = tempLeague.get(i).rank
								it.updated_at = new Date()
								it.account = userAccount
								
								if	(!it.save(failOnError:true))
								{
									System.out.println("---------------Banner save failed")
									log.error "generateCurrentMonthBanner: Status: 1, Process: Update Banner, Message: Banner failed to save"
									return null
								}
								i++
						}
						
						for(int j = i; j < tempLeague.size(); j++)
						{
							Account userAccount = Account.findByUserId(tempLeague.get(j).userId)
							
							def newCurrentMonthBanner = new UserBanner(league:league,type:CURRENT_MONTH_BANNER,rank:tempLeague.get(j).rank,month:bannerDate,created_at:new Date(),updated_at:new Date())

							userAccount.addToBanner(newCurrentMonthBanner)
							if	(!userAccount.save(failOnError:true)){
								System.out.println("---------------banner save failed")
								log.error "generateCurrentMonthBanner: Status: 1, Process: Insert Banner, Message: Banner failed to save"
								return null
								}
						}
						currentRanking.put(league, tempLeague)
					}
				}
			}
		}
		return [Month:(month), Ranking:(currentRanking)]	
    }
	
	private Map generatePastMonthBanner()
	{
		Map lastMonthTopRanking = [:]
		String lastmonth = helperService.getPreviousMonthAndYear()
		
		for (LeagueTypeEnum league: LeagueTypeEnum.values())
		{
			List<AbstractScore> tempLeague = []
			
			def currentMonth = scoreRankingService.getRankingByLeagueAndMonth(month, league.toString())
			
			if (currentMonth.rankScores) // find top 10 users
			{
				for (Map user: currentMonth.rankScores)
				{
					if (user.rank <= 10)
					{
						tempLeague.add(user)
					}
				}
			}
			
			
			def pastRanking = UserBanner.findAll("from UserBanner where league='"+league+"' and type="+PAST_MONTH_BANNER+" and month ='"+lastmonth+"' order by rank asc")
			
			if (!pastRanking)
			{
				if (currentMonth.rankScores) // has user ranking
				{
					for (Map user: tempLeague)
					{
							Account userAccount = Account.findByUserId(user.userId)
							def newCurrentMonthBanner = new UserBanner(league:league, type:PAST_MONTH_BANNER,rank:user.rank,month:lastmonth,created_at:new Date(),updated_at:new Date())
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
			def pastRanking = UserBanner.findAll("from UserBanner where league='"+league+"' and type="+CURRENT_MONTH_BANNER+" and month ='"+lastmonth+"' order by rank asc")
			
			if (pastRanking)
			{
				pastRanking.each{
					it.delete()
				}	
			}
		}
	}
	
	private Map getUserBanners(String userId)
	{
		Map listOfBanners = [:]
		List<UserBanner> bannerList = []
		def userBanners = UserBanner.findAll("from UserBanner as s where s.account.userId ='"+userId+"'")
		
		if(!userBanners)
		{
			return [Banner:[:]]
		}
		
		userBanners.each{
			println(it.updated_at)
			bannerList.add([rank:it.rank, league:it.league.name(), month:it.month])
		}
		
		listOfBanners.putAt("Banner", bannerList)
		
		return listOfBanners
	}
	
	def generateSeasonal(String league)
	{
		
	}
	
	
	
	
}
