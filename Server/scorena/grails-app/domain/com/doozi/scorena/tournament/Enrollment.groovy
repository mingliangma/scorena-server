package com.doozi.scorena.tournament
import com.doozi.scorena.Account

class Enrollment {
	Date enrollmentDate
	Date createdAt
	Date updatedAt
	EnrollmentStatusEnum enrollmentStatus
	EnrollmentTypeEnum enrollmentType
	
	static belongsTo = [tournament: Tournament, account: Account]
	
    static constraints = {
		enrollmentDate nullable: true
		account unique: 'tournament'
    }
	
	static marshalling={
		shouldOutputIdentifier false
		shouldOutputVersion false
		shouldOutputClass false
		deep "account"
		ignore "tournament", "tournamentEndDate"
		serializer{  // cusomtize the name output to all caps for our 'special report'
			enrollmentType { value, json ->
					json.value("${value.enrollmentType.toString()}")
				}
			enrollmentStatus { value, json ->
				json.value("${value.enrollmentStatus.toString()}")
			}
		}

	}
}
