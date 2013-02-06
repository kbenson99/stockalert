package com.benson.stockalert.utility;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class Network {
	
	private final String myName = this.getClass().getSimpleName();
	
	private Context myContext;
	
	public Network(Context c){
		this.myContext = c;		
	}
	

	public boolean isOnline() {
		//returns if we have a network or WiFi connection
		Log.i(myName, "Checking if we have a network connection");
		ConnectivityManager cm = (ConnectivityManager) myContext.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		NetworkInfo mWifi = cm.getActiveNetworkInfo();
		//NetworkInfo mWifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		
		if (mWifi == null) {
			return false;
		}
		
		Log.i(myName, "mWifi.isConnected() = " + mWifi.isConnected());
		Log.i(myName, "mWifi.getTypeName () = " + mWifi.getTypeName ());
		Log.i(myName, "cm.getActiveNetworkInfo().isConnectedOrConnecting() = " + cm.getActiveNetworkInfo().isConnectedOrConnecting());
		
		Log.i(myName, "Network connection available:  " + (mWifi.isConnectedOrConnecting() || mWifi.isConnected()) );
		
		return ( mWifi.isConnectedOrConnecting() || mWifi.isConnected() );		
	}
}
