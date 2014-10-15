package com.doozi.scorenaiap.test;


import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

import com.android.util.Security;
import com.android.vending.billing.IInAppBillingService;
import android.support.v7.app.ActionBarActivity;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PurchasePackageActivity extends ActionBarActivity implements OnClickListener, AsyncResponse  {

	IInAppBillingService appService;
	public devGenerator devString = new devGenerator(36);
	public String payload = devString.nextString();
	
	ServiceConnection sConn = new ServiceConnection(){
		   @Override
		   public void onServiceDisconnected(ComponentName name) {
			   appService = null;
		   }

		   @Override
		   public void onServiceConnected(ComponentName name, 
		      IBinder service) {
			   appService = IInAppBillingService.Stub.asInterface(service);
		   }
		};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase_package);
		
		Intent intent = getIntent();
		String message = intent.getStringExtra(PurchaseActivity.COIN_PACKAGE);
		
		TextView item = (TextView)findViewById(R.id.iap_item);
		item.setText(message);
		
		Button buy = (Button)findViewById(R.id.button1);
		buy.setOnClickListener(this);
		
		Button consume = (Button)findViewById(R.id.consume);
		consume.setOnClickListener(this);
		
		Intent sIntent = new Intent("com.android.vending.billing.InAppBillingService.BIND");
		sIntent.setPackage("com.android.vending");
		
		bindService(sIntent,sConn,Context.BIND_AUTO_CREATE);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase_package, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onDestroy() {
	    super.onDestroy();
	    if (appService != null) {
	        unbindService(sConn);
	    }   
	}
	
	/********************* Handles Completion of Purchase *************************************/
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) { 
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		CharSequence text;
		
	   if (requestCode == 1010) {           
	      int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
	      String purchaseData = data.getStringExtra("INAPP_PURCHASE_DATA");
	      String dataSignature = data.getStringExtra("INAPP_DATA_SIGNATURE");
	       
	      
	      if (dataSignature.equals(""))
	      {
	    	  dataSignature = "Test string";
	      }
	      
	      android.util.Log.d("Google Data",purchaseData);
	      android.util.Log.d("Google Data",dataSignature);
	      
	      if (resultCode == RESULT_OK) 
	      { 
	    	 // sends request to server with purchase data, signature and server nonce
	    	 try{
	    		  
	    		  // Http request to get server nonce
	    		  HttpNonce val = new HttpNonce();
	    		  val.response = this;
	    		 
	    		  val.execute(); 
	    		  
	    		  // Http response from server
	    		  
	    		  JSONObject nonce_response =  new JSONObject(val.get());
	    		  
	    		  String nonce_status = nonce_response.getString("Status");
	    		  String nonce_process = nonce_response.getString("Process");
	    		  String nonce_val = nonce_response.getString("Message");
	    		  
	    		  if (nonce_status.equals("1"))
	    		  {
	    			  android.util.Log.d(nonce_process,nonce_val);
	    			  text = "Error: " + nonce_process + " " + nonce_val;
	    			  Toast   toast = Toast.makeText(context, text, duration);
		              toast.show();
		              return;
	    		  }
	    		  
	    		  
	    		android.util.Log.d(nonce_process,"nonce = " + nonce_val);
	    		  
	    		 //Http request to verify purchase data
	    		HttpVerify receipt = new HttpVerify();
	    		receipt.response = this;
	    		
	    		receipt.execute(dataSignature,nonce_val,purchaseData,payload);

	    		
	    		// Http response from server
	    		
	    		JSONObject verify_response =  new JSONObject(receipt.get());
	    		String verify_status = verify_response.getString("Status");
	    		String verify_process = verify_response.getString("Process");
	    		String verifyResponse = verify_response.getString("Message");
	    		android.util.Log.d("Scorena-verify","Response " + verifyResponse);
	    	 
	    	 // consume inapp item block 
		    	 if (verify_status.equals("0"))
		    	 {
		    	
		    		 JSONObject itemOrder = new JSONObject(purchaseData);
			            String sku = itemOrder.getString("productId");
			            String token = itemOrder.getString("purchaseToken");
			            String order = itemOrder.getString("orderId");
			            
			            // replace with code to get the logged in user's userID 
			            String userID = "IeNg7YPk2u";
			            
			            // issue consume command for purchased item
			           int consumeIntent = appService.consumePurchase(3, getPackageName(), token);
			            
			           if (consumeIntent == 0)
			           {
			        	   // toast message to user
			        	   text = "Applying Coins to your account!!";
			        	   Toast toast = Toast.makeText(context, text, duration);
			        	   toast.show();
			        	
				    		// logs response details
				            android.util.Log.d("Scorena","You have bought the " + sku + ". Excellent choice,User!");
				            android.util.Log.d("Scorena","Purchase data: " + purchaseData );
				            android.util.Log.d("Scorena","Signature data: " + dataSignature );
				            
				            // Http request to activate item  
				            HttpConsume coinPKG = new HttpConsume();
				            coinPKG.response = this;
				            
				            
				            coinPKG.execute(nonce_val,userID,purchaseData);
				            android.util.Log.d("Scorena-Consume","call to server to verify ");
				            
				            // Http response from server
				            
				            JSONObject activate_response =  new JSONObject(coinPKG.get());
				            String activate_status = activate_response.getString("Status");
				            String activate_process = activate_response.getString("Process");
				   		    String activateResponse = activate_response.getString("Message");
				            android.util.Log.d("Scorena-Consume","Response " + activateResponse);
				            
				            if (activate_status.equals("0"))
				            {
				            	 // toast message to user
				            	text = sku + " Package has been applied to your account";
				            	toast = Toast.makeText(context, text, duration);
				            	toast.show();
				            	
				            	// log response
				            	// TODO: add in API call to update balance on device
				            	android.util.Log.d("Scorena","user balance updated  " );
				            }
				            else
				            {
				            	 // toast message to user
				            	 android.util.Log.d(activate_process,activateResponse);
				            	 
				            	// log response
					            android.util.Log.d("Scorena","user balance not updated");
				            	 
				    			 text = "Error: " + activate_process + " " + activateResponse;
				    		     toast = Toast.makeText(context, text, duration);
					             toast.show();
					             return;
				            	
				            }			            
			           }
			           else
			           {
			        	   //Context context = getApplicationContext();
			        	    text = "Item consume failed!!";
				    		Toast toast = Toast.makeText(context, text, duration);
				    		toast.show();
				    		
				    		// logs consume error
			        	   android.util.Log.d("Scorena","Consume Intent error code = " + consumeIntent);
			           }
		    	 }
		    	 
		    	 else
		    	 {
		    		 // logs response
		    		 android.util.Log.d(verify_process,verifyResponse);
	    			 text = "Error: " + verify_process + " " + verifyResponse;
	    			 Toast   toast = Toast.makeText(context, text, duration);
		             toast.show();
		             return;
		    	 }
	    	 }
	    	 catch (Exception e){
	    		 e.printStackTrace();
	 		}
	      }
	   }
	}
	
	
  /********************** Handles *********************************/
	@Override
	public void onClick(View v) {
		Context context = getApplicationContext();
		int duration = Toast.LENGTH_LONG;
		CharSequence text;
		try {
			// sets up purchase request to google
			// 1: api number
			// 2: package name
			// 3: in app sku - for this it is set to a static test google product
			// 4: type of product - inapp = consumable
			// 5: developers payload = extra data to send to google
			Bundle buyingIntent = appService.getBuyIntent(3, getPackageName(),"coins3000", "inapp", payload);
			// "coins3000", "inapp", payload)
			// android.test.item_unavailable
			// android.test.refunded
			// android.test.canceled
			// android.test.purchased
			
			// request is successful if response code of 0 
			if (buyingIntent.getInt("RESPONSE_CODE") == 0)
			{
					// sets up pendingIntent to be used for the purchase logic flow
					PendingIntent pendingIntent = buyingIntent.getParcelable("BUY_INTENT");
					
					try {
						
						// takes pending Intent and uses it to set complete the purchase logic flow
						startIntentSenderForResult(pendingIntent.getIntentSender(),1010,new Intent(),Integer.valueOf(0),Integer.valueOf(0),Integer.valueOf(0));
						} 
					catch (SendIntentException e) 
					{
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
			
			else if (buyingIntent.getInt("RESPONSE_CODE") == 7)
			{
				String token;
				String AppData =  buyingIntent.getString("INAPP_PURCHASE_DATA");
				
				Bundle purchasedItems = appService.getPurchases(3, getPackageName(), "inapp", null);
				
				int response = purchasedItems.getInt("RESPONSE_CODE");
				
				if (response == 0)
				{
					android.util.Log.d("Scorena", "Item Already owned");
					android.util.Log.d("Scorena", "List of Purchased Items");
					ArrayList<String> ownedSkus = purchasedItems.getStringArrayList("INAPP_PURCHASE_ITEM_LIST");
					ArrayList<String>  purchaseDataList = purchasedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST");
				//  ArrayList<String>  signatureList = purchasedItems.getStringArrayList("INAPP_DATA_SIGNATURE");
						   
						   
					
						   JSONObject pd;
						   for (int i = 0; i < purchaseDataList.size(); ++i) 
						   {
						      String purchaseData = purchaseDataList.get(i);
						  //    String signature = signatureList.get(i);
						      String sku = ownedSkus.get(i);
						      
						     android.util.Log.d("Scorena", "Purchased Item " + i);
						     android.util.Log.d("Scorena", "Purchase Data: " + purchaseData);
						  //   android.util.Log.d("Scorena", "Signature: " + signature);
						     android.util.Log.d("Scorena", "sku: " + sku );
						     android.util.Log.d("Scorena", "*********************** ");
						     
						     try {
									pd = new JSONObject(purchaseData);
									token = pd.getString("purchaseToken");
									 android.util.Log.d("Scorena", "Purchase Token: " + token );
									 
									 android.util.Log.d("Scorena","Atempting to consume ...");
										
										int intent = appService.consumePurchase(3, getPackageName(), token);

										if (intent == 0)
										{
										android.util.Log.d("Scorena","Consumed ...");
										}
										
										else
										{
											android.util.Log.d("Scorena","Consume Intent = " + intent);
										}
									 
								} 
						     catch (JSONException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
						   } 
					}
				}
			
			
			else
			{
				android.util.Log.d("Scorena","some thing went wrong ...." + buyingIntent.getInt("RESPONSE_CODE") );
				text = "Error: " + buyingIntent.getInt("RESPONSE_CODE");
    			 Toast   toast = Toast.makeText(context, text, duration);
	             toast.show();
			}		
		}
		catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	@Override
	public void ServerResponse(String response) {
		// TODO Auto-generated method stub
	//	android.util.Log.d("ServerResponse method",response);
	}
}
				

