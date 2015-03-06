package com.doozi.scorena.tournament

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.doozi.scorena.*
import com.doozi.scorena.score.*
import com.doozi.scorena.transaction.LeagueTypeEnum

import org.springframework.transaction.annotation.Transactional

@Transactional
class TournamentService {
	def helperService
	def userService 
	def parseService   
	def scoreRankingService
	
	Map getTournamentRanking(String tournamentId){
		def userRankingWorldCup = UserRankingWCTournament.findAll("from UserRankingWCTournament UserRankingWCT order by UserRankingWCT.netGain desc, UserRankingWCT.currentBalance desc")

		List rankingResultWorldCup =[]

		
		int rankingWorldCupSize = userRankingWorldCup.size()

		List userIdList = []
		Map userIdMap = [:]
		
		
		for (int i=0; i<rankingWorldCupSize; i++){
			UserRankingWCTournament rankEntry = userRankingWorldCup.get(i)
			Account userAccount = Account.get(rankEntry.id)
			rankingResultWorldCup.add(getAccountInfoMap(userAccount.userId, userAccount.username, rankEntry.netGain,i+1))
			if (!userIdMap.containsKey(userAccount.userId)){
				userIdMap.put(userAccount.userId, "")
				userIdList
			}
		}
		
		Map userProfileResults = parseService.retrieveUserList(userIdMap)
		Map UserProfileUserIdAsKeyMap = getUserProfileUserIdAsKeyMap(userProfileResults.results)
		
		for (Map rankingAllEntry: rankingResultWorldCup){
			String accountUserId = rankingAllEntry.userId
			Map userProfile = UserProfileUserIdAsKeyMap.get(accountUserId)

			rankingAllEntry.pictureURL = ""
			
			if (userProfile != null){
				if (userProfile.display_name != null && userProfile.display_name != "")
					rankingAllEntry.username = userProfile.display_name
			
				if (userProfile.pictureURL != null && userProfile.pictureURL != "")
					rankingAllEntry.pictureURL = userProfile.pictureURL
			}
			
		}
		
		return [rank: rankingResultWorldCup]
	}
	
	def enroll(String userId, String tournamentId) {
		println "tournamentId: "+tournamentId
		Tournament tournament = Tournament.get(tournamentId.toInteger())
		
		if (tournament == null){
			return [code: 500, error: "Invalid Tournament ID"]
		}
		
		Account account = Account.findByUserId(userId)
		
		if (account == null){
			return [code: 500, error: "The user account doesn not exist"]
		}
		
		Enrollment eRecord = Enrollment.find("from Enrollment as e where e.account.userId=? and e.tournament.id=?", [userId, tournament.id])
		
		if (eRecord != null){
			return [:]
		}
		
		Enrollment enrollRecord = new Enrollment(enrollmentDate: new Date())
		
		tournament.addToEnrollment(enrollRecord)
		account.addToEnrollment(enrollRecord)
		
		if (!tournament.save(failOnError:true)){
			String errorMessage=""
			tournament.errors.each{
				println it
				errorMessage = errorMessage + " " + it
			}
			return [code: 500, error: errorMessage]
		}
		
		if (!account.save(failOnError:true)){
			String errorMessage=""
			account.errors.each{
				println it
				errorMessage = errorMessage + " " + it
			}
			return [code: 500, error: errorMessage]
		}
		return [enrollmentDate: helperService.getOutputDateFormat(enrollRecord.enrollmentDate)]
    }
	
	Map joinTournament(String userId, String tournamentId){
		Map enrollResult = enroll(userId, tournamentId)
		if (enrollResult.error){
			return enrollResult
		}
		
		Map tournamentRankingResult = getTournamentRanking(tournamentId)
		return tournamentRankingResult
		
	}
	
	List listTournaments(userId){
		List tournamentObjects = Tournament.findAll()
		List tournamentList = []
		
		for (Tournament t: tournamentObjects){
			Map tournament = [:]

			int enrollmentStatus = 0

			if (userId != null){

				Enrollment eRecord = Enrollment.find("from Enrollment as e where e.account.userId=? and e.tournament.id=?", [userId, t.id])
				if (eRecord != null){
					enrollmentStatus = 1
				}
			}
			
			tournament = [title: t.title, content: t.content, prize:t.prize, tournamentId:t.id, tournamentStatus:t.status, enrollmentStatus:enrollmentStatus, 
				startDate: t.startDate.format("yyyy-MM-dd z"), expireDate: t.expireDate.format("yyyy-MM-dd z")]
			tournamentList.add(tournament)
		}
		
		return tournamentList
	}
	
