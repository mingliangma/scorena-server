package com.doozi.scorenaiap.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;

public class HttpConsume extends AsyncTask<String,Void,String>{

	public AsyncResponse response;

	@Override
	protected String doInBackground(String... params) {
		
		String nonce, uID, app_data;
		
		nonce = params[0];
		uID = params[1];
		app_data = params[2];
		
		
		StringBuilder response_object = new StringBuilder();
		try{
			System.setProperty("http.keepAlive", "false");

			// URL url_activate = new URL("http://192.168.2.17:8080/iap_test/v1/activateAndroid"); 
			 
			URL url_activate = new URL("http://192.168.2.17:8080/scorena/v1/iap/activatePurchse"); 
			 
			 HttpURLConnection urlConn = (HttpURLConnection) url_activate.openConnection();
			 
		
			 
			 urlConn.setDoOutput(true);
	         urlConn.setChunkedStreamingMode(0);
	         urlConn.setRequestProperty("Connection", "close");
	         urlConn.setRequestMethod("POST");
	         
	         String data = URLEncoder.encode("nonce", "UTF-8") + "=" + URLEncoder.encode(nonce, "UTF-8"); 
	         
	         data+=  "&" + URLEncoder.encode("uID", "UTF-8") + "=" + URLEncoder.encode(uID, "UTF-8");
	         data+=  "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(app_data, "UTF-8");
	        
	         
	         OutputStreamWriter writer = new OutputStreamWriter(urlConn.getOutputStream());
	         writer.write(data);
	         writer.flush();

	         
	         BufferedReader in =  new BufferedReader( new InputStreamReader (urlConn.getInputStream()));
	         
	         String response_line;
	         
	         while ((response_line = in.readLine()) != null)
				{
	        	 response_object.append(response_line);
				}
				
				writer.close(); 
				in.close(); 
			
			 
		}
		catch (Exception e)
		{
	   		 e.printStackTrace();
	   		// this.cancel(true); 
		}
				
		if (isCancelled())
		 {
			 return "Error";
		 }
		 else
		 {
		return response_object.toString();
		 } 
	}
	
	
	@Override
	   protected void onPostExecute(String result) {
	      response.ServerResponse(result);
	   }
}
