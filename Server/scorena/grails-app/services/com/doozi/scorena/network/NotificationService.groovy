package com.doozi.scorena.network

import grails.plugins.rest.client.RestBuilder
import com.doozi.scorena.enums.EventTypeEnum;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.transaction.annotation.Transactional

import com.doozi.scorena.Account
import com.doozi.scorena.Question
import com.doozi.scorena.communication.Notification
import com.doozi.scorena.communication.NotificationTypeEnum;
import com.doozi.scorena.*;
import com.doozi.scorena.tournament.*
import com.doozi.scorena.transaction.*
@Transactional
class NotificationService {
	def pushService
	def gameService
	def friendSystemService
	def betTransactionService
	def parseService
	
	def newFollowNotification(String followingAccountUserId, String meAccountUserId, String meAccountDisplayName){
		log.info "newFollowNotification(): begins with followingAccountUserId=${followingAccountUserId}, "+
		"meAccountUserId=${meAccountUserId}, meAccountDisplayName=${meAccountDisplayName}"
		String msg = meAccountDisplayName + " is now following you on Scorena."
		def rest = new RestBuilder()
		pushService.sendFollowPush(rest,followingAccountUserId,meAccountUserId,msg)
		storeNewFollowNotification(followingAccountUserId, msg)
//		storeNewFollowNotification(followingAccountUserId, msg)
	}
	
	def newCommentNotification(String commenterUserId, String commenterDisplayName, String questionId, String eventKey){
		log.info "newCommentNotification(): begins with commenterUserId=${commenterUserId}, "+
		"commenterDisplayName=${commenterDisplayName}, questionId=${questionId}, eventKey=${eventKey}"
		
		def friends = friendSystemService.listFollowers(commenterUserId)
		List<String> userIdList = betTransactionService.listAllBetUserIdsByQId(questionId.toLong())
		def game = gameService.getGame(eventKey)
		
		String status = game.gameStatus
		String home = game.home.teamname
		String away = game.away.teamname
		
		String msg = commenterDisplayName + " has just commented on the "+ away +" vs "+ home+" game."

		List<String> pushUsers = []
		
		if (userIdList != [])
		{
			for(String userId:userIdList )
			{
				if (userId != commenterUserId)
					pushUsers.add(userId)
			}
			def rest = new RestBuilder()
			pushService.userCommentPush(rest, pushUsers, eventKey,status, questionId.toString(), msg )
			storeNotification(pushUsers, msg, NotificationTypeEnum.NEW_COMMENT, eventKey, questionId)
		}
	}
	
	def tournamentInvitationNotification(long tournamentId, String userId, String displayName){
		log.info "tournamentInvitationNotification() begins with tournamentId=${tournamentId}, userId=${userId}, displayName=${displayName}"
		def rest =  new RestBuilder()
		String message = "${displayName} is inviting you to a tournament"
		pushService.tournamentInvitationNotification(rest,tournamentId, userId, message)

		storeTournamentNotification([userId], message, NotificationTypeEnum.TOURNAMENT_INVITATION, tournamentId.toString())
	}
	
	def tournamentAcceptanceNotification(long tournamentId, Enrollment userEnrollment, String displayName){
		
		Enrollment ownerEnrollment = Enrollment.find ("from Enrollment as e where e.tournament.id = (:tournamentId) and e.enrollmentType = (:enrollmentType)",
			[tournamentId: tournamentId, enrollmentType:EnrollmentTypeEnum.OWNER])
		String ownerUserId = ownerEnrollment.account.userId
		Tournament t = ownerEnrollment.tournament
		String title = t.title
		String description = t.description
		Date startDate = t.startDate
		Date expireDate = t.expireDate
		List subscribedLeagues = []

		for(SubscribedLeague sl:t.subscribedLeagues){
			subscribedLeagues.add(sl.leagueName)
		}
		
		String message = "${displayName} joined ${title} tournament"

		pushService.acceptTournamentNotification(new RestBuilder(),tournamentId, ownerUserId, message, title, description, 
			startDate, expireDate, subscribedLeagues.toString())
		storeTournamentNotification([ownerUserId], message, NotificationTypeEnum.TOURNAMENT_ACCEPTANCE, tournamentId.toString())
	}
	
