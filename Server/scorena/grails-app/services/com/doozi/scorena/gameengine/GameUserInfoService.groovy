package com.doozi.scorena.gameengine

import com.doozi.scorena.Question;
import com.doozi.scorena.controllerservice.*
import com.doozi.scorena.score.*
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
	
	Map getPastGamesUserInfo(Map game, List<BetTransaction> userBetsInTheGame, String userId, AbstractScore scoreTransaction){
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(userBetsInTheGame)
		userinfo.gameWinningAmount = getProfitInGame(game, userId, userBetsInTheGame)

		if (scoreTransaction == null){
			
			userinfo.isGameProcessed = false
			userinfo.rank=-1
			userinfo.badge=""
			userinfo.badgeScore=0
			
		}else{
			userinfo.isGameProcessed = true
			userinfo.rank = scoreTransaction.rank
			userinfo.badge = scoreTransaction.class.getSimpleName()
			
			if (GoldMetalScore.class.getSimpleName() == scoreTransaction.class.getSimpleName()){
				userinfo.badgeScore = ScoreConstant.GOLD_SCORE
			}else if (SilverMetalScore.class.getSimpleName() == scoreTransaction.class.getSimpleName()){
				userinfo.badgeScore = ScoreConstant.SILVER_SCORE
			}else if (BronzeMetalScore.class.getSimpleName() == scoreTransaction.class.getSimpleName()){
				userinfo.badgeScore = ScoreConstant.BRONZE_SCORE
			}else{
				userinfo.badgeScore = ScoreConstant.NO_SCORE
			}
		}
		return userinfo
	}
	
	private int getProfitInGame(Map game, String userId, List userBetsInTheGame){
		int profitAmount = 0
		
		for (BetTransaction bet: userBetsInTheGame){
			profitAmount += questionUserInfoService.getProfitInQuestion(game, bet)
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
