package com.doozi.scorena.iap

import java.text.SimpleDateFormat
import java.util.Date;

class IapConst {
	// Coin Packages
	public static final int COIN_PK_20K = 20000
	public static final int COIN_PK_45K = 45000
	public static final int COIN_PK_100K = 100000
	
	// URL for IOS Sandbox receipt verification
	public static final String IOS_Sandbox = "https://sandbox.itunes.apple.com/verifyReceipt"
	
	// URL for IOS Production receipt verification
	public static final String IOS_Production = "https://buy.itunes.apple.com/verifyReceipt"
	
	// Public Key for the iap test app in google play store
	String alpha_pk = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAh1RX3rE7GAbGRiMglxVbPYI1XAcr3o1/gr6Mzc70Ut3c+zv7gnKUhpnXEZz/1x8KY8hGNxFIPQebo/KVI0WuKBXy+hSA52RlSspWZIhYRduE7RFZHzigSOrKGc0bUnwryHb4x9p9R71QDtQd/KbP3hEbOwQt/l7SLj/lk7LpmlBsI5jM4n65yrJJGoG61+1F9VaujhFf4/7TTk/GWmIKGgT3FA4TqNtfZ+aJj5bCy1IvdKU7oBgGd88O6oWPYSf+iQ/HSXIGeKnk17/Lcqd++BmEoRVtCT315KsyOqWFpvqRxyMHP0iWX5SBCUnPHZW+twYjfP3dLQ/aWY3YHW+b0QIDAQAB"
	
	// TODO: replace with real public key for Scorena app
	String pk = ""
	
	// returns android app public key
	public String getPk()
	{
		
		return alpha_pk
		
	}
	
	// returns IOS sandbox url
	public String getSandbox()
	{
		return IOS_Sandbox
	}
	
	// returns IOS production url
	public String getProduction()
	{
		return IOS_Production
	}
	
	// returns 20k coin pack value 
	public int get20k()
	{
		return COIN_PK_20K
	}
	
	// returns 45k coin pack value
	public int get45k()
	{
		return COIN_PK_45K
	}
	
	// returns 100k coin pack value
	public int get100k()
	{
		return COIN_PK_100K
	}
	
	/* removes time zone and converts date string to UTC date time
	 * @param date - date string 
	 */
	public Date convertString (String date)
	{

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
		
		Date d = sdf.parse(date)
		
		String tz_removed = sdf.format(d)
		
		Date purchase = sdf.parse(tz_removed)
		
		return purchase
	}
	
	/* converts timestamp string to UTC date time
	 * @param timestamp - timestamp in milliseconds
	 */
	public Date convertMS(String timestamp)
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"))
		
		// testing with static android responses, purchaseTime field is 0
		// to remove if testing with alpha or production
		if (timestamp.equals("0"))
		{
			long utc = System.currentTimeMillis();
			Date d = new Date(utc);
			String tz_removed = sdf.format(d)
			
			Date purchase = sdf.parse(tz_removed)
			return purchase
		}
		
		else
		{
			Date d = new Date(timestamp.toLong())
			String tz_removed = sdf.format(d)
			
			Date purchase = sdf.parse(tz_removed)
			return purchase
		}
	}
	
	
}
