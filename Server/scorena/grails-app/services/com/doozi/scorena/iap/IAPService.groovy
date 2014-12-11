package com.doozi.scorena.iap

import java.text.SimpleDateFormat
import java.util.ArrayList

import com.doozi.scorena.iap.android.util.*
import com.doozi.scorena.Account
import com.doozi.scorena.transaction.AndroidIAPTransaction
import com.doozi.scorena.transaction.IOSIAPTransaction

import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject

import org.codehaus.groovy.grails.web.json.JSONObject

import org.springframework.transaction.annotation.Transactional
@Transactional
class IAPService {
	public Nonce nonce
	
	// nonce list
	static ArrayList nonce_list = new ArrayList()
	
	// transaction list 
	static ArrayList transaction_list = new ArrayList()

	// generates nonce
	def generateNonce()
	{
		nonce = new Nonce()
		
		// nonce failed to generate
		if (!nonce)
		{
		  return [Status:"1",Process:"Generate Nonce", Message:"Nonce failed to generate"]
		}
		
		// nonce generated
		else
		{
		// nonce added to noce list
		nonce_list.add(nonce.getNonce())
		return [Status:"0",Process:"Generate Nonce", Message:nonce.getNonce()]  
		}
	}
	
	/* Applies android iap item to user account
	 * 
	 * @param client_nonce - client nonce for tracking request
	 * @param userID - Clients User ID
	 * @param app_data - Json string containing inapp purchase data
	 */
	def applyPurchase(String client_nonce, String userID, String app_data)
	{
		// iap class constants
		IapConst iap = new IapConst()

		JSONObject receipt = new JSONObject(app_data)
		
		String order_id = receipt.get("orderId")
		String product_id = receipt.get("productId")
		Date transaction_date_utc = iap.convertMS((String)receipt.get("purchaseTime"))
		int qty = 1; 
		
		// if both the client nonce and order id are contained within their respected lists
		if (transaction_list.contains(order_id) && nonce_list.contains(client_nonce))
		{
			// search for associated user account in DB
			Account userAccount = Account.findByUserId(userID)
			
			// if User does not exist
			if (!userAccount)
			{
				return [Status:"1",Process:"Account Check", Message:"Account not found"]
			}
			
			// creates a new transaction recored for user by iap product id
			// and updates users current balance and previous balance
			switch(product_id)
				{
					case "coins100":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: iap.getPK_1(), createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + iap.getPK_1()
					break;
					
					case "coins250":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: iap.getPK_2(), createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + iap.getPK_2()
					break;
					
					case "coins500":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: iap.getPK_3(), createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + iap.getPK_3()
					break;
					
					case "coins1000":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: iap.getPK_4(), createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + iap.getPK_4()
					break;
					
					case "coins2500":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: iap.getPK_5(), createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + iap.getPK_5()
					break;
					
					// TODO: test case google play store; remove in production
					// test case google play store
					case "Coins3000":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: 3000, createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + 3000
					break;
					
					// TODO: test case static response; remove in production
					// test case static response 
					case "android.test.purchased":
					AndroidIAPTransaction action = new AndroidIAPTransaction(transactionAmount: 3000, createdAt: new Date(),productId:product_id,orderId:order_id,quantity:qty,purchaseTime:transaction_date_utc)
					userAccount.addToTrans(action)
					userAccount.previousBalance = userAccount.currentBalance
					userAccount.currentBalance = userAccount.currentBalance + 3000
					break;
					
				}
			
				// user account failed to save
				if (!userAccount.save(failOnError:true)){
					System.out.println("---------------account save failed")
					return [Status:"1",Process:"Account Save", Message:"Account transaction failed to save"] 
					}
				
				
			// remove nonce from nonce list	
			nonce_list.remove(client_nonce)
			
			// remove order id from transaction list
			transaction_list.remove(order_id)
			
			// account save success
			[Status:"0",Process:"Account Save", Message:"Account transaction was saved"]
		}
		
		// either nonce or order id failed to be contain with in the correct list
		else
		{
			return [Status:"1",Process:"Nonce-Transaction Check", Message:"Nonce/Transaction ID failed to verify"] 
		}
	}
	
