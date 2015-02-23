package com.doozi.scorena.transactionprocessengine

import org.springframework.transaction.annotation.Transactional
import com.doozi.scorena.processengine.ProcessStatus
@Transactional
class ProcessStatusService {

    boolean isReadyToProcess(String processMessage) {
		return ProcessStatus.transactionProcessStartRunning(processMessage)
    }
	
	def processCompleted(){
		ProcessStatus.transactionProcessStopped()
	}
}
