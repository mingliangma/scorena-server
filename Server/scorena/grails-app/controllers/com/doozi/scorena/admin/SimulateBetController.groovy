package com.doozi.scorena.admin

import com.doozi.scorena.*
import com.doozi.scorena.processengine.*

import grails.converters.JSON
import grails.plugins.rest.client.RestBuilder

class SimulateBetController {
	def simulateBetService
	def simulateCommentService
	def payoutTansactionService
    def simulateBet() { 
		def result = [:]
		int betCounter = 0
		boolean isReadyProcess = ProcessStatus.transactionProcessStartRunning("API call simulate bet")
		if (isReadyProcess){
			log.info "simulate Bet started"
			betCounter = simulateBetService.simulateBetUpcoming()
			ProcessStatus.transactionProcessStopped()
			log.info "simulate Bet ended"
			result = [numberBets: betCounter]
		}else{
		log.info "simulate Bet: Other process is running"
			result = [message: "Other process is running"]
		}
		render result as JSON
	}
	
	def simulateComment(){
		simulateCommentService.simulateComment()
	}
	
	def handleException(Exception e) {
		response.status = 500
		render e.toString()
		log.info "${e.toString()}"
	}
}
