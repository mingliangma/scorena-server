package com.doozi.scorena

import grails.converters.JSON

class UserBannerController {

	def userBannerService
	
    def generateCurrentMonthBanner() 
	{ 
		def banners = userBannerService.generateCurrentMonthBanner(params.userId) 
		render banners as JSON
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
