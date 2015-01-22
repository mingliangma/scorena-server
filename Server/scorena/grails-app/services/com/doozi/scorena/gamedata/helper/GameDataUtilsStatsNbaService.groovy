package com.doozi.scorena.gamedata.helper

import org.springframework.transaction.annotation.Transactional

class GameDataUtilsStatsNbaService {

    String toScorenaEventStatus(int statsNbaEventStatusId) {
		String scorenaEventStatus=""
		switch (statsNbaEventStatusId){
			case 1: //prevent
				scorenaEventStatus = GameDataConstants.PREEVENT_NAME
				return scorenaEventStatus
			case 2:
				scorenaEventStatus = GameDataConstants.MIDEVENT_NAME
				return scorenaEventStatus
			case 3:
				scorenaEventStatus = GameDataConstants.POSTEVENT_NAME
				return scorenaEventStatus
			default:
				log.error "unrecognized statsNbaEventStatusId: ${statsNbaEventStatusId}"
		}
		return scorenaEventStatus
		
    }
}
