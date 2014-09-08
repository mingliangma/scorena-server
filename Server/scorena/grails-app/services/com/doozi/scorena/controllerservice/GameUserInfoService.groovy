package com.doozi.scorena.controllerservice

import com.doozi.scorena.PoolTransaction
import grails.transaction.Transactional


@Transactional
class GameUserInfoService {
	def betService
	
    Map getUpcomingGamesUserInfo(String gameId, List playedGames, String userId) {
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(gameId, playedGames)
		userinfo.userWager = getWagerInGame(gameId, userId)
		return userinfo
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
