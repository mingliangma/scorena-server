package com.doozi.scorenaiap.test;

import org.json.JSONException;
import org.json.JSONObject;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class PurchaseActivity extends ActionBarActivity implements OnClickListener {
	
	public static final String COIN_PACKAGE = "com.doozi.scorenaiap.conipackage"; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_purchase);
		
	//	Button c_500 = (Button)findViewById(R.id.coins_500);
	//	Button c_1000 = (Button)findViewById(R.id.coins_1000);
		Button c_3000 = (Button)findViewById(R.id.coins_3000);
	 
		//c_500.setOnClickListener(this);
	//	c_1000.setOnClickListener(this);
		c_3000.setOnClickListener(this);
	 
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.purchase, menu);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		Intent coinPurchase = new Intent(this,PurchasePackageActivity.class);
		
		switch (v.getId())
		{
		//case R.id.coins_500:
		//	coinPurchase.putExtra(COIN_PACKAGE, "500");
		//	 startActivity(coinPurchase);
			
		//	break;
			
	//	case R.id.coins_1000:
	//		coinPurchase.putExtra(COIN_PACKAGE, "1000");
	//		 startActivity(coinPurchase);
	//		break;
			
		case R.id.coins_3000:
			coinPurchase.putExtra(COIN_PACKAGE, "3000");
			 startActivity(coinPurchase);
			break;
		}
	}
	

	

}
