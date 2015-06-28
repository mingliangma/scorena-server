package com.doozi.scorena.gameengine

import com.doozi.scorena.Question;
import com.doozi.scorena.controllerservice.*
import com.doozi.scorena.score.*
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.PayoutTransaction
import com.doozi.scorena.FriendSystem


import org.springframework.transaction.annotation.Transactional


class GameUserInfoService {
	
	static transactional = false
	
	def betTransactionService
	def processEngineImplService
	def questionUserInfoService
	def questionPoolUtilService
	
    Map getUpcomingGamesUserInfo(String gameId, List<BetTransaction> userBetsInTheGame, String userId) {
		log.info "getUpcomingGamesUserInfo(): begins with gameId = ${gameId}, userBetsInTheGam = ${userBetsInTheGame}, userId = ${userId}"
		
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(userBetsInTheGame)
		userinfo.userWager = getWagerInGame(userBetsInTheGame)
		userinfo.followingNumPeople = getFollowingNumInAGame(userId, gameId)
		
		
		log.info "getUpcomingGamesUserInfo(): ends with userinfo = ${userinfo}"
		
		return userinfo
    }
	
	Map getPastGamesUserInfo(Map game, List<BetTransaction> userBetsInTheGame, String userId, List<AbstractScore> scoreTransactions){
		log.info "getPastGamesUserInfo(): begins with game = ${game}, userBetsInTheGame = ${userBetsInTheGame}, userId = ${userId}, scoreTransactions = ${scoreTransactions}"
		
		Map userinfo=[:]
		userinfo.placedBet = getPlacedBet(userBetsInTheGame)
		userinfo.followingNumPeople = getFollowingNumInAGame(userId, game.gameId)
		
		if (userinfo.placedBet && scoreTransactions.size() == 0){
			
			userinfo.rank= -1
			userinfo.badge= ""
			userinfo.badgeScore= -1
			userinfo.gameProfit = -1
			userinfo.totalScore = -1
			userinfo.userWager = getWagerInGame(userBetsInTheGame)
			
			
		} else if (userinfo.placedBet && scoreTransactions.size() > 0){
			userinfo.gameProfit = getUserGameProfit(game.gameId, userId)
			userinfo.badgeScore = -1
			userinfo.rank = -1
			userinfo.badge = ""
			userinfo.userWager = getWagerInGame(userBetsInTheGame)
			
			int totalScore = 0
			for (AbstractScore score : scoreTransactions){
				if (score.class.getSimpleName() == GoldMetalScore.class.getSimpleName() ||
					score.class.getSimpleName() == SilverMetalScore.class.getSimpleName() ||
					score.class.getSimpleName() == BronzeMetalScore.class.getSimpleName()){
					userinfo.badgeScore = score.score
					userinfo.rank = score.rank
					userinfo.badge = score.class.getSimpleName()
				}else if (score.class.getSimpleName() == NoMetalScore.class.getSimpleName()){
					userinfo.rank = score.rank
					userinfo.badge = score.class.getSimpleName()
					userinfo.badgeScore = score.score
				}
				totalScore += score.score
			}
			userinfo.totalScore = totalScore
			
		}else{
			userinfo.rank= -1
			userinfo.badge= ""
			userinfo.badgeScore= -1
			userinfo.gameProfit = -1
			userinfo.totalScore = -1
			userinfo.userWager = getWagerInGame(userBetsInTheGame)
		}
		
		log.info "getPastGamesUserInfo(): ends with userinfo = ${userinfo}"
		
		return userinfo
	}
	
	int getUserGameProfit(String gameId, String userId){
		List profitList = PayoutTransaction.executeQuery("select sum(p.profit) from PayoutTransaction as p where p.eventKey=? and p.account.userId=?)",[gameId, userId])
		println "getUserGameProfit(): profitList="+profitList
		if (profitList.size() > 0){
			if (profitList[0] == null){
				return 0
			}else{
				return profitList[0]
			}
		}else{
			return 0
		}
	} 
	
	private int getWagerInGame(List<BetTransaction> userBetsInTheGame){
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
	
	private int getFollowingNumInAGame(String userId, String gameId){
		//select count(*) as followingNumInAGame from `friend_system`where user_id=13 and `following_id` in (Select account_id from `abstract_transaction` where event_key='l.uefa.org.champions-2014-e.1886830')
		List followingNumInAGameResult = FriendSystem.executeQuery("SELECT count(*) FROM FriendSystem as f where f.user.userId = (:userId) and " + 
			"f.following in (select account from BetTransaction as b where b.eventKey = (:gameId))", [userId: userId, gameId: gameId])
		
		if (followingNumInAGameResult.size() == 1)
			return followingNumInAGameResult[0]
		else
			return 0

	}
}
