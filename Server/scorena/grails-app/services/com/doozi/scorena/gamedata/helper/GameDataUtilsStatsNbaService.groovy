package com.doozi.scorena.gamedata.helper

import org.springframework.transaction.annotation.Transactional
import com.doozi.scorena.enums.EventTypeEnum;
class GameDataUtilsStatsNbaService {

    String toScorenaEventStatus(int statsNbaEventStatusId) {
		String scorenaEventStatus=""
		switch (statsNbaEventStatusId){
			case 1: //prevent
				scorenaEventStatus = EventTypeEnum.PREEVENT.toString()
				return scorenaEventStatus
			case 2:
				scorenaEventStatus = EventTypeEnum.MIDEVENT.toString()
				return scorenaEventStatus
			case 3:
				scorenaEventStatus = EventTypeEnum.POSTEVENT.toString()
				return scorenaEventStatus
			default:
				log.error "unrecognized statsNbaEventStatusId: ${statsNbaEventStatusId}"
		}
		return scorenaEventStatus
		
    }
}