	/* verifies android iap receipt
	 * @param client_nonce -  client nonce for tracking request
	 * @param signature - iap signature string
	 * @param app_data - Json string containing inapp purchase data
	 * @param dev_key - developer payload string
	 */
	def verifyPurchase(String client_nonce, String signature, String app_data, String dev_key )
	{
		// iap class constants
		IapConst iap = new IapConst()
		
		// if nonce is contained in the nonce list
		if (nonce_list.contains(client_nonce))
		{	
			// gets app public key		
			String Pk = iap.getPk()

			JSONObject receipt = new JSONObject(app_data)			
			
			String orderID = receipt.get("orderId")
			String productID = receipt.get("productId")
			String payload = receipt.get("developerPayload")
			
			// checks to see if developer payload in app_data matches dev payload sent in google purchase request
			if (dev_key.equals(payload))
			{
				// checks database to see if order has been processed
				AndroidIAPTransaction search = AndroidIAPTransaction.find("from AndroidIAPTransaction as b where (b.orderId=?)", orderID)
				
				// if order has not been processed
				if(!search)
				{
					// validation: checks base64 encode, signature and public key are verified
					// TODO: uncomment and remove (true) if testing from google playstore
					// TODO: leave (true) if testing with static responses 
					if(true)//(Security.verifyPurchase(Pk, app_data, signature))
					{
						// adds order id to transaction list
						transaction_list.add(orderID)
						System.out.println("google play receipt is valid")
						return [Status:"0",Process:"Security-Verify", Message:"Valid receipt"] 
					}
					
					// failed validation
					else
					{
						return [Status:"1",Process:"Security-Verify", Message:"Receipt failed to verify"] 
					}
				}
				
				// order already exist in database
				else
				{
					return [Status:"1",Process:"Database Check", Message:"Receipt already exist in DB"]  
				}
			}
			
			// dev_key and payload are not equal
			else
			{
				return [Status:"1",Process:"Payload Check", Message:"Payload failed to verify"] 
			}
		}
		
		// Nonce not contained in nonce list
		else
		{
			return [Status:"1",Process:"Nonce Check", Message:"Nonce failed to verify"] 
		}	
	}
	
