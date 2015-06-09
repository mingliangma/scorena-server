package com.doozi.scorena.gameengine

import com.doozi.scorena.Account
import com.doozi.scorena.Question;
import com.doozi.scorena.processengine.GameProcessRecord
import com.doozi.scorena.score.AbstractScore
import com.doozi.scorena.transaction.BetTransaction
import com.doozi.scorena.transaction.LeagueTypeEnum
import com.doozi.scorena.processengine.*
import com.doozi.scorena.enums.*;
import com.doozi.scorena.challenge.*

import org.springframework.transaction.annotation.Transactional

class GameService {
	
	static transactional = false
	
	def sportsDataService
	def questionService
	def betTransactionService
	def helperService
	def customGameService
	def getUpcomingGamesUserInfo
	def gameUserInfoService
	def scoreService
	def parseService
	def profitRankingService
	def challengeService
	
	List listUpcomingNonCustomGames(){
		log.info "listUpcomingNonCustomGames(): begins..."
		
		List upcomingSoccerGames = sportsDataService.getAllUpcomingGames()
		List upcomingNBAGames = customGameService.getAllNonCustomUpcomingGames()
		
		List upcomingGamesResult=[]
		upcomingGamesResult.addAll(upcomingSoccerGames)
		upcomingGamesResult.addAll(upcomingNBAGames)
		
		log.info "listUpcomingNonCustomGames(): ends"
		
		return upcomingGamesResult
	}
	
	List listUpcomingGamesData(String sportType, String leagueType){
		log.info "listUpcomingGamesData(): begins with sportType = ${sportType}, leagueType = ${leagueType}"
		
		List upcomingGames = []
		List upcomingCustomGames = []
		List upcomingChampGames = []
		List upcomingTennisGames = []
		List upcomingMLBGames = []
		List upcomingNBADraftGames = []
		
		if (leagueType.toUpperCase() == LeagueTypeEnum.NBA.toString()){
			
			upcomingCustomGames = customGameService.getAllUpcomingGames()
		
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.EPL.toString()){
		
			upcomingGames = sportsDataService.getAllUpcomingGames(leagueType)
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.CHAMP.toString()){
		
			upcomingGames = sportsDataService.getAllUpcomingChampionGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.MLB.toString()){
		
			upcomingGames = sportsDataService.getAllUpcomingMLBGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.FRENCHOPEN.toString()){
			upcomingGames = sportsDataService.getAllUpcomingTennisGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.NBADRAFT.toString()){
		
			upcomingGames = sportsDataService.getAllUpcomingNBADraftGames()
			
		}else{
			upcomingChampGames = sportsDataService.getAllUpcomingChampionGames()
			upcomingTennisGames = sportsDataService.getAllUpcomingTennisGames()
			upcomingMLBGames = sportsDataService.getAllUpcomingMLBGames()
			upcomingNBADraftGames = sportsDataService.getAllUpcomingNBADraftGames()
			upcomingCustomGames = customGameService.getAllUpcomingGames()
			upcomingGames = sportsDataService.getAllUpcomingGames()
		}
		
		List upcomingGamesResult=[]
		upcomingGamesResult.addAll(upcomingGames)
		upcomingGamesResult.addAll(upcomingCustomGames)
		upcomingGamesResult.addAll(upcomingChampGames)
		upcomingGamesResult.addAll(upcomingTennisGames)
		upcomingGamesResult.addAll(upcomingMLBGames)
		upcomingGamesResult.addAll(upcomingNBADraftGames)
		
		
		log.info "listUpcomingGamesData(): ends"
		
		return upcomingGamesResult
	}
	
