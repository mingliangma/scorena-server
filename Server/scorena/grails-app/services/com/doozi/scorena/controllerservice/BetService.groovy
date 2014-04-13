package com.doozi.scorena.controllerservice

import java.util.Date;


import com.doozi.scorena.Account;
import com.doozi.scorena.BetResult;
import com.doozi.scorena.BetTransaction;
import com.doozi.scorena.Game;
import com.doozi.scorena.Question;

import grails.transaction.Transactional

@Transactional
class BetService {

	def saveBetTrans(int _wager, Date _time, int _pick, String userId, int quesitonId, int gameId) {
		def account = Account.findByUserId(userId)
		def question = Question.findById(quesitonId)
		def game = Game.findById(gameId)
		return saveBetTrans(_wager, _time, _pick, account, question, game)
		
	}
    def saveBetTrans(int playerWager, Date betCreatedAt, int playerPick, Account playerAccount, Question q, Game game) {
		println "wager:"+playerWager
		println "_time:"+betCreatedAt
		println "_pick:"+playerPick
		int pick1amount
		int pick2amount 
		int pick1num 
		int pick2num 
		
		if (q.bet == null){
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
			def lastBet = q.bet.find
		
		}
		
		def bet = new BetTransaction(wager: playerWager, createdAt: betCreatedAt, pick: playerPick, pick1Amount:pick1amount, pick1NumPeople:pick1num,
			pick2Amount:pick2amount, pick2NumPeople:pick2num)
		
		
		
		playerAccount.addToBet(bet)
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
