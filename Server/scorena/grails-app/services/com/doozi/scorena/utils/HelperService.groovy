package com.doozi.scorena.utils
import java.text.SimpleDateFormat
import java.util.Date;
import java.util.Map;

import groovyjarjarcommonscli.ParseException


class HelperService {

	Map months = ["January":0,"February":1,"March":2,"April":3,"May":4,"June":5,"July":6,"August":7,"September":8,"October":9,"November":10,"December":11]
	Map days_31 = [January:"01",March:"03",May:"05",July:"07",August:"08",October:"10" , December:"12"]
	Map days_30 = [April:"04",June:"06",September:"09",November:"11"]
	
    String getOutputDateFormat(Date date){
		return date.format("yyyy-MM-dd HH:mm:ss z")
	}
	
	def setUTCFormat(def date) {
		return date.toString()+" UTC"		
    }
	
	def parseDateAndTimeFromString(String date){
		def newerdate = new Date().parse("yyyy-MM-dd HH:mm:ss", date)
		return newerdate
	}
	
	def parseDateAndTimeFromStringT(String date){
		def newerdate = new Date().parse("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", date)
		return newerdate
	}
	
	def parseDateFromString(String date){
		def newerdate = new Date().parse("yyyy-MM-dd", date)
		return newerdate
	}
	
	def getUTCCuurentTime(){
		def now = new Date()
		return now
	}
	
	
	Date getFirstDateOfCurrentWeek(){
		Calendar c1 = Calendar.getInstance();   // this takes current date
		c1.clear(Calendar.MINUTE);
		c1.clear(Calendar.SECOND);
		c1.clear(Calendar.MILLISECOND);
		c1.set(Calendar.HOUR_OF_DAY, 0);
		c1.set(Calendar.DAY_OF_WEEK, 2);
		return c1.getTime();
	}