	List listUpcomingGames(def userId, String sportType, String leagueType){
		log.info "listUpcomingGames(): begins with userId = ${userId}, sportType = ${sportType}, leagueType = ${leagueType}"
				
		List upcomingGamesResult=listUpcomingGamesData(sportType, leagueType)		
		List upcomingGameIds = getGameIdsFromGameData(upcomingGamesResult)
		
		List<BetTransaction> betsInUpcomingGames = []
		if (upcomingGameIds!=[]){
			betsInUpcomingGames = betTransactionService.listBetTransByGameIds(upcomingGameIds)
		}
		
		println "upcomingGamesResult size="+upcomingGamesResult.size()
		for (def upcomingGame: upcomingGamesResult){
			List<BetTransaction> allBetsInGame = getAllBetsByGameId(upcomingGame.gameId, betsInUpcomingGames)
			upcomingGame.numPeople = getNumUsersInGame(allBetsInGame)
			if (userId != null){
				List<BetTransaction> userBetsInTheGame = getUserBetsFromGame(userId, allBetsInGame)
				upcomingGame.userInfo = gameUserInfoService.getUpcomingGamesUserInfo(upcomingGame.gameId, 
					userBetsInTheGame, userId)
				
				upcomingGame.challenge = challengeService.constructGameListChallengesResponse(upcomingGame.gameId, userId)
				
			}
			
		}
		
		log.info "listUpcomingGames(): ends"
		
		return upcomingGamesResult
	}
	
	
	
	def listPastGamesData(String sportType, String leagueType){
		log.info "listPastGamesData(): begins with sportType = ${sportType}, leagueType = ${leagueType}"
		
		List pastGames = []
		List pastCustomGames = []
		List pastChampGames = []
		List pastTennisGames = []
		List pastMLBGames = []
		List pastNBADraftGames = []

		if (leagueType.toUpperCase() == LeagueTypeEnum.NBA.toString()){
			
			pastCustomGames = customGameService.getAllPastGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.EPL.toString()){
		
			pastGames = sportsDataService.getAllPastGames(leagueType)
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.CHAMP.toString()){
		
			pastGames = sportsDataService.getAllPastChampionGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.MLB.toString()){
		
			pastGames = sportsDataService.getAllPastMLBGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.FRENCHOPEN.toString()){
		
			pastGames = sportsDataService.getAllPastTennisGames()
			
		}else if (leagueType.toUpperCase() == LeagueTypeEnum.NBADRAFT.toString()){
		
			pastGames = sportsDataService.getAllPastNBADraftGames()
			
		}else{
			pastCustomGames = customGameService.getAllPastGames()
			pastGames = sportsDataService.getAllPastGames()
			pastChampGames = sportsDataService.getAllPastChampionGames()
			pastTennisGames = sportsDataService.getAllPastTennisGames()
			pastMLBGames = sportsDataService.getAllPastMLBGames()
			pastNBADraftGames = sportsDataService.getAllPastNBADraftGames()
		}
		
		
		List pastGamesResult=[]	
		pastGamesResult.addAll(pastCustomGames)
		pastGamesResult.addAll(pastGames)
		pastGamesResult.addAll(pastChampGames)
		pastGamesResult.addAll(pastTennisGames)
		pastGamesResult.addAll(pastMLBGames)
		pastGamesResult.addAll(pastNBADraftGames)
		
		log.info "listPastGamesData(): ends "
		
		return pastGamesResult
	}
	