	/* IOS Receipt validation Method
	 * @param nonce - client nonce for tracking request
	 * @param userID - Clients User ID
	 * @param receipt - IOS IAP encoded receipt
	 */
	def validateWithServer(String nonce,String userID,String receipt) 
	{
		// iap class constants 
		IapConst iap = new IapConst()
		
		// if nonce is contained in the nonce list
		if (nonce_list.contains(nonce))
		{
			// remove from nonce list
			nonce_list.remove(nonce)
			try{
			   // TODO: http connection to the sand box url -> change toIapConst.getProduction() when moving to production
				HttpURLConnection connection = (HttpURLConnection) new URL(iap.getSandbox()).openConnection();
				
				// jSON data object passed in from ios
				JsonObject receipt_object = new JsonObject();
				
				// mapping of json data object
				receipt_object.addProperty("receipt-data", receipt);

				
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
				
				while ((response_line = reader.readLine()) != null)
				{
					response_object.append(response_line);
				}
				
				writer.close();
				reader.close();
				
				// creates new gson object to parse nested json response string
				Gson gson = new GsonBuilder().create();
				def holder =  gson.fromJson(response_object.toString(), IosReceipt.class);
				
				// holder.status = 0 -> valid receipt
				if (holder.status == 0)
				{
					String receipt_quantity = holder.receipt.in_app.quantity
					String product = holder.receipt.in_app.product_id
					String transaction = holder.receipt.in_app.transaction_id
					String trans_date = holder.receipt.in_app.purchase_date
					
					String qty = receipt_quantity.replace("[", "").replace("]", "")
					String product_id = product.replace("[", "").replace("]", "")
					String transaction_id = transaction.replace("[", "").replace("]", "")
					String transaction_date = trans_date.replace("[", "").replace("]", "")
					
					// converts date
					Date transaction_date_utc = iap.convertString(transaction_date)
					
					// search for associated user account in DB
					Account userAccount = Account.findByUserId(userID)
					
					// checks database to see if order has been processed
					IOSIAPTransaction search = IOSIAPTransaction.find("from IOSIAPTransaction as b where (b.transactionId=?)", transaction_id)
					
					// if order has not been processed
					if (!search)
					{	
						// user account does not exist
						if (!userAccount)
						{
							return [Status:"1",Process:"Account Check", Message:"Account not found"]  
						}
						
						// creates a new transaction recored for user by iap product id
						// and updates users current balance and previous balance
						switch(product_id)
						{
							case "coins100":
							IOSIAPTransaction itemPurchase = new IOSIAPTransaction(transactionAmount: iap.getPK_1(), createdAt: new Date(),quantity:qty ,productId:product_id ,transactionId:transaction_id ,purchaseTime:transaction_date_utc)
							userAccount.addToTrans(itemPurchase)
							userAccount.previousBalance = userAccount.currentBalance
							userAccount.currentBalance = userAccount.currentBalance + iap.getPK_1()
							break;
							
							case "coins250":
							IOSIAPTransaction itemPurchase  = new IOSIAPTransaction(transactionAmount:iap.getPK_2(), createdAt: new Date(),quantity:qty ,productId:product_id ,transactionId:transaction_id ,purchaseTime:transaction_date_utc)
							userAccount.addToTrans(itemPurchase)
							userAccount.previousBalance = userAccount.currentBalance
							userAccount.currentBalance = userAccount.currentBalance + iap.getPK_2()
							break;
							
							case "coins500":
							IOSIAPTransaction itemPurchase = new IOSIAPTransaction(transactionAmount: iap.getPK_3(), createdAt: new Date(),quantity:qty ,productId:product_id ,transactionId:transaction_id ,purchaseTime:transaction_date_utc)
							userAccount.addToTrans(itemPurchase)
							userAccount.previousBalance = userAccount.currentBalance
							userAccount.currentBalance = userAccount.currentBalance + iap.getPK_3()
							break;
							
							case "coins1000":
							IOSIAPTransaction itemPurchase  = new IOSIAPTransaction(transactionAmount:iap.getPK_4(), createdAt: new Date(),quantity:qty ,productId:product_id ,transactionId:transaction_id ,purchaseTime:transaction_date_utc)
							userAccount.addToTrans(itemPurchase)
							userAccount.previousBalance = userAccount.currentBalance
							userAccount.currentBalance = userAccount.currentBalance + iap.getPK_4()
							break;
							
							case "coins2500":
							IOSIAPTransaction itemPurchase = new IOSIAPTransaction(transactionAmount: iap.getPK_5(), createdAt: new Date(),quantity:qty ,productId:product_id ,transactionId:transaction_id ,purchaseTime:transaction_date_utc)
							userAccount.addToTrans(itemPurchase)
							userAccount.previousBalance = userAccount.currentBalance
							userAccount.currentBalance = userAccount.currentBalance + iap.getPK_5()
							break;
							
							
							// test case -> to be removed in production 
							case "com.scorena.iaptest.100c":
							IOSIAPTransaction itemPurchase = new IOSIAPTransaction(transactionAmount: 100, createdAt: new Date(),quantity:qty ,productId:product_id ,transactionId:transaction_id ,purchaseTime: transaction_date_utc)
							userAccount.addToTrans(itemPurchase)
							userAccount.previousBalance = userAccount.currentBalance
							userAccount.currentBalance = userAccount.currentBalance + 100
							break;
						}
			
					}
					
					// order already exist in database 
					else
					{
						return [Status:"1",Process:"Database Check", Message:"Receipt already exist in DB"] 
					}
					
					// user account failed to save
					if (!userAccount.save(failOnError:true)){
						System.out.println("---------------account save failed")
						return [Status:"1",Process:"Account Save", Message:"Account transaction failed to save"] 
						}
					
					// account save success 
					return [Status:"0",Process:"Account Save", Message:"Account transaction was saved"]
				}
				// holder.status not 0
				/*
				 * other status codes
				 * holder.status == 21000 - The App Store could not read the JSON object you provided.
				 * holder.status == 21002 - The data in the receipt-data property was malformed or missing.
				 * holder.status == 21004 - The shared secret you provided does not match the shared secret on file for your account. (Only returned for iOS 6 style transaction receipts for auto-renewable subscriptions.)
				 * holder.status == 21005 - The receipt server is not currently available.
				 * holder.status == 21006 - This receipt is valid but the subscription has expired. (Only returned for iOS 6 style transaction receipts for auto-renewable subscriptions.)
				 * holder.status == 21007 - This receipt is from the test environment, but it was sent to the production environment for verification. Send it to the test environment instead.
				 * holder.status == 21008 - This receipt is from the production environment, but it was sent to the test environment for verification. Send it to the production environment instead.
				 */
				else
				{
					return [Status:"1",Process:"Security-Verify", Message:"Receipt failed to verify, error: " + holder.status]
				}
				
			}
			// catch exception
			catch (Exception e){
				return [Status:"1",Process:"Exception Error", Message: e.getMessage()]
			}
		}
		// nonce not contained in list
		else
		{
			return [Status:"1",Process:"Nonce Check", Message:"Nonce failed to verify"]
		} 
	}
	
}
