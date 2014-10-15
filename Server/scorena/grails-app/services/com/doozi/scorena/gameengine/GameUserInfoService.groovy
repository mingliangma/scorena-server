package com.doozi.scorena.gameengine

import com.doozi.scorena.Question;
import com.doozi.scorena.controllerservice.*
import com.doozi.scorena.transaction.BetTransaction

import grails.transaction.Transactional


class GameUserInfoService {
	
	static transactional = false
	
	def betTransactionService
	def processEngineImplService
	def questionUserInfoService
	def questionPoolUtilService
	
    Map getUpcomingGamesUserInfo(String gameId, List<BetTransaction> userBetsInTheGame, String userId) {
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(userBetsInTheGame)
		userinfo.userWager = getWagerInGame(gameId, userId, userBetsInTheGame)
		return userinfo
    }
	
	Map getPastGamesUserInfo(String gameId, List<BetTransaction> userBetsInTheGame, String userId){
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(userBetsInTheGame)
		userinfo.gameWinningAmount = getProfitInGame(gameId, userId, userBetsInTheGame)
		return userinfo
	}
	
	private int getProfitInGame(String gameId, String userId, List userBetsInTheGame){
		int profitAmount = 0
		
		for (BetTransaction bet: userBetsInTheGame){
			profitAmount += questionUserInfoService.getProfitInQuestion(gameId, bet)
		}
		return profitAmount
	}
	
	private int getWagerInGame(String gameId, String userId, List<BetTransaction> userBetsInTheGame){
		int wagerInGame = 0
		
		for (BetTransaction bet: userBetsInTheGame){
			wagerInGame += bet.transactionAmount
		}
		
		return wagerInGame
	}
	
	private Boolean getPlacedBet(List<BetTransaction> userBetsInGames ){
		if (userBetsInGames.size() == 0)
			return false
		else
			return true
	}
	
	private List<BetTransaction> listBetsByGameId(List<BetTransaction> playedGames, String gameId){
		List<BetTransaction> userBetsInGame = []
		for (BetTransaction bet: playedGames){
			if (bet.eventKey == gameId){
				userBetsInGame.add(bet)
			}
		}
		return userBetsInGame
	}
}
