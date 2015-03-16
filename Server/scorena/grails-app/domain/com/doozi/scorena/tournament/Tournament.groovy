package com.doozi.scorena.tournament

import com.doozi.scorena.transaction.LeagueTypeEnum

class Tournament {
	String title
	String description	
	TournamentTypeEnum tournamentType
	TournamentStatusEnum tournamentStatus
	Collection subscribedLeagues // Since uniqueness and order aren't managed by Hibernate, adding to or removing from collections mapped as a Bag don't trigger a load of all existing instances from the database, so this approach will perform better and require less memory than using a Set or a List.
	Date startDate
	Date expireDate
	String ownerPictureUrl
	String ownerAvatarCode
	
	//non-persistence properties
	String userRank
	int numberEnrollment
	
	static hasMany = [enrollment: Enrollment, subscribedLeagues: SubscribedLeague]
	
    static constraints = {
		description nullable: true
		ownerPictureUrl nullable: true
		ownerAvatarCode nullable: true
    }
	
	static transients = ['userRank', 'numberEnrollment']
	
//	static marshalling={
//		shouldOutputIdentifier false
//		shouldOutputVersion false
//		shouldOutputClass false
//		deep 'subscribedLeagues'
////		ignore "enrollment"
//		serializer{  // cusomtize the name output to all caps for our 'special report'
//			tournamentType { value, json ->
//					json.value("${value.tournamentType.toString()}")
//				}
//			tournamentStatus { value, json ->
//				json.value("${value.tournamentStatus.toString()}")
//			}
//		}
//		virtual{     // add a virtual property, in this case a date/time stamp
//			tournamentId { value, json -> json.value("${value.id}") }
////			userRank { value, json -> json.value("") }
////			playerPitureUrl { value, json -> json.value("") }
////			avatarCode { value, json -> json.value("") }
//		}
//	}
}
