package com.benson.stockalert;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;
import android.widget.Toast;

import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Network;


public class TabDemo extends TabActivity  {
  
	@Override
	public void onCreate(Bundle savedInstanceState) {    
		super.onCreate(savedInstanceState);    
		setContentView(R.layout.tabhost); 
		
	
		Resources res = getResources(); 
		
		// Resource object to get Drawables    
		TabHost tabHost = getTabHost(); 
		
		// The activity TabHost    
		TabHost.TabSpec spec;
		TabHost.TabSpec spec1;
		
		// Resusable TabSpec for each tab    
		Intent intent;  
		
		// Reusable Intent for each tab    
		// Create an Intent to launch an Activity for the tab (to be reused)    
		intent = new Intent().setClass(this, Alerts.class);    
		
		// Initialize a TabSpec for each tab and add it to the TabHost    
		spec1 = tabHost.newTabSpec("alert").setIndicator("Alert",                      
				res.getDrawable(R.drawable.ic_tab_alerts))                  
				.setContent(intent);    
		//tabHost.addTab(spec1);    
		
		// Do the same for the other tabs    
		intent = new Intent().setClass(this, Quote.class);    
		spec = tabHost.newTabSpec("quote").setIndicator("Quote",                      
				res.getDrawable(R.drawable.ic_tab_artists))                  
				.setContent(intent);    
		//tabHost.addTab(spec);
		
		
		if (!new Network(this).isOnline()) {
			Toast mytoast = Toast.makeText(this, Constants.NO_NETWORK_CONNECTION, Toast.LENGTH_LONG);
			mytoast.show();
		}
		else {
			tabHost.addTab(spec1);
			tabHost.addTab(spec);
			tabHost.setCurrentTab(0);				
		}
		//start the Alert service		
		startService(new Intent(com.benson.stockalert.service.AlertService.class.getName()));		
	}
}

