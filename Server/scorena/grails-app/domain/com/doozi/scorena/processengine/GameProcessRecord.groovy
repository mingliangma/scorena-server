package com.doozi.scorena.processengine

import java.util.Date;

/*transProcessStatus
	0 = not processed
	1 = in process
	2 = process completed
	3 = process completed with unprocessed custom questions
	-1 = process completed with Error
	-2 = process completed with Error and unprocessed custom questions
*/
class GameProcessRecord {
	String eventKey
	TransactionProcessStatusEnum transProcessStatus
	ScoreProcessStatusEnum scoreProcessStatus
	Date lastUpdate
	Date startDateTime
	
    static constraints = {
    }
}
