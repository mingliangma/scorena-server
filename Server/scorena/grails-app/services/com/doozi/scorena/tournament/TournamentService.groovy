package com.doozi.scorena.tournament

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.doozi.scorena.*

import grails.transaction.Transactional

@Transactional
class TournamentService {
	def helperService
	def userService 
	def parseService   
	
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
	
	def listTournaments(){
		listTournaments(null)
	}
	
	def createTournament(String title, String content, String type, String prize, String startDateStr, String expireDateStr){
		println "TournamentService::createTournament()"
		Date startDate = helperService.parseDateFromString(startDateStr)
		Date expireDate = helperService.parseDateFromString(expireDateStr)
		
		def existTournament = Tournament.findByTitle(title)
		if (existTournament != null && existTournament.startDate == startDate && existTournament.expireDate == expireDate){
			return [code: 500, error: "tournament already exists"]
		}
		
		def tournament = new Tournament(title:title, content:content, type: type, prize:prize, status:0, startDate:startDate, expireDate:expireDate, sport:"soccer")
		if (!tournament.save(failOnError:true)){
			String errorMessage=""
			tournament.errors.each{
				println it
				errorMessage = errorMessage + " " + it
			}
			return [code: 500, error: errorMessage] 
		}
		
		return [title:title, content:content, type: type, prize:prize, status:0, startDate:startDate, expireDate:expireDate]
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