	def challengeInvitationNotification(long challengeId, String challengeeUserId, String challengerDisplayName, 
		String eventKey){
		log.info "challengeInvitationNotification() begins with challengeId=${challengeId}, " +
		"challengeeUserId=${challengeeUserId}, challengerDisplayName=${challengerDisplayName}"
		
		Map game = gameService.getGame(eventKey)
		
		String homeTeam = game.home.teamname
		String awayTeam = game.away.teamname
		
		def rest =  new RestBuilder()
		String message = "${challengerDisplayName} just challenged you in the game ${homeTeam} vs ${awayTeam}. "+
		"Think you can beat ${challengerDisplayName}?"
		pushService.challengeInvitationNotification(rest,challengeId, challengeeUserId, message)

		storeChallengeNotification([challengeeUserId], message, NotificationTypeEnum.CHALLENGE_INVITATION, challengeId.toString())
	}
	
	def challengeAcceptanceNotification(long challengeId, String challengerUserId, String challengeeDisplayName
		, String homeTeam, String awayTeam){
		log.info "challengeAcceptanceNotification() begins with challengeId=${challengeId}, " +
		"challengerUserId=${challengerUserId}, challengeeDisplayName=${challengeeDisplayName}"

		String message = "${challengeeDisplayName} accepted your challenge in the game ${homeTeam} vs ${awayTeam}. Game is on!"

		pushService.acceptChallengeNotification(new RestBuilder(),challengeId, challengerUserId, message)
		storeChallengeNotification([challengerUserId], message, NotificationTypeEnum.CHALLENGE_ACCEPTANCE, challengeId.toString())
	}
	
	
	public void gameResultNotification(Map userTotalGameProfit, Map gameIdToGameInfoMap)
	{
		log.info "sendEndGamePush(): begins and "+gameIdToGameInfoMap.size()+" games will be pushed"
		def rest = new RestBuilder()

		//userTotalGamesProfit = [gameId : [userId: game profit]]
		Set gameIds =  userTotalGameProfit.keySet()
		for (String gameId: gameIds){
			Map userAGameProfit = userTotalGameProfit[gameId]
			String status = gameIdToGameInfoMap[gameId].gameStatus
			// println("status - " + status)

			String awayTeam = gameIdToGameInfoMap[gameId].away.teamname
			String homeTeam = gameIdToGameInfoMap[gameId].home.teamname
			String[] userIdKeys = userAGameProfit.keySet()

			//			 List userRanks = profitRankingService.getGameRanking(gameId)
			//
			//			 for (Map rank: userRanks)
			//			 {
			//				 String msg = ""
			//				 switch (rank.rank)
			//				 {
			//					 case 1:
			//						 msg ="You have ranked: " + rank.rank + "st in game "+ awayTeam +" vs "+ homeTeam
			//						 break;
			//					 case 2:
			//						 msg ="You have ranked: " + rank.rank + "nd in game "+ awayTeam +" vs "+ homeTeam
			//						 break;
			//					 case 3:
			//						 msg ="You have ranked: " + rank.rank + "rd in game "+ awayTeam +" vs "+ homeTeam
			//						 break;
			//					default:
			//						msg ="You have ranked: " + rank.rank + "th in game "+ awayTeam +" vs "+ homeTeam
			//						break;
			//				 }
			//
			//				 println(rank.userId + " " + msg)
			//				 def payoutPush = endOfGamePush(rest,gameId, status ,rank.userId, msg)
			//			 }

			for (String userID: userIdKeys )
			{
				int gameProfit = userAGameProfit[userID]
				String msg = ""
				if ( gameProfit > 0)
				{
					msg = "Congratulations! You have won " + gameProfit +" Coins in game "+ awayTeam +" vs "+ homeTeam
				}

				else if (gameProfit == 0)
				{
					msg = "Sorry, you did not win any Coins in game "+ awayTeam +" vs "+ homeTeam
				}

				else
				{
					msg = "Sorry, You have lost "+ Math.abs(gameProfit) +" Coins in game "+ awayTeam +" vs "+ homeTeam
				}

				// sends end of game push to user with amount of coins won or lost
				def payoutPush = pushService.endOfGamePush(rest,gameId, status ,userID, msg)
				storeNotification([userID], msg, NotificationTypeEnum.GAMERESULT, gameId)

			}
		}
		log.info "sendEndGamePush(): ends"
	}
	
