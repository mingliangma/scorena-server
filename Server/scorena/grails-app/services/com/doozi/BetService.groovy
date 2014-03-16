package com.doozi

import java.util.Date;
import com.doozi.User
import com.doozi.Account
import com.doozi.GameService
import com.doozi.Question
import com.doozi.Game
import com.doozi.BetTransaction
import com.doozi.BetResult

import grails.transaction.Transactional

@Transactional
class BetService {

    def saveBetTrans(int _wager, Date _time, boolean _pick, User user, Question q, Game game) {
		def bet = new BetTransaction(wager: _wager, time: _time, pick: _pick, betResult: new BetResult(gameResult: "", payOut:0))
		System.out.println("bet: "+bet.id)
		
	//	if (bet.save()){
	//		System.out.println("---------------bet successfully saved")
	//	}else{
	//		System.out.println("---------------bet save failed")
	//	}
		
		user.addToBet(bet)
		q.addToBet(bet)
		game.addToBet(bet)
		
		
		if (user.save()){
			System.out.println("---------------user successfully saved")
		}else{
			System.out.println("---------------user save failed")
		}
		
		if (q.save()){
			System.out.println("---------------q successfully saved")
		}else{
			System.out.println("---------------q save failed")
		}
		
		if (game.save()){
			System.out.println("---------------game successfully saved")
		}else{
			System.out.println("---------------game save failed")
		}
    }
}
