package com.doozi.scorena.utils
import java.text.SimpleDateFormat
import java.util.Date;

import groovyjarjarcommonscli.ParseException


class HelperService {

	Map months = ["January":0,"February":1,"March":2,"April":3,"May":4,"June":5,"July":6,"August":7,"September":8,"October":9,"November":10,"December":11]
	
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
	
	def getFirstDateOfCurrentWeek(){
		Calendar c1 = Calendar.getInstance();   // this takes current date
		c1.clear(Calendar.MINUTE);
		c1.clear(Calendar.SECOND);
		c1.clear(Calendar.MILLISECOND);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.DAY_OF_WEEK, 2);
		return c1.getTime();
	}

	def getFirstDateOfCurrentMonth(){
		Calendar c = Calendar.getInstance();   // this takes current date
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	
	def getLastDateOfCurrentMonth()
	{
		Calendar c = Calendar.getInstance();   // this takes current date
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		return c.getTime();
	}
	
	// gets first day of specified month
	def getFirstOfMonth(String month)
	{
		if(!(months.containsKey(month)))
		{
			return null;
		}
		
		Calendar c = Calendar.getInstance();   // this takes current date
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MONTH,months[month])
		c.set(Calendar.DAY_OF_MONTH,1 );
		
	    if ( getFirstDateOfCurrentMonth() < c.getTime() )
		{
		   c.add(Calendar.YEAR, -1)
		}
		
		
		return c.getTime();
	}
	
	// gets last day of specified month
	def getLastOfMonth(String month)
	{
		if(!(months.containsKey(month)))
		{
			return null;
		}
		
		Calendar c = Calendar.getInstance();   // this takes current date
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.MONTH,months[month])
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		
		if ( getLastDateOfCurrentMonth() < c.getTime() )
		{
		   c.add(Calendar.YEAR, -1)
		}

		return c.getTime();
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
