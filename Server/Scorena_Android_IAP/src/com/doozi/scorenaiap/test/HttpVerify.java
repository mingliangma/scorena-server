package com.doozi.scorenaiap.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import android.os.AsyncTask;

public class HttpVerify extends AsyncTask<String,Void,String> {

	public AsyncResponse response;
	
	@Override
	protected String doInBackground(String... params) {
		
		String signature, nonce, app_data, devString;
		
		signature = params[0];
		nonce = params[1];
		app_data = params[2];
		devString = params[3];
		
		StringBuilder response_object = new StringBuilder();
		try{
			System.setProperty("http.keepAlive", "false");

			// local host url for testing -> change when on production
		// URL url_verify = new URL("http://192.168.2.17:8080/iap_test/v1/verifyAndroid"); 
		
		 URL url_verify = new URL("http://192.168.2.17:8080/scorena/v1/iap/validateAndroid"); 
		 HttpURLConnection urlConn = (HttpURLConnection) url_verify.openConnection();

		 urlConn.setDoOutput(true);
         urlConn.setChunkedStreamingMode(0);
		 urlConn.setRequestProperty("Connection", "close");
         urlConn.setRequestMethod("POST");
         
         String data = URLEncoder.encode("nonce", "UTF-8") + "=" + URLEncoder.encode(nonce, "UTF-8"); 
         
         data+=  "&" + URLEncoder.encode("signature", "UTF-8") + "=" + URLEncoder.encode(signature, "UTF-8");
         data+=  "&" + URLEncoder.encode("data", "UTF-8") + "=" + URLEncoder.encode(app_data, "UTF-8");
         data+=  "&" + URLEncoder.encode("dev", "UTF-8") + "=" + URLEncoder.encode(devString, "UTF-8");
         
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
		 catch (Exception e){
   		 e.printStackTrace();
   		 //this.cancel(true);
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
