package com.doozi.scorena.iap
import grails.converters.JSON
class IAPController {

	def IAPService
	
	def getNonce() 
	{
		def nonce = IAPService.generateNonce()
		render nonce as JSON
	}
	
	def verifyApple()
	{
		// gets post request from app sending the encoded in app receipt
		def purchase  = IAPService.validateWithServer(params.nonce,params.user,params.encode)
		render purchase as JSON
	}
	
	def verifyAndroid()
	{
		def ans = IAPService.verifyPurchase(params.nonce,params.signature,params.data,params.dev)
		render ans as JSON
	}
	
	def activateAndroid()
	{
		def active = IAPService.applyPurchase(params.nonce,params.uID,params.data)
		render active as JSON
	}
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.error "${e.toString()}", e
		return
	}
}
