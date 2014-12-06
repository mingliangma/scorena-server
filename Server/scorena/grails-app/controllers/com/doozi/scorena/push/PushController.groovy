package com.doozi.scorena.push

import grails.converters.JSON

class PushController {
	def pushService
    
	def updateChannel()
	{
		def installation = pushService.updateGameChannel(params.objectId, params.eventkey)
		render installation
	}
	
	def removeChannel()
	{
		def installation = pushService.removeGameChannel(params.objectId, params.eventkey)
		render installation
	}
	
	def getUserInstallationID()
	{
		def installation = pushService.getInstallationByUsername(params.user)
		render installation 
	//	render installation as JSON
	}
	
	def sendEndofGamePush()
	{
		def installation = pushService.testDb()
		render installation
	}
	
	
}