	Map getWorldCupTournament(userId){
		Tournament t = Tournament.findByType("worldcup")
		Map tournament = [:]
		int enrollmentStatus = 0

		if (userId != null){
			Enrollment eRecord = Enrollment.find("from Enrollment as e where e.account.userId=? and e.tournament.id=?", [userId, t.id])
			if (eRecord != null){
				enrollmentStatus = 1
			}
		}
		
		if (t){
		
			tournament = [title: t.title, content: t.content, prize:t.prize, tournamentId:t.id, tournamentStatus:t.status, enrollmentStatus:enrollmentStatus,
				startDate: t.startDate.format("yyyy-MM-dd z"), expireDate: t.expireDate.format("yyyy-MM-dd z")]		
		}
		return tournament
	}
	
	Map getWorldCupTournament(){
		return getWorldCupTournament(null)
	}
	
	
	
	def listTournamentEnrollment(String userId){
		log.info "TournamentService::listEnrolledTournaments() begins with userId = ${userId}"
		List<Tournament> enrolledTournamentList = listTournament(userId, EnrollmentStatusEnum.ENROLLED)
		log.info "TournamentService::listEnrolledTournaments() end with enrolledTournamentList size = ${enrolledTournamentList.size()}"
		return enrolledTournamentList
	}
	
	def listTournamentInvitation(String userId){
		log.info "TournamentService::listTournamentInvitation() begins with userId = ${userId}"
		List<Tournament> tournamentInvitationList = listTournament(userId, EnrollmentStatusEnum.INVITED)
		log.info "TournamentService::listTournamentInvitation() end with tournamentInvitationList size = ${tournamentInvitationList.size()}"
		return tournamentInvitationList
	}
	
	List<Tournament> listTournament(String userId, EnrollmentStatusEnum enrollmentStatus){
		return Tournament.findAll ("from Tournament as t where t in (select e.tournament from Enrollment as e where e.account.userId = (:userId) and e.enrollmentStatus = (:enrollmentStatus))",
			[userId: userId, enrollmentStatus:enrollmentStatus])
	}
	
	def searchTournament(String keywords){
		log.info "TournamentService::createTournament() begins"
		def t = Tournament.createCriteria()
		List<Tournament> tournamentResults = t.list {
			like("title", "%${keywords}%")
			ne("tournamentStatus", TournamentStatusEnum.EXPIRED)
		}
		log.info "TournamentService::createTournament() end"
		return tournamentResults		
	}
	
	def inviteToTournament(long tournamentId, List invitingUserIds){
		Tournament tournament = Tournament.get(tournamentId)
		if (!tournament){
			throw new RuntimeException("tournament does not exist")
		}
		if (invitingUserIds.size() > 0){			
			Date today = new Date()			
			List<Account> invitingUsers = Account.findAll("from Account as a where a.userId in (:userIds)", [userIds: invitingUserIds])
			log.info "invitingUsers=${invitingUsers}"
			for (Account invitingUser: invitingUsers){
				Enrollment enrollRecord = new Enrollment(enrollmentDate: null, createdAt: today, updatedAt: today, enrollmentStatus:EnrollmentStatusEnum.INVITED, enrollmentType:EnrollmentTypeEnum.PLAYER)
				tournament.addToEnrollment(enrollRecord)
				invitingUser.addToEnrollment(enrollRecord)
				invitingUser.save()
				tournament.save()
			}
		}
		return tournament
	}
	
	def enrollTournament(String userId, long tournamentId){
		Tournament tournament = Tournament.get(tournamentId)
		if (!tournament){
			throw new RuntimeException("tournament does not exist")
		}
		
		Date today = new Date()
		Account user = Account.find("from Account as a where a.userId in (:userIds)", [userIds: userId])

		Enrollment enrollRecord = new Enrollment(enrollmentDate: today, createdAt: today, updatedAt: today, enrollmentStatus:EnrollmentStatusEnum.ENROLLED, enrollmentType:EnrollmentTypeEnum.PLAYER)
		tournament.addToEnrollment(enrollRecord)
		user.addToEnrollment(enrollRecord)
		user.save()
		tournament.save()
		return tournament
	}
	
