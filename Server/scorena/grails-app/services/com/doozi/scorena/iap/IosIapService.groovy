package com.doozi.scorena.iap

import org.springframework.transaction.annotation.Transactional
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

@Transactional
class IosIapService {

	
	public static String validateWithServer(String receipt) //throws IOException
	{
		try{
		   // http connection to the sand box url **** change to - https://buy.itunes.apple.com/verifyReceipt - when going into production ****
			HttpURLConnection connection = (HttpURLConnection) new URL("https://sandbox.itunes.apple.com/verifyReceipt").openConnection();
			
			// jSON data object passed in from ios
			JsonObject receipt_object = new JsonObject();
			
			// mapping of json data object
			receipt_object.addProperty("receipt-data", receipt);

			//System.out.println(receipt_object.toString());
			
			// connection method set to post
			connection.setRequestMethod("POST");
			connection.setDoOutput(true);
			
			connection.setRequestProperty("Content-Type", "application/json");
			
			// output stream writer for writing out to the connection
			OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
			
			writer.write(receipt_object.toString());
			writer.flush();
			
			// buffer reader for reading the response from the server
			BufferedReader reader = new BufferedReader( new InputStreamReader(connection.getInputStream()));
			String response_line;
			StringBuilder response_object = new StringBuilder();
			
			// while 
			while ((response_line = reader.readLine()) != null)
			{
				response_object.append(response_line);
			}
			
			// close stream writer and reader 
			writer.close();
			reader.close();
			
			// creats new gson data object 
			Gson gson = new GsonBuilder().create();
			
			// parses Json object into a Gson object, using the IOS_receipt.class as a parsing template 
			def holder =  gson.fromJson(response_object.toString(),IosReceipt.class);
			
			if (holder.status == 0)
			{
				// returns to the caller that the receipt is valid. must append holder object if sending back more data.
				return "Valid receipt !!!";
			}
			else
			{
				return "In app receipt failed to verify";
			}
		}
		
		catch (Exception e){
			return e.getMessage();
		}
	
	}
	
}
