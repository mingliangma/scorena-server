package com.doozi.scorena.tournament

import com.doozi.scorena.transaction.LeagueTypeEnum

class SubscribedLeague {
	LeagueTypeEnum leagueName
	
	static belongsTo = [tournament: Tournament]
		
    static constraints = {
    }
	
	static marshalling={
		shouldOutputIdentifier false
		shouldOutputVersion false
		shouldOutputClass false
		ignore "enumType", "tournament"
		serializer{  // cusomtize the name output to all caps for our 'special report'
			leagueName { value, json -> 
					json.value("${value.leagueName.toString()}") 
				}
	   }
	}
}