	def createTournament(String ownerUserId, String title, String description, List<LeagueTypeEnum> subscribedLeagues, String startDateStr, String expireDateStr, List invitingUserIds){
		log.info "TournamentService::createTournament()"
		Date startDate = helperService.parseDateFromString(startDateStr)
		Date expireDate = helperService.parseDateFromString(expireDateStr)
		
		def existTournament = Tournament.findByTitle(title)
		if (existTournament != null && existTournament.startDate == startDate && existTournament.expireDate == expireDate){			
			throw new RuntimeException("tournament already exists")
		}
				
		def tournament = new Tournament(title:title, description:description, tournamentType: TournamentTypeEnum.PRIVATE_POOL, tournamentStatus: TournamentStatusEnum.NEW, 
			startDate:startDate, expireDate:expireDate)
		for (LeagueTypeEnum l : subscribedLeagues){
			tournament.addToSubscribedLeagues(new SubscribedLeague(leagueName: l))
		}
		Date today = new Date()
		Account ownerUser = Account.findByUserId(ownerUserId)
		Enrollment ownerEnrollRecord = new Enrollment(enrollmentDate: today, tournamentEndDate:expireDate, createdAt: today, updatedAt: today, enrollmentStatus:EnrollmentStatusEnum.ENROLLED, enrollmentType:EnrollmentTypeEnum.OWNER)
		tournament.addToEnrollment(ownerEnrollRecord)
		ownerUser.addToEnrollment(ownerEnrollRecord)
		ownerUser.save()
		tournament.save()
		
		if (invitingUserIds.size() > 0){
			List<Account> invitingUsers = Account.findAll("from Account as a where a.userId in (:userIds)", [userIds: invitingUserIds])
			log.info "invitingUsers=${invitingUsers}"
			for (Account invitingUser: invitingUsers){
				Enrollment enrollRecord = new Enrollment(enrollmentDate: null, createdAt: today, updatedAt: today, enrollmentStatus:EnrollmentStatusEnum.INVITED, enrollmentType:EnrollmentTypeEnum.PLAYER)
				tournament.addToEnrollment(enrollRecord)
				invitingUser.addToEnrollment(enrollRecord)
				invitingUser.save()
				tournament.save()
			}
		}

		return tournament
	}
	
	def getTournamentRanking(long tournamentId){
		log.info "TournamentService::getTournamentRanking() begins with tournamentId = ${tournamentId}"
		return scoreRankingService.getRankingByTournament(tournamentId)
	}
	
	def acceptTournamentInvitation(String userId, long tournamentId){
		log.info "TournamentService::acceptTournamentInvitation() begins with userId = ${userId}, tournamentId = ${tournamentId}"
		Enrollment userEnrollment = Enrollment.find ("from Enrollment as e where e.tournament.id = (:tournamentId) and e.account.userId = (:userId) and e.enrollmentStatus = (:enrollmentStatus)", 
			[tournamentId: tournamentId, userId: userId, enrollmentStatus:EnrollmentStatusEnum.INVITED])
		if (userEnrollment){
			userEnrollment.enrollmentStatus = EnrollmentStatusEnum.ENROLLED
			Date now = new Date()
			userEnrollment.enrollmentDate = now
			userEnrollment.updatedAt = now
			userEnrollment.save()
			return userEnrollment
		}else{
			return null
		}
		log.info "TournamentService::listTournamentInvitation() end"
		
	}
	
	def ignoreTournamentInvitation(String userId, long tournamentId){
		log.info "TournamentService::ignoreTournamentInvitation() begins with userId = ${userId}, tournamentId = ${tournamentId}"
		Enrollment userEnrollment = Enrollment.find ("from Enrollment as e where e.tournament.id = (:tournamentId) and e.account.userId = (:userId) and e.enrollmentStatus = (:enrollmentStatus)",
			[tournamentId: tournamentId, userId: userId, enrollmentStatus:EnrollmentStatusEnum.INVITED])
		if (userEnrollment){
			userEnrollment.enrollmentStatus = EnrollmentStatusEnum.DECLINED
			Date now = new Date()
			userEnrollment.updatedAt = now
			userEnrollment.save()
			return userEnrollment
		}else{
			return null
		}
		log.info "TournamentService::ignoreTournamentInvitation() end"
		
	}
	
	private Map getAccountInfoMap(String userId, String username, int netgain, int rank){
		String netGain = ""
		if (netgain>0)
			netGain="+"+netgain.toString()
		else
			netGain=netgain.toString()
			
		return [userId: userId, username: username, gain: netGain, rank: rank]
	}
	
	private Map getUserProfileUserIdAsKeyMap(List userProfileList){
		Map UserProfileUserIdAsKeyMap = [:]
		for (Map userProfile: userProfileList){
			UserProfileUserIdAsKeyMap.put(userProfile.objectId, userProfile)
		}
		return UserProfileUserIdAsKeyMap
	}
}
