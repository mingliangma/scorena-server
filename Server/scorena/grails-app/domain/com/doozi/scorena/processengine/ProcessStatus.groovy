package com.doozi.scorena.processengine

class ProcessStatus {
	int processCode
	ProcessStatusEnum processStatus
	
	public static final int TRANSACTIONPROCESS_CODE = 1

	
	
    static constraints = {
    }

	static def initProcessStatus(){
		if (ProcessStatus.count == 0){
			def process = new ProcessStatus(processCode: TRANSACTIONPROCESS_CODE, processStatus: ProcessStatusEnum.STOPPED)
			process.save()

		}
	}
	
	static boolean transactionProcessStartRunning(){
		def sProcess = ProcessStatus.findByProcessCode(TRANSACTIONPROCESS_CODE)
		if (sProcess.processStatus == ProcessStatusEnum.STOPPED){
			sProcess.processStatus = ProcessStatusEnum.RUNNING
			sProcess.save(flush:true)
			return true
		}else{
			return false
		}
	}
	
	static def transactionProcessStopped(){
		def sProcess = ProcessStatus.findByProcessCode(TRANSACTIONPROCESS_CODE)
		println "processStatus="+sProcess.processStatus
		sProcess.processStatus = ProcessStatusEnum.STOPPED
		sProcess.save()
	}
}
