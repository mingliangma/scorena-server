package com.doozi.scorena.gameengine

import com.doozi.scorena.PoolTransaction
import grails.transaction.Transactional

@Transactional

class QuestionPoolUtilService {

	def betService
	
    private def getDenominatorPick1Mult(def lastBet) {
		def denominatorPick1Mult = lastBet.pick1Amount		
		if (denominatorPick1Mult==0)
			denominatorPick1Mult=1		
		return denominatorPick1Mult
    }
	
	private def getDenominatorPick2Mult(def lastBet) {
		def denominatorPick2Mult = lastBet.pick2Amount			
		if (denominatorPick2Mult==0)
			denominatorPick2Mult=1
		return denominatorPick2Mult
	}
	
	def getCurrentOddsPick1(def lastBet, int denominatorPick2Mult){
		def currentOddsPick1=1
		if (lastBet.pick1Amount > lastBet.pick2Amount){
			currentOddsPick1=(lastBet.pick1Amount/denominatorPick2Mult)
		}
		
	}
	
	def getCurrentOddsPick2(def lastBet, int denominatorPick1Mult){
		def currentOddsPick2=1

		if (lastBet.pick1Amount < lastBet.pick2Amount){
			currentOddsPick2 = lastBet.pick2Amount/denominatorPick1Mult
		}
	}
	
	def calculatePick1PayoutMultiple(def questionId){
		PoolTransaction lastBet = betService.getLatestBetByQuestionId(questionId)
		return calculatePick1PayoutMultiple(lastBet)
	}
	
	def calculatePick1PayoutMultiple(PoolTransaction lastBet){
		def denominatorPick1Mult = getDenominatorPick1Mult(lastBet)
		def pick1PayoutMultiple =  (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick1Mult
		return pick1PayoutMultiple
	}
	
	def calculatePick2PayoutMultiple(def questionId){
		PoolTransaction lastBet = betService.getLatestBetByQuestionId(questionId)
		return calculatePick2PayoutMultiple(lastBet)
	}
	
	def calculatePick2PayoutMultiple(PoolTransaction lastBet){
		def denominatorPick2Mult = getDenominatorPick2Mult(lastBet)
		def pick2PayoutMultiple = (lastBet.pick1Amount + lastBet.pick2Amount)/denominatorPick2Mult
		return pick2PayoutMultiple
	}
}
