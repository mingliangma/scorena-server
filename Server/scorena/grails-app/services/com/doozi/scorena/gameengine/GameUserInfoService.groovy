package com.doozi.scorena.gameengine

import com.doozi.scorena.Question;
import com.doozi.scorena.controllerservice.*
import com.doozi.scorena.score.*
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction


import org.springframework.transaction.annotation.Transactional


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
	
	Map getPastGamesUserInfo(Map game, List<BetTransaction> userBetsInTheGame, String userId, List<AbstractScore> scoreTransactions){
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(userBetsInTheGame)

		if (userinfo.placedBet && scoreTransactions.size() == 0){
			
			userinfo.isGameProcessed = false
			userinfo.rank= null
			userinfo.badge= null
			userinfo.badgeScore= null
			userinfo.gameWinningAmount = null
			userinfo.totalScore = null
			
		} else if (userinfo.placedBet && scoreTransactions.size() > 0){
//			userinfo.gameWinningAmount = getProfitInGame(game, userId, userBetsInTheGame)	
			userinfo.gameWinningAmount = PayoutTransaction.executeQuery("select sum(p.transactionAmount) from PayoutTransaction as p where p.eventKey=? and p.account.userId=?)",[game.gameId, userId])[0]
			userinfo.isGameProcessed = true
			
			int totalScore = 0
			for (AbstractScore score : scoreTransactions){
				if (score.class.getSimpleName() == GoldMetalScore.class.getSimpleName() ||
					score.class.getSimpleName() == SilverMetalScore.class.getSimpleName() ||
					score.class.getSimpleName() == BronzeMetalScore.class.getSimpleName()){
					userinfo.badgeScore = score.score
					userinfo.rank = score.rank
					userinfo.badge = score.class.getSimpleName()
				}
				totalScore += score.score
			}
			userinfo.totalScore = totalScore
			
		}else{
			userinfo.isGameProcessed = false
			userinfo.rank= null
			userinfo.badge= null
			userinfo.badgeScore= null
			userinfo.gameWinningAmount = null
			userinfo.totalScore = null
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