	def inactiveUsersReminder(){

		List<String> inactiveUserIds = Account.executeQuery("select userId from Account as a where  a.id > 30 and a.id not in "+
				"(select t.account.id from BetTransaction as t where t.createdAt > ? group by t.account.id)", [new Date() -3])

		if (inactiveUserIds.size() > 0){
			List<Map> upcomingNBAGames = gameService.listUpcomingGamesData("", LeagueTypeEnum.NBA.toString())
			if (upcomingNBAGames.size() > 0){
				def rest = new RestBuilder()
				Map upcomingNBAGame = upcomingNBAGames.get(0)
				List<Question> questions = Question.findAllByEventKey(upcomingNBAGame.gameId)
				if (questions.size() > 3){
					Question q = questions.last()
					String message = q.questionContent.content + " "+q.pick1 + " or " + q.pick2
					pushService.questionPushTargetUserId(rest, inactiveUserIds, upcomingNBAGame.gameId, upcomingNBAGame.gameStatus, message)
					storeNotification(inactiveUserIds, message, NotificationTypeEnum.INACTIVE_USER_REMINDER, q.id.toString())
				}else{
					Question q = questions.first()
					String message = q.questionContent.content + " "+q.pick1 + " or " + q.pick2
					pushService.questionPushTargetUserId(rest, inactiveUserIds, upcomingNBAGame.gameId, upcomingNBAGame.gameStatus, message)
					storeNotification(inactiveUserIds, message, NotificationTypeEnum.INACTIVE_USER_REMINDER, q.id.toString())
				}
			}
		}
	}

