package com.doozi.scorena.user

import java.util.List;
import com.doozi.scorena.useraccount.UserBannerNP
import grails.converters.*

class UserBannerController {

	def userBannerService
	
    def generateCurrentMonthBanner() 
	{ 
		List<UserBannerNP> banners = userBannerService.generateCurrentMonthBanner(params.userId) 
		render banners as JSON
		return
	}
	
	
	def generatePastMonthTopBanner()
	{
		def banners = userBannerService.generatePastMonthBanner()
		render banners as JSON
	}
	
	def clearPastCurrentBanners()
	{
		def banners = userBannerService.clearPastCurrentBanners()
	}
	
	
	// eg.-  /v1/banners/getBanners?userId=dQanIg0Wz4
	def getUserBanners()
	{
		def banners = userBannerService.getUserBanners(params.userId)
		render banners as JSON
	}
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.error "${e.toString()}", e
		return
	}
}