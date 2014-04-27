package com.doozi.scorena.controllerservice

import com.doozi.scorena.sportsdata.*;
import grails.transaction.Transactional

@Transactional
class ViewService 
{
	def getUpcomingEplMatches() 
	{
		def upcomingGames = UpcomingEplView.findAll()
		return upcomingGames
    }
	
	def getUpcomingChampMatches() 
	{
		def upcomingGames = UpcomingChampView.findAll()
		return upcomingGames
	}
	
	def getPastEplMatches() 
	{
		def pastGames = PastEplView.findAll()
		return pastGames
	}
	
	def getPastChampMatches() 
	{
		def pastGames = PastChampView.findAll()
		return pastGames
	}
}