	def friendBetReminder(String userId, int userWager, int userPick, long questionId){
		log.info "friendBetReminder(): begins with userId = ${userId}, userWager = ${userWager} ,userPick = ${userPick},questionId = ${questionId}"
		Account userMadeBet = Account.findByUserId(userId)
		Question q = Question.get(questionId)
		if (q.questionContent.questionType == QuestionContent.SCOREGREATERTHAN_SOCCER ||
		q.questionContent.questionType == QuestionContent.SCOREGREATERTHAN_BASKETBALL ||
		q.questionContent.questionType == QuestionContent.HIGHERREBOUNDS_BASKETBALL ||
		q.questionContent.questionType == QuestionContent.CUSTOMSURVEY ){

			log.info "friendBetReminder(): skip for question type = ${q.questionContent.questionType}"
			return []
		}
		String query = "select userId from Account as a where "+
				"a.id in (select f.user.id from FriendSystem as f where f.following.id = :userMadeBet and f.user.id not in "+
				"(select t.account.id from BetTransaction as t where t.createdAt > :inactiveDays group by account.id) "+
				") "+
				" and "+
				"a.userId not in (select userId from Notification as n where n.createdAt > :notificationMadeDate and "+
				"n.notificationType = :ntype group by userId)"


		List<String> usersToBeNotified = Account.executeQuery(query, [userMadeBet: userMadeBet.id, inactiveDays: new Date() -3,
			notificationMadeDate: new Date() - 1, ntype: NotificationTypeEnum.FRIENDBET])


		if (usersToBeNotified == []){
			log.info "friendBetReminder(): usersToBeNotified is zero"
			return []
		}
		Map game = gameService.getGame(q.eventKey)
		String message = ""
		if (q.questionContent.questionType == QuestionContent.WHOWIN){
			if (userPick == 1){
				message = userMadeBet.displayName + " put ${userWager} coins on ${q.pick1} in "+
						"${game.home.teamname} vs ${game.away.teamname}"
			}else if (userPick == 2){
				message = userMadeBet.displayName + " put ${userWager} coins on ${q.pick2} in "+
						"${game.home.teamname} vs ${game.away.teamname}"
			}else{
				log.error "friendBetReminder: userPick is neither 1 or 2"
				return []
			}
		}else{
			String questionContent = q.questionContent.content
			questionContent = questionContent.replace("?", "")

			if (userPick == 1){
				message = userMadeBet.displayName + " put ${userWager} coins on ${q.pick1} for ${questionContent} in "+
						"${game.home.teamname} vs ${game.away.teamname}"
			}else if (userPick == 2){
				message = userMadeBet.displayName + " put ${userWager} coins on ${q.pick2} for ${questionContent} in "+
						"${game.home.teamname} vs ${game.away.teamname}"
			}else{
				log.error "friendBetReminder: userPick is neither 1 or 2"
				return []
			}
		}
		log.info "friendBetReminder(): message = "+message

		def rest = new RestBuilder()
		pushService.questionPushTargetUserId(rest, usersToBeNotified, q.eventKey, EventTypeEnum.POSTEVENT.toString(), message)
		storeNotification(usersToBeNotified, message, NotificationTypeEnum.FRIENDBET, q.eventKey, q.id.toString())
		log.info "friendBetReminder(): ends"
		return  usersToBeNotified
	}

	private def storeNotification(List<String> userIds, String message, NotificationTypeEnum notificationType, 
		String eventKey, String questionId, String tournamentId, String challengeId){
		log.info "storeNotification(): begins with userIds=${userIds}, message=${message}, notificationType=${notificationType}, eventKey=${eventKey}"
		Date createdAt = new Date()
		for (String userId: userIds){
			println "userId:"+userId
			Notification n = new Notification(userId: userId, message:message, notificationType:notificationType, eventKey:eventKey, 
				createdAt:createdAt, questionId:questionId, tournamentId:tournamentId, challengeId:challengeId)
			
			println "Notification"
			if (!n.save()) {
				Notification n1 = new Notification(userId: userId, message:message, notificationType:notificationType, eventKey:eventKey,
					createdAt:createdAt, questionId:questionId, tournamentId:tournamentId, challengeId:challengeId)
				if (!n1.save()) {
					n1.errors.each {
						println it
					}
					
				}else{
					println "success"
				}
				
			}else{
				println "success"
			}
		}
		log.info "storeNotification(): ends"
	}
	
	private def storeChallengeNotification(List<String> userIds, String message, NotificationTypeEnum notificationType, String challengeId){
		storeNotification(userIds, message, notificationType, null, null, null, challengeId)
	}
	
	private def storeTournamentNotification(List<String> userIds, String message, NotificationTypeEnum notificationType, String tournamentId){
		storeNotification(userIds, message, notificationType, null, null, tournamentId, null)
	}
	
	private def storeNewFollowNotification(String followingAccountUserId, String msg){		
		storeNotification([followingAccountUserId], msg, NotificationTypeEnum.NEW_FOLLOW, null, null, null, null)
	}
	
	private def storeNotification(List<String> userIds, String message, NotificationTypeEnum notificationType, String eventKey){
		storeNotification(userIds, message, notificationType, eventKey, null, null, null)	
	}
	
	private def storeNotification(List<String> userIds, String message, NotificationTypeEnum notificationType, String eventKey, String questionId){
		storeNotification(userIds, message, notificationType, eventKey, questionId, null, null)
	}
}
