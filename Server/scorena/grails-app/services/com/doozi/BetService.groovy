package com.doozi

import java.util.Date;
import com.doozi.Account
import com.doozi.GameService
import com.doozi.Question
import com.doozi.Game
import com.doozi.BetTransaction
import com.doozi.BetResult

import grails.transaction.Transactional

@Transactional
class BetService {

	def saveBetTrans(int _wager, Date _time, int _pick, String userId, int quesitonId, int gameId) {
		def account = Account.findByUserId(userId)
		def question = Question.findById(quesitonId)
		def game = Game.findById(gameId)
		return saveBetTrans(_wager, _time, _pick, account, question, game)
		
	}
    def saveBetTrans(int _wager, Date _time, int _pick, Account account, Question q, Game game) {
		println "wager:"+_wager
		println "_time:"+_time
		println "_pick:"+_pick
		

		def bet = new BetTransaction(wager: _wager, time: _time, pick: _pick, betResult: new BetResult(gameResult: 1, payOut:0))
		
//		if (bet.save()){
//			System.out.println("---------------bet successfully saved")
//		}else{
//			System.out.println("---------------bet save failed")
//		}
		
		account.addToBet(bet)
		q.addToBet(bet)
		game.addToBet(bet)
		
		
		if (account.save(failOnError:true)){
			System.out.println("---------------account successfully saved")
		}else{
			System.out.println("---------------account save failed")
			return 202
		}
		
		if (q.save(failOnError:true)){
			System.out.println("---------------q successfully saved")			
		}else{
			System.out.println("---------------q save failed")
			return 202
		}
		
		if (game.save(failOnError:true)){
			System.out.println("---------------game successfully saved")
		}else{
			System.out.println("---------------game save failed")
			return 202
		}
		return 201
    }
	
	 
}
