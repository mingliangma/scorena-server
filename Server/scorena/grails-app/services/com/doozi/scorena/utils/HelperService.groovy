package com.doozi.scorena.utils
import java.util.Date;

import grails.transaction.Transactional
import groovyjarjarcommonscli.ParseException

@Transactional
class HelperService {

    String getOutputDateFormat(Date date){
		return date.format("yyyy-MM-dd HH:mm:ss z")
	}
	
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
	
	boolean dateValidator(String date){

		try {
		   Date.parse('yyyy-MM-dd HH:mm:ss', date)
		   return true
		} catch (Exception e) {
		   return false
		}
		 
	}
}
