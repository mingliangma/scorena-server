package com.doozi.scorena.controllerservice

import com.doozi.scorena.PoolTransaction
import com.doozi.scorena.Question;

import grails.transaction.Transactional


@Transactional
class GameUserInfoService {
	def betService
	def processEngineImplService
	def questionUserInfoService
	def questionPoolUtilService
	
    Map getUpcomingGamesUserInfo(String gameId, List playedGames, String userId) {
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(gameId, playedGames)
		userinfo.userWager = getWagerInGame(gameId, userId)
		return userinfo
    }
	
	Map getPastGamesUserInfo(String gameId, List playedGames,  String userId){
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(gameId, playedGames)
		userinfo.gameWinningAmount = getProfitInGame(gameId, userId)
		return userinfo
	}
	
	private int getProfitInGame(String gameId, String userId){
		List userBetsInGame = betService.listBetsByUserIdAndGameId(gameId, userId)
		int profitAmount = 0
		
		for (PoolTransaction bet: userBetsInGame){
			profitAmount += questionUserInfoService.getProfitInQuestion(gameId, bet)
		}
		return profitAmount
	}
	
	private int getuserWinningAmount(PoolTransaction bet){
		int winnerPick = processEngineImplService.getWinningPick(bet.eventKey, bet.question)
		int userPickStatus = questionUserInfoService.getUserPickStatus(winnerPick, bet.pick)
		if (userPickStatus==0){
			return 0
		}else if (userPickStatus==1){
			return 
		}
	}

	
	private int getWagerInGame(String gameId, String userId){
		List userBetsInGame = betService.listBetsByUserIdAndGameId(gameId, userId)
		int wagerInGame = 0
		
		for (PoolTransaction bet: userBetsInGame){
			wagerInGame += bet.transactionAmount
		}
		
		return wagerInGame
	}
	
	private Boolean getPlacedBet(String gameId, List playedGames ){
		Boolean PlacedBet = false
		for (def eventKey: playedGames){
			if (gameId == eventKey){
				PlacedBet = true
				break
			}
		}
		return PlacedBet
	}
}
