package com.benson.stockalert;

import org.json.JSONArray;

import org.json.JSONObject;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


//import com.benson.stockalert.model.Result;
//import com.benson.stockalert.model.SearchResponse;
import com.benson.stockalert.utility.CopyInputStream;



public class JSONParseActivity extends Activity{
    /** Called when the activity is first created. */
	
	private String myName;	
	private EditText text;
	
        
	@Override
	public void onCreate(Bundle savedInstanceState) {

		this.myName = this.getClass().getSimpleName();
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		this.text = (EditText) findViewById(R.id.editText1);
		

//		// Setup WiFi		
//		wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);		
//		// Get WiFi status		
//		WifiInfo info = wifi.getConnectionInfo();	
//		Log.d( this.myName, info.toString() );
//		Log.d(this.myName, "testing");
//		
//		//Check the our wifi is currently turned on or turned off
//		 
//		 if(wifi.isWifiEnabled()){
//			 wifi.setWifiEnabled(false);  // Turn on/off our wifi
//		  }else{
//			  wifi.setWifiEnabled(true);
//		  }
		
		//textStatus.append("\n\nWiFi Status: " + info.toString());
		
//		 String ns = Context.NOTIFICATION_SERVICE;
//		 NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);		 
//		 //Notification localNotification = makeNewAlertNotification(this.getApplicationContext(), localStockAlert, str2, "Alert triggered.(" + str3 + ")");
//		 
//		 int icon = R.drawable.shares;
//		 CharSequence tickerText = "Hello";
//		 long when = System.currentTimeMillis();
//		 Notification notification = new Notification(icon, tickerText, when);
//		 
//		 Context context = getApplicationContext();
//		 CharSequence contentTitle = "My notification";
//		 CharSequence contentText = "Hello World!";
//		 Intent notificationIntent = new Intent(this, JSONParseActivity.class);
//		 PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//		 notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
//		 
//		 int HELLO_ID = 1;
//		 mNotificationManager.notify(HELLO_ID, notification);

 
			
//		 this.runStock( str1 );
//
//	      Log.i(this.myName, "url:finance/info?infotype=infoquoteall&q=" + paramString);
//
//	      try
//	      {
//	      JSONArray localJSONArray = new JSONArray(str1.substring(3));
//	      JSONObject localJSONObject = localJSONArray.getJSONObject(0);
//	      }
//	      catch (JSONException localJSONException)
//	      {
//	        Log.e(this.myName, "get stocks json fail:" + paramString, localJSONException);
//	      }
//	      //String str2 = localJSONObject.getString(Table.Stocks.e.name()) + ":" + localJSONObject.getString(Table.Stocks.t.name());
//	      //String str3 = localJSONObject.getString(Table.Stocks.l.name());
	}
	
	
	public void onClick( View view ) {
		switch (view.getId()) {
			case R.id.button1:

				if (this.text.getText().length() == 0) {
					Toast.makeText(this, "Please enter a stock symbol",
							Toast.LENGTH_LONG).show();
					return;
				}	
				 String paramString = this.text.getText().toString(); //"OCLR,CINF,RDN,UGA,ODP";
				 //String str1 = doHttpGet( "http://www.google.com/finance/info?infotype=infoquoteall&q=" + paramString);
				 //String str1 = "http://www.google.com/finance/info?infotype=infoquoteall&q=" + paramString;
				 
				 JSONArray localJSONArray= null;
				 try {
	
					 localJSONArray = new StockQuote(this).getJsonStockArray( paramString );
					 Log.i(this.myName, localJSONArray.length() +"");
	
					 int count = localJSONArray.length();
	
					 JSONObject localJSONObject;
					 for (int i=0; i<count; ++i) {   
						 localJSONObject = localJSONArray.getJSONObject(i);
						 Toast.makeText(this, localJSONObject.getString( "name") + " = " + localJSONObject.getString( "l_cur"), 1).show();
						 Log.i(this.myName, localJSONObject.getString( "name") + " = " + localJSONObject.getString( "l_cur") ); 
					 }			
				 }
				 catch (Exception e) {
					 Log.e( this.myName, "Error obtaining quote for " + paramString, e);
				 }	
		}
	}


	// This method is called at button click because we assigned the name to the
	// "On Click property" of the button
//	public void myClickHandlerORIGINAL(View view) {
//		switch (view.getId()) {
//		case R.id.button1:
//
//			if (this.text.getText().length() == 0) {
//				Toast.makeText(this, "Please enter a valid address",
//						Toast.LENGTH_LONG).show();
//				return;
//			}			
//			
//			//get the JSON Map URL
//			jsonURL = new JsonURL( this );
//			jsonURL.createJsonMapUri();
//			jsonURL.setQueryParameter( "sensor", "false" );
//			
//			jsonURL.setQueryParameter( "address", this.text.getText().toString() );
//
//			String url = jsonURL.getUriBuilder().build().toString();
//
//			Log.d( this.myName, url);
//			
//			SearchResponse response = null;
//			
//			try {
//				BufferedReader reader = retrieveReader( url );
//				
//				response = this.gson.fromJson(reader, SearchResponse.class);
//				
//				//now close the response reader
//				reader.close();				
//			}
//			catch (IOException e) {
//				Log.e( this.myName, "Error for URL " + url, e);
//			} 
//
//
//			List<Result> results = response.results;
//
//			Toast.makeText(this, "# of results = " +results.size(), Toast.LENGTH_SHORT).show();
//
//			//Toast.makeText(this, response.status, Toast.LENGTH_SHORT).show();
//
//			for (Result result : results) {
//				Toast.makeText(this, result.formattedAddr, 1).show();
//			}
//			//				
//			//				RadioButton celsiusButton = (RadioButton) findViewById(R.id.radio0);
//			//				RadioButton fahrenheitButton = (RadioButton) findViewById(R.id.radio1);
//			//				if (text.getText().length() == 0) {
//			//					Toast.makeText(this, "Please enter a valid number",
//			//							Toast.LENGTH_LONG).show();
//			//					return;
//			//				}
//			//
//			//				float inputValue = Float.parseFloat(text.getText().toString());
//			//				if (celsiusButton.isChecked()) {
//			//					text.setText(String
//			//							.valueOf(convertFahrenheitToCelsius(inputValue)));
//			//					celsiusButton.setChecked(false);
//			//					fahrenheitButton.setChecked(true);
//			//				} else {
//			//					text.setText(String
//			//							.valueOf(convertCelsiusToFahrenheit(inputValue)));
//			//					fahrenheitButton.setChecked(false);
//			//					celsiusButton.setChecked(true);
//			//				}
//			break;
//		}	
//
//	}
	


}
    
