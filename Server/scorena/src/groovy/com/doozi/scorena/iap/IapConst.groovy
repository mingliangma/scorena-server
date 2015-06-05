package com.doozi.scorena.iap

import java.text.SimpleDateFormat
import java.util.Date;

class IapConst {
	// Coin Packages
	public static final int COIN_PK_1 = 1000
	public static final int COIN_PK_2 = 2500
	public static final int COIN_PK_3 = 5000
//	public static final int COIN_PK_4 = 1000
//	public static final int COIN_PK_5 = 2500
	
	
	// URL for IOS Sandbox receipt verification
	public static final String IOS_Sandbox = "https://sandbox.itunes.apple.com/verifyReceipt"
	
	// URL for IOS Production receipt verification
	public static final String IOS_Production = "https://buy.itunes.apple.com/verifyReceipt"
	
	// Public Key for the iap test app in google play store
	static final String COM_WINGO_SCORENA_PK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgaqpbpd5ObvkEcDMJZDYNe97sD9JoMteu0eXFbBKL8WsQi9Nb9mg2z481DMqtXMSN1TM1RVUKex2JuNata4M3C8hm53glrmTx1Xcns9nEJvHJkY3lr2ajwb5AMb/bKzbIhmAhQghSydfCpTxt+y+/Tj3Z+W8h0ZML6AH3TgmzVrLZy8npyVh13rpsB7Ca8Hj+omwpCMLtk2ucpCeThWz74ww8lkfCmnurMBmopadl1zpqsn+Kb6qcQXc6ZMnE2h1oeTBSvtzk3OtvoCCywdXsHcOjc0JRIQhSxhG2Hj8hh1wuEQqMB79hiqqflWaGphRltwgtc0QG8IvbW53UG46+QIDAQAB"
	static final String NGO_SCORENA_NBADRAFT_PK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAp0uL/MWqOTTZzfCuWmOuDPFADqR6Pxsxpz5pblZYSlW66kh1PNy5rwhzV9oufPKX8iKy1VpsldLMbjt+++PrFbGN6qTpCSPy0+Ot5uPCrwzcUQcIQgi0bd7qLx+Ywpizx+i+AxXg1M511iXFQTZjGT1FUj3BPS3w9MTUY/VMhMaPApko05ILbhal8I0pffWkREI3pWZDVS9iDN+p0Y4YyMWoxm2KIp+UO05hXbTPSDX1GXFLxL6T+2eFbzHH+XMgtfOGAKX6zgj4imK/OlWgALfXzyjPjw4M0gtGb8INJaiECIie7SlkFlUVPsWEHoOQOCIB8oq8Fzfl7wfNz59YHwIDAQAB"
	static final String COM_WINGO_SCORENA_FRENCHOPEN_PK = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAvYWTHI3oOVSUEv/1rCwW5Fq5xwa4fXQni4IDoaM989LABf3g3ur1sAyj5/mvZKVC1dkBczMLgEUH7J510kqHKEtZaUm28tbUiGCFsw7mZHl5xUtFasPLFzv1S8DrRrGWnP2jwqUw+ByWZWU+AiDOireZkPB3JuEoeFrJErMn9RpTeZBaUvDfC16kuzo37JZDYX3xeIZ/qNbUQngCQKkqw1BLI8udpeARYPeZ03l+ea5l77RxEwivxJOJ/Xzbhdo4fATK/UPYauAIkzDVdwq3cVGJhui6gOyWN0uslj1hDMSbX/H+T/Vjvl3Q0nR0h9q1vn3TnfBQoTRNcWJ1XviGgwIDAQAB"
	
	//Scorena Android app package names
	static final String COM_WINGO_SCORENA = "com.wingo.scorena"
	static final String COM_WINGO_SCORENA_NBADRAFT = "com.wingo.scorena.nbadraft"
	static final String COM_WINGO_SCORENA_FRENCHOPEN = "com.wingo.scorena.frenchopen"
	
	// returns android app public key
	public String getPk(String packageName)
	{
		println packageName
		println COM_WINGO_SCORENA_FRENCHOPEN
		switch (packageName){
			case COM_WINGO_SCORENA:
				return COM_WINGO_SCORENA_PK
			case COM_WINGO_SCORENA_NBADRAFT:
				return NGO_SCORENA_NBADRAFT_PK
			case COM_WINGO_SCORENA_FRENCHOPEN:
				return COM_WINGO_SCORENA_FRENCHOPEN_PK
			default:
				return null
		}
		
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
	
	// returns coin_pk_1 value 
	public int getPK_1()
	{
		return COIN_PK_1
	}
	
	// returns coin_pk_2  value
	public int getPK_2()
	{
		return COIN_PK_2
	}
	
	// returns coin_pk_3 value
	public int getPK_3()
	{
		return COIN_PK_3
	}
	/*
	// returns coin_pk_4 value
	public int getPK_4()
	{
		return COIN_PK_4
	}
	
	// returns coin_pk_5 value
	public int getPK_5()
	{
		return COIN_PK_5
	}
	*/
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
