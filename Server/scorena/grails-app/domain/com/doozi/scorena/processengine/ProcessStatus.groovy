package com.doozi.scorena.processengine

class ProcessStatus {
	int processCode
	ProcessStatusEnum processStatus
	Date updatedAt
	String message
	
	public static final int TRANSACTIONPROCESS_CODE = 1

	
	
    static constraints = {
		message nullable: true
    }

	static def initProcessStatus(){
		println "initProcessStatus() begins..."
		if (ProcessStatus.count == 0){
			println "initProcessStatus(): count == 0"
			def process = new ProcessStatus(processCode: TRANSACTIONPROCESS_CODE, processStatus: ProcessStatusEnum.STOPPED, 
				updatedAt:new Date(), message:" ")
			if (process.save()){
				System.out.println("initProcessStatus successfully saved")
			}else{
				System.out.println("initProcessStatus save failed")
				process.errors.each{
					println it
				}
			}
		}else{
			println "initProcessStatus(): process stop"
			transactionProcessStopped()
		}
	}
	
	static boolean transactionProcessStartRunning(){
		return transactionProcessStartRunning("")
	}
	static boolean transactionProcessStartRunning(String message){
		def sProcess = ProcessStatus.findByProcessCode(TRANSACTIONPROCESS_CODE)
		if (sProcess.processStatus == ProcessStatusEnum.STOPPED){
			sProcess.processStatus = ProcessStatusEnum.RUNNING
			sProcess.updatedAt = new Date()
			sProcess.message = message
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
		sProcess.updatedAt = new Date()
		if (!sProcess.save()) {
			sProcess.errors.each {
				println it
			}
		}
	}
}
