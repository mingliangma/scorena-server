package com.doozi.scorenaiap.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.AsyncTask;

public class HttpNonce extends AsyncTask<Void,Void,String> {

	public AsyncResponse response;
	
	@Override
	protected String doInBackground(Void... params) {
		StringBuilder nonce_object = new StringBuilder();
		 try{
			 System.setProperty("http.keepAlive", "false");

    		// URL url_nonce = new URL("http://192.168.2.17:8080/iap_test/v1/getNonce"); 
			
			 URL url_nonce = new URL("http://192.168.2.17:8080/scorena/v1/iap/getNonce"); 
    		 HttpURLConnection urlConn = (HttpURLConnection) url_nonce.openConnection();

    		 urlConn.setDoOutput(true);
	         urlConn.setChunkedStreamingMode(0);
	         urlConn.setRequestProperty("Connection", "close");
	         urlConn.setRequestMethod("POST");
	         
	         OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
	         
	         writer.flush();
	         
	         BufferedReader in =  new BufferedReader( new InputStreamReader (urlConn.getInputStream()));
	         
	         //StringBuilder nonce_object = new StringBuilder();
	         String response_line;
	         
	         while ((response_line = in.readLine()) != null)
				{
	        	 nonce_object.append(response_line);
				}
				
				writer.close();
				in.close();

	    	 //android.util.Log.d("Response", "returned :" + nonce_object); 
		 }
		 catch (Exception e){
    		 e.printStackTrace();
    		// this.cancel(true);
    		 
 		}
		 
		 if (isCancelled())
		 {
			 return "Error";
		 }
		 else
		 {
			 return nonce_object.toString();
		 }
	}
	
	 @Override
	   protected void onPostExecute(String result) {
	      response.ServerResponse(result);
	   }

}