	/* Gets the first day of the current month
	 * 
	 * Returns date String. i.e. Thu Jan 01 00:00:00 UTC 2015 
	 */
	Date getFirstDateOfCurrentMonth(){
		Calendar c = Calendar.getInstance();   // this takes current date
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.DAY_OF_MONTH, 1);
		return c.getTime();
	}
	
	/* Gets the last day of the current month 
	 * 
	 * Return date String. i.e. Sat Jan 31 23:59:59 UTC 2015
	 */
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
	
	/* Gets the first day of the month from specified month input value. 
	 * 
	 * input month string can be a numerical string with or without a leading 0 for single digits (i.e. 1 or 01)
	 * or can be the months string name (i.e. January, February, ... etc)
	 * 
	 * if it's currently not the specified month, and it has not occurred yet, returns last years first day of month date string
	 * Otherwise, returns the current year's first day of the specified month date string
	 * 
	 * Return date String. i.e. Thu May 01 00:00:00 UTC 2014
	 */
	Date getFirstOfMonth(String month)
	{
		int parse_month
		
		try{
			 parse_month = Integer.parseInt(month)
			 
			 if (parse_month > 12 || parse_month <= 0)
			 {
				 return null
			 } 
		}
		catch (Exception e) 
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

			if ( getLastDateOfCurrentMonth() < c.getTime() )
			{
			   c.add(Calendar.YEAR, -1)
			}
			return c.getTime();
		}
		
		Calendar c = Calendar.getInstance();   // this takes current date
		c.clear(Calendar.MINUTE);
		c.clear(Calendar.SECOND);
		c.clear(Calendar.MILLISECOND);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MONTH, parse_month - 1)
		c.set(Calendar.DAY_OF_MONTH,1 );
		
		if ( getLastDateOfCurrentMonth() < c.getTime() )
		{
		   c.add(Calendar.YEAR, -1)
		}
		return c.getTime();	
	}
	
	// gets last day of specified month
	/* Gets the last day of the month from specified month input value.
	 *
	 * input month string can be a numerical string with or without a leading 0 for single digits (i.e. 1 or 01)
	 * or can be the months string name (i.e. January, February, ... etc)
	 *
	 * if it's currently not the specified month, and it has not occurred yet, returns last years last day of month date string
	 * Otherwise, returns the current year's last day of the specified month date string
	 *
	 * Return date String. i.e.Sat May 31 23:59:59 UTC 2014
	 */
	def getLastOfMonth(String month)
	{
		int parse_month
		
		try{
			 parse_month = Integer.parseInt(month)
			 
			 if (parse_month > 12 || parse_month <= 0)
			 {
				 return null
			 }
		}
		catch (Exception e)
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
			c.set(Calendar.MONTH, parse_month - 1)
			c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
			
			if ( getLastDateOfCurrentMonth() < c.getTime() )
			{
			   c.add(Calendar.YEAR, - 1)
			}
			return c.getTime();
		}
		
		Calendar c = Calendar.getInstance();   // this takes current date
		c.set(Calendar.HOUR_OF_DAY, 23);
		c.set(Calendar.MINUTE, 59);
		c.set(Calendar.SECOND, 59);
		c.set(Calendar.MILLISECOND, 999);
		c.set(Calendar.MONTH,parse_month - 1)
		c.set(Calendar.DATE, c.getActualMaximum(Calendar.DATE));
		
		if ( getLastDateOfCurrentMonth() < c.getTime() )
		{
		   c.add(Calendar.YEAR, -1)
		}
		return c.getTime();
	}
	
	/* Gets the month string from input month and dateString.
	 *
	 * input month string can be a numerical string with or without a leading 0 for single digits (i.e. 1 or 01)
	 * or can be the months string name (i.e. January, February, ... etc)
	 *
	 * input dateString can be date string.  i.e. Thu Jan 01 00:00:00 UTC 2015 '
	 *
	 * Returns long month name and year String (i.e. December 2014, January 2015, ... etc)
	 */
	def getMonthString(String month, Date dateString )
	{
		int parse_month
		def myMonth 
		String year = dateString.toCalendar().get(Calendar.YEAR).toString()
		try{
			 parse_month = Integer.parseInt(month)
			 
			 if (parse_month > 12 || parse_month <= 0)
			 {
				 return null
			 }
		}
		catch (Exception e)
		{
			if(!(months.containsKey(month)))
			{
				return null
			}
			return month + " " + year
		}
		
		myMonth = months.find{ it.value == (parse_month -1) }?.key
		return myMonth +" "+ year
	}
	
	/* Gets the month from input month string. 
	 * 
	 * input month string can be a numerical string with or without a leading 0 for single digits (i.e. 1 or 01)
	 * or can be the months string name (i.e. January, February, ... etc)
	 * 
	 * Returns long month name String (i.e. January, February, ... etc)
	 */
	def getMonthByMonth(String month)
	{
		int parse_month
		def myMonth
		
		try{
			 parse_month = Integer.parseInt(month)
			 
			 if (parse_month > 12 || parse_month <= 0)
			 {
				 return null
			 }
		}
		catch (Exception e)
		{
			if(!(months.containsKey(month)))
			{
				return null
			}
			return month 
		}		
		myMonth = months.find{ it.value == (parse_month -1) }?.key
		return myMonth
	}
	
	/* Gets the current month string. 
	 * Returns String in long month name format. 
	 * i.e. - January
	 */
	def getMonth()
	{
		Calendar c = Calendar.getInstance();
		return new SimpleDateFormat("MMMM").format(c.getTime()).toString()
	}
	
	
	/* Gets the current month and year string.
	 * Returns String in short month name and 4 digit year format.
	 * i.e. - Jan 2015
	 */
	def getShortMonthYear()
	{
		Calendar c = Calendar.getInstance();
		return new SimpleDateFormat("MMM").format(c.getTime()).toString() + " " + c.get(Calendar.YEAR).toString()
	}
	
	/* Gets the previous month and year string.
	 * Returns String in short month name and 4 digit year format.
	 * i.e. - Dec 2014
	 */
	def getPreviousMonthAndYear()
	{
		int previousValue = 0
		String previousMonth = getMonth()
		previousValue = months[previousMonth] - 1
		
		Calendar c = Calendar.getInstance()
		
		if (previousValue < 0)
		{
			previousValue = 11
			c.add(Calendar.YEAR, - 1)
		}

		c.set(Calendar.MONTH, previousValue)

		return new SimpleDateFormat("MMM").format(c.getTime()).toString() + " " + c.get(Calendar.YEAR).toString()
	}
	
	/* Gets the previous month string.
	 * Returns String in long month name.
	 * i.e. - December
	 */
	def getPreviousMonth()
	{
		int previousValue = 0
		String previousMonth = getMonth()
		previousValue = months[previousMonth] - 1
		
		Calendar c = Calendar.getInstance()
		
		if (previousValue < 0)
		{
			previousValue = 11
		}

		c.set(Calendar.MONTH, previousValue)

		return new SimpleDateFormat("MMMM").format(c.getTime()).toString()
	}
	
	/* evaluates date query string based on month and year
	 * 
	 * Returns date search string from first day of month to last day of month
	 * i.e. '2015-01-01 00:00:00' AND '2015-01-31 23:59:59'
	 */
	public String evalDate(String month, String year)
	{
		String date_search = ""
		String MM = ""
		String DD = ""
	
		if (month.equals("February"))
		{
			MM = "02"
			
			if (leapyear(Integer.parseInt(year)))
			{
				DD = "29"
			}
			else
			{
				DD = "28"
			}
			
			date_search = "'"+year+"-"+MM+"-01 00:00:00' AND '"+year+"-"+MM+"-"+DD+" 23:59:59'"
			
			return date_search
		}
		
		else
		{
			Map datecheck = getMonthDayMap(month)
						
			MM = datecheck['month']
			DD = datecheck['day']
			
			// checks if month is
			if(MM == null)
			{
				return null
			}
			
			date_search =  "'"+year+"-"+MM+"-01 00:00:00' AND '"+year+"-"+MM+"-"+DD+" 23:59:59'"
			
			return date_search
		}
	}
	
	/* Gets month day map
	 * Returns map of month with month numerical value and day count. i.e. [month:01, day:"31"] 
	 */
	public Map getMonthDayMap(String month)
	{
		if (!days_31.containsKey(month))
		{
			String mm = days_30[month]
			return [ month:mm, day:"30"]
		}
		
		if (!days_30.containsKey(month))
		{
			String mm = days_31[month]
			return [month:mm, day:"31"]
		}
	}
	
	/* checks if year is a leap year
	 */
	public boolean leapyear(int year)
	{
		if(year%400 == 0)
		{
			return true
		}
		
		if(year%100 == 0)
		{
			return false
		}
		
		if(year%4 == 0)
		{
			return true
		}
		
		return false;
	}
	
	
		
	boolean dateValidator(String date){

		try {
		   Date.parse('yyyy-MM-dd', date)
		   return true
		} catch (Exception e) {
		   return false
		}
		 
	}
	
	boolean dateAndTimeValidator(String date){
		
		try {
		   Date.parse('yyyy-MM-dd HH:mm:ss', date)
		   return true
		} catch (Exception e) {
		   return false
		}
		 
	}
	
	Map listToMap(List l) {
		Map result = [:]
		for (item in l){
			result[item]=item
		}
		return result
	}
}
