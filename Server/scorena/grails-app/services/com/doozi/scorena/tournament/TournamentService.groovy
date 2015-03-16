package com.doozi.scorena.tournament

import grails.converters.JSON

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.doozi.scorena.*
import com.doozi.scorena.score.*
import com.doozi.scorena.transaction.LeagueTypeEnum

import org.codehaus.groovy.grails.web.json.JSONArray
import org.springframework.transaction.annotation.Transactional

@Transactional
class TournamentService {
	def helperService
	def userService 
	def parseService   
	def scoreRankingService
	
	
	def listTournamentEnrollment(String userId){
		log.info "TournamentService::listEnrolledTournaments() begins with userId = ${userId}"
		def enrolledTournamentList = listTournament(userId, EnrollmentStatusEnum.ENROLLED)
		enrolledTournamentList = getTournamentMetaData(enrolledTournamentList, userId)
		log.info "TournamentService::listEnrolledTournaments() end with enrolledTournamentListAsMap size = ${enrolledTournamentList.size()}"
		return enrolledTournamentList
	}
	
	def listTournamentInvitation(String userId){
		log.info "TournamentService::listTournamentInvitation() begins with userId = ${userId}"
		List<Tournament> tournamentInvitationList = listTournament(userId, EnrollmentStatusEnum.INVITED)
		tournamentInvitationList = getTournamentMetaData(tournamentInvitationList, userId)
		log.info "TournamentService::listTournamentInvitation() end with tournamentInvitationList size = ${tournamentInvitationList.size()}"
		return tournamentInvitationList
	}
	
	def listTournament(String userId, EnrollmentStatusEnum enrollmentStatus){
		return Tournament.findAll ("from Tournament as t where t in (select e.tournament from Enrollment as e where e.account.userId = (:userId) and e.enrollmentStatus = (:enrollmentStatus))",
			[userId: userId, enrollmentStatus:enrollmentStatus])
	}
	
	def searchTournament(String keywords){
		log.info "TournamentService::createTournament() begins"
		def t = Tournament.createCriteria()
		List<Tournament> tournamentList = t.list {
			like("title", "%${keywords}%")
			ne("tournamentStatus", TournamentStatusEnum.EXPIRED)
		}
		log.info "TournamentService::createTournament() end with result size = ${tournamentList.size()}"
		
		def tournamentResults = getTournamentMetaData(tournamentList)
		return tournamentResults		
	}
	
	def inviteToTournament(String userId, long tournamentId, List invitingUserIds){
		Tournament tournament = Tournament.get(tournamentId)
		if (!tournament){
			throw new RuntimeException("tournament does not exist")
		}
		if (invitingUserIds.size() > 0){
			Date today = new Date()			
			List<Account> invitingUsers = Account.findAll("from Account as a where a.userId in (:userIds)", [userIds: invitingUserIds])
			Account inviter = Account.findByUserId(userId)
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
	
	def createTournament(String ownerUserId, String title, String description, List<LeagueTypeEnum> subscribedLeagues, String startDateStr, String expireDateStr, List invitingUserIds,
		String ownerPictureUrl, String ownerAvatarCode){
		log.info "TournamentService::createTournament()"
		Date startDate = helperService.parseDateFromString(startDateStr)
		Date expireDate = helperService.parseDateFromString(expireDateStr)
		
		def existTournament = Tournament.findByTitle(title)
		if (existTournament != null && existTournament.startDate == startDate && existTournament.expireDate == expireDate){			
			throw new RuntimeException("tournament already exists")
		}
				
		def tournament = new Tournament(title:title, description:description, tournamentType: TournamentTypeEnum.PRIVATE_POOL, tournamentStatus: TournamentStatusEnum.NEW, 
			startDate:startDate, expireDate:expireDate, ownerPictureUrl:ownerPictureUrl, ownerAvatarCode:ownerAvatarCode)
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
			println "invitingUsers size: "+invitingUsers.size()
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
	
	private List getTournamentMetaData(def enrolledTournamentList){
		return getTournamentMetaData(enrolledTournamentList, null)
	}
	
	private List getTournamentMetaData(List enrolledTournamentList, String userId){
		for (Tournament t : enrolledTournamentList){
//			def ranking = scoreRankingService.getRankingByTournament(t.id)
//			for (Map userRank: ranking.rankScores){
//				if (userRank.userId == userId){
//					t.userRank = userRank.rank
//				}
//			
//			}
			int numberEnrollment = 0
			t.numberEnrollment = 0
			
			for (Enrollment e : t.enrollment){				
				if (e.enrollmentStatus == EnrollmentStatusEnum.ENROLLED){
					numberEnrollment++
				}
			}

			t.numberEnrollment = numberEnrollment
			
		}
		return enrolledTournamentList
	}
}
