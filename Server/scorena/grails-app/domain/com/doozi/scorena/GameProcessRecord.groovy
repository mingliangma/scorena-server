package com.doozi.scorena

import java.util.Date;

/*transProcessStatus
	0 = not processed
	1 = in process
	2 = process completed
	-1 = process completed with Error
*/
class GameProcessRecord {
	String eventKey
	int transProcessStatus
	Date lastUpdate
	Date startDateTime
	
    static constraints = {
    }
}