	def listPastGames(def userId, String sportType, String leagueType){
		log.info "listPastGames(): begins with userId = ${userId}, sportType = ${sportType}, leagueType = ${leagueType}"

		List pastGamesResult=listPastGamesData(sportType, leagueType)
		List pastGameIds = getGameIdsFromGameData(pastGamesResult)
		List<BetTransaction> betsInPastGames = []
		List<AbstractScore> scoreTransactionList = []
		if (pastGameIds!=[]){
			betsInPastGames = betTransactionService.listBetTransByGameIds(pastGameIds)
			scoreTransactionList = scoreService.listScoresByUserIdAndPastGames(userId, pastGameIds)
		}
		 		
		for (def pastGame: pastGamesResult){
			if (pastGame.gameStatus != EventTypeEnum.POSTEVENT.toString()){
				println "gameService::listPastGames():wrong event: "+ pastGame
				log.error "wrong event:" + pastGame
			}
			
			List<BetTransaction> allBetsInGame = getAllBetsByGameId(pastGame.gameId, betsInPastGames)
			pastGame.numPeople = getNumUsersInGame(allBetsInGame)
			
			
			if (userId != null){
				
				List<AbstractScore> userGameScores = []
				for (AbstractScore scoreTransaction: scoreTransactionList){
					if (scoreTransaction.eventKey == pastGame.gameId){
						userGameScores.add(scoreTransaction)
					}
				}
				
				
				List<BetTransaction> userBetsInTheGame = getUserBetsFromGame(userId, allBetsInGame)				
				pastGame.userInfo=gameUserInfoService.getPastGamesUserInfo(pastGame, userBetsInTheGame, userId, userGameScores)
				
				pastGame.challenge = challengeService.constructGameListChallengesResponse(pastGame.gameId, userId)
			}
			
			Boolean isGameProcessed = false
			GameProcessRecord gameRecord = GameProcessRecord.findByEventKey(pastGame.gameId)
			if (gameRecord != null){ 
				if (gameRecord.transProcessStatus == TransactionProcessStatusEnum.PROCESSED && gameRecord.scoreProcessStatus == ScoreProcessStatusEnum.PROCESSED)
					isGameProcessed = true
			}
			pastGame.isGameProcessed = isGameProcessed
		}
		
		log.info "listPastGames(): ends. game size="+pastGamesResult.size()
		
		return pastGamesResult
	}
	

	
	def listFeatureGames(userId){
		List featureGames = questionService.listFeatureQuestions(userId)
		return featureGames
	}
	
	def listFeatureGames(){
		List featureGames = questionService.listFeatureQuestions()
		return featureGames
	}
	
	Map getGame(String gameId){

		if (gameId.startsWith(sportsDataService.getLeaguePrefixFromLeagueEnum(LeagueTypeEnum.NBADRAFT)))
			return sportsDataService.getNBADraftGame(gameId)
		else if (gameId.startsWith(customGameService.CUSTOM_EVENT_PREFIX) || 
			gameId.startsWith(sportsDataService.getLeaguePrefixFromLeagueEnum(LeagueTypeEnum.NBA)))
			return customGameService.getGame(gameId)
		else if (gameId.startsWith(sportsDataService.getLeaguePrefixFromLeagueEnum(LeagueTypeEnum.MLB)))
			return sportsDataService.getMLBGame(gameId)
		else if (gameId.startsWith(sportsDataService.getLeaguePrefixFromLeagueEnum(LeagueTypeEnum.FRENCHOPEN)))
			return sportsDataService.getTennisGame(gameId)
		else if (gameId.startsWith(sportsDataService.getLeaguePrefixFromLeagueEnum(LeagueTypeEnum.CHAMP)))
			return sportsDataService.getChampionGame(gameId)
		else	
			return sportsDataService.getGame(gameId)
		
	}
	
	public List<String> getGameIdsFromGameData(List games){
		List gameIds = []
		for (Map game: games){
			gameIds.add(game.gameId)
		}
		return gameIds
	}
	
	public int getNumUsersInGame(List<BetTransaction> userBetsInGame){
		Map userMap =[:]
		for (BetTransaction bet: userBetsInGame){
			if (!userMap.containsKey(bet.account.id)){
				userMap.put(bet.account.id, "")
			}
		}
		return userMap.size()
	}
	
	public List<BetTransaction> getAllBetsByGameId(String gameId, List<BetTransaction> betsInGames){
		List<BetTransaction> allBetsInTheGame = []
		for (BetTransaction bet: betsInGames){
			if (bet.eventKey == gameId){
				allBetsInTheGame.add(bet)
			}
		}
		return allBetsInTheGame
	}
	
	public List<BetTransaction> getUserBetsFromGame(String userId, List<BetTransaction> betsInGames){
		List<BetTransaction> userBetsInTheGame = []
		for (BetTransaction bet: betsInGames){
			if (bet.account.userId == userId){
				userBetsInTheGame.add(bet)
			}
		}
		return userBetsInTheGame
	}
	
	public AbstractScore getScoreTransactionByGameId(String eventKey, List<AbstractScore> metalScoreTransactionList){
		for (AbstractScore scoreTransaction: metalScoreTransactionList){
			if (scoreTransaction.eventKey == eventKey){
				return scoreTransaction
			}
		}
		return null
	}
	

}
