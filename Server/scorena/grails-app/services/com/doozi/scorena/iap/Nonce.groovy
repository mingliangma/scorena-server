package com.doozi.scorena.iap

import java.security.SecureRandom;

class Nonce {

	private byte[] nonce;
	
	public Nonce()
	{
		nonce = new byte[16];
		SecureRandom val = new SecureRandom();
		val.getInstance("SHA1PRNG");
		val.nextBytes(nonce);
	}
	
	public getNonce()
	{
		return nonce.toString();
	}
	
	
}
