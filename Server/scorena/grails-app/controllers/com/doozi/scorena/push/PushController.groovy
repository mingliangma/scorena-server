package com.doozi.scorena.push

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder

class PushController {
	def pushService
	def rest = new RestBuilder()
	def updateChannel()
	{
		def installation = pushService.updateGameChannel(rest, params.objectId, params.eventkey)
		render installation
	}
	
	def removeChannel()
	{
		def installation = pushService.removeGameChannel(rest, params.objectId, params.eventkey)
		render installation
	}
	
	def getUserInstallationID()
	{
		def installation = pushService.getInstallationByUserID(params.userId)
	//	render installation 
		render installation as JSON
	}
	
	/*
	def sendEndofGamePush()
	{
		def installation = pushService.endOfGamePush(rest, params.eventKey, params.userId, params.msg)
		render installation
	}
	
	*/
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.info "${e.toString()}"
	}
}
