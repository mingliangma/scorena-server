package com.doozi.scorena.gameengine

import grails.transaction.Transactional



class QuestionPoolUtilService {

	def betTransactionService
	
    private def getDenominatorPick1Mult(int pick1Amount) {
		def denominatorPick1Mult = pick1Amount		
		if (denominatorPick1Mult==0)
			denominatorPick1Mult=1		
		return denominatorPick1Mult
    }
	
	private def getDenominatorPick2Mult(int pick2Amount) {
		def denominatorPick2Mult = pick2Amount			
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
	
	def calculatePick1PayoutMultiple(PoolInfo poolInfo){
		def denominatorPick1Mult = getDenominatorPick1Mult(poolInfo.getPick1Amount())
		def pick1PayoutMultiple =  (poolInfo.getPick1Amount() + poolInfo.getPick2Amount())/denominatorPick1Mult
		return pick1PayoutMultiple
	}
	
	def calculatePick2PayoutMultiple(PoolInfo poolInfo){
		def denominatorPick2Mult = getDenominatorPick2Mult(poolInfo.getPick2Amount())
		def pick2PayoutMultiple = (poolInfo.getPick1Amount() + poolInfo.getPick2Amount())/denominatorPick2Mult
		return pick2PayoutMultiple
	}
}
