package com.doozi.scorena.controllerservice

import java.util.Date;

import com.doozi.scorena.Account;
import com.doozi.scorena.BetResult;
import com.doozi.scorena.PoolTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import org.hibernate.criterion.CriteriaSpecification

import grails.transaction.Transactional

@Transactional
class BetService {

	def getPayoutTransByQuestion(Question q){
		def result = PoolTransaction.find("from PoolTransaction as t where (t.transactionType=1 and t.question.id=?)",[q.id])
	}
	
	
	def savePayoutTrans(Account playerAccount, Question q, int payout, int winnerPick, int potAmoutToBePaid, int numPlayersToBePaid, int wager){
		def potAmoutToBePaidAfter = potAmoutToBePaid - payout
		def numPlayersToBePaidAfter = numPlayersToBePaid - 1
		int result = 0
		if (potAmoutToBePaidAfter < 0 || numPlayersToBePaidAfter < 0){
			println "ERROR: Total payout amount is nagative"
			result = -1
		} 
//		Random random = new Random()
//		def createdAt = new Date() - (random.nextInt(8) + 3)
		
		def bet = new PoolTransaction(transactionAmount: payout, transactionType:PoolTransaction.PAYOUT, createdAt: new Date(), pick: winnerPick, pick1Amount:potAmoutToBePaidAfter, pick1NumPeople:numPlayersToBePaidAfter,
			pick2Amount:wager, pick2NumPeople:0, eventKey: q.eventKey)
		
//		def bet = new PoolTransaction(transactionAmount: payout, transactionType:PoolTransaction.PAYOUT, createdAt: createdAt, pick: winnerPick, pick1Amount:potAmoutToBePaidAfter, pick1NumPeople:numPlayersToBePaidAfter,
//			pick2Amount:wager, pick2NumPeople:0)
		
		playerAccount.addToBet(bet)
		q.addToBet(bet)
		
		if (!playerAccount.save(failOnError:true)){
			println "ERROR: payout transaction failed to be added to player account"
		}
		
		if (!q.save(failOnError:true)){
			println "ERROR: payout transaction failed to be added to question"
			result = -1
		}
		
		return result
	}
	
	def saveBetTrans(int _wager, Date _time, int _pick, String userId, long quesitonId) {
		def account = Account.findByUserId(userId)
		if (!account){
			return [code:202, message: "the userId does not exsist"]
		}
		
		if (account.currentBalance < _wager){
			return [code:202, message: "The user does not have enough coins to make a bet"]
		}
		def question = Question.findById(quesitonId)
		if (!question){
			return [code:202, message: "the questionId does not exsist"]
		}
		return saveBetTrans(_wager, _time, _pick, account, question)
		
	}
	
    def saveBetTrans(int playerWager, Date betCreatedAt, int playerPick, Account playerAccount, Question q) {
		int pick1amount
		int pick2amount 
		int pick1num 
		int pick2num 
		def lastBet
		
		def b = PoolTransaction.find("from PoolTransaction as b where (b.question.id=? and b.account.id=? and b.transactionType=?)", q.id, playerAccount.id, PoolTransaction.BUYIN)
		if (b){
			return [code:202, message: "the bet transaction already exsists"]
			
		}
		
		if (q.bet == null || q.bet.size() == 0){
			if (playerPick==1){
				 pick1amount = playerWager
				 pick1num = 1
				 pick2amount = 0			 
				 pick2num = 0
			}else{
				pick1amount = 0
				pick1num = 0
				pick2amount = playerWager
				pick2num = 1
			}
			
		}else{
		
			def questionId = q.id.toString()
			lastBet = getLatestBetByQuestionId(questionId)
		
			if (playerPick==1){
				 pick1amount = lastBet.pick1Amount + playerWager
				 pick1num = lastBet.pick1NumPeople + 1
				 pick2amount = lastBet.pick2Amount			 
				 pick2num = lastBet.pick2NumPeople
			}else{
				pick1amount = lastBet.pick1Amount	
				pick1num = lastBet.pick1NumPeople
				pick2amount = lastBet.pick2Amount + playerWager
				pick2num = lastBet.pick2NumPeople + 1
			}
				
		}	
		def bet = new PoolTransaction(transactionAmount: playerWager, transactionType:PoolTransaction.BUYIN, createdAt: betCreatedAt, pick: playerPick, pick1Amount:pick1amount, pick1NumPeople:pick1num,
			pick2Amount:pick2amount, pick2NumPeople:pick2num, eventKey: q.eventKey)
		
		
		playerAccount.addToBet(bet)
		q.addToBet(bet)

		
		playerAccount.currentBalance -= playerWager
		
		if (!playerAccount.save(failOnError:true)){
			System.out.println("---------------account save failed")
			return [code:202, message: "account data is not saved"]
		}
		
		if (!q.save(failOnError:true)){
			System.out.println("---------------q save failed")
			return [code:202, message: "Question data is not saved"]
		}
		

		return [code:201]
    }
	
	def getLatestBetByQuestionId(def qId){
		def lastBets = PoolTransaction.findAll("from PoolTransaction as b WHERE id = (select max(id) from b where question_id=? and b.transactionType=?)", [qId, 0])
		
		if (lastBets.size()==0){
		
			def lastUpdate=new Date()
			return [pick1Amount:0,pick2Amount:0,pick1NumPeople:0,pick2NumPeople:0, lastUpdate:lastUpdate]
		}
				
		return lastBets.get(0)
				
	}
	def listPayoutTransByUserId(def userId){
		return listPayoutTransByUserId(userId, 0)
	}
	
	//periodOption:
	//	0=all
	//	1=monthly
	//	2=weekly
	def listPayoutTransByUserId(def userId, def periodOption){
		if (periodOption==0)
			return PoolTransaction.findAll("from PoolTransaction as b where b.account.userId=? and b.transactionType=?", [userId, 1])
		if (periodOption==1)
			return PoolTransaction.findAll("from PoolTransaction as b where b.account.userId=? and b.transactionType=? and (date between DATE_FORMAT(NOW() ,'%Y-%m-01') AND NOW())", [userId, 1])
		if (periodOption==2)
			return PoolTransaction.findAll("from PoolTransaction as b where b.account.userId=? and b.transactionType=? and (date between subdate(now(), INTERVAL weekday(now()) DAY) AND NOW())", [userId, 1])
	}
	
	def getBetByQuestionIdAndUserId(def qId, def userId){
		return PoolTransaction.find("from PoolTransaction as b where b.question.id=? and b.account.userId=? and b.transactionType=?", [qId, userId, 0])
	}
	
	def listAllBets(def qId){
		return PoolTransaction.findAll("from PoolTransaction as b where b.question.id=? and b.transactionType=?", [qId, 0], [cache: true])
	}
	
	def listAllBetsByPick(def qId, def pick){
		if (pick==0){
			return listAllBets(qId)
		}else{
			return PoolTransaction.findAll("from PoolTransaction as b where b.question.id=? and b.pick=? and b.transactionType=?", [qId, pick, 0])
		}
	}
	
	def listDistinctBetEventKeyByUserId(def userId){
		return PoolTransaction.executeQuery("SELECT DISTINCT eventKey from PoolTransaction as b where b.account.userId=? and b.transactionType=?", [userId, 0])
	}

}
