package com.doozi.scorena.controllerservice
import java.util.Date;

import grails.transaction.Transactional

@Transactional
class HelperService {

    def setUTCFormat(def date) {
		return date.toString()+" UTC"		
    }
	
	def parseDateFromString(String date){
		def newerdate = new Date().parse("yyyy-MM-dd HH:mm:ss", date)
		return newerdate
	}
	
	def parseDateFromStringT(String date){
		def newerdate = new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", date)
		return newerdate
	}
	
	def getUTCCuurentTime(){
		def now = new Date()
		return now
	}
}
