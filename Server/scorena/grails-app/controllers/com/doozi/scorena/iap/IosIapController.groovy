package com.doozi.scorena.iap

class IosIapController {

   def IosIapService
	// process method for ios receipt
     def process() 
	{ 
		// gets post request from app sending the encoded in app receipt
		def purchase  = IosIapService.validateWithServer(params.encode)
		render purchase
	}
}
