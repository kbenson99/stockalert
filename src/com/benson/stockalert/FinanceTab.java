package com.benson.stockalert;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.TabHost;
import android.widget.Toast;

import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Network;


@SuppressWarnings("deprecation")
public class FinanceTab extends TabActivity  
{
	TabHost myHost;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {    		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().build());
		
//        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
//        .detectDiskReads()
//        .detectDiskWrites()
//        .detectNetwork()   // or .detectAll() for all detectable problems
//        .penaltyLog()
//        .build());
//        
//    	StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//        .detectLeakedSqlLiteObjects()        
//        .penaltyLog()
//        .penaltyDeath()
//        .build());		
		
		super.onCreate(savedInstanceState);    
		setContentView(R.layout.tabhost); 	
		
	
		Resources res = getResources();
		
		
		if (!new Network(this).isOnline()) {
			Toast mytoast = Toast.makeText(this, Constants.NO_NETWORK_CONNECTION, Toast.LENGTH_LONG);
			mytoast.show();
		}
		else 
		{
			// Reusable Intent for each tab    
			// Create an Intent to launch an Activity for the tab (to be reused)
		
			// Resource object to get Drawables    
			myHost = getTabHost(); 
			
			// Resusable TabSpec for each tab    
			Intent intent;
			
			// The activity TabHost    
			TabHost.TabSpec spec;				
			
			intent = new Intent().setClass(this, Alerts.class);		
			// Initialize a TabSpec for each tab and add it to the TabHost    
			spec = myHost.newTabSpec("alert").setIndicator("Alert",                      
					res.getDrawable(R.drawable.ic_tab_alerts))                  
					.setContent(intent);    
			myHost.addTab(spec);
			
			intent = new Intent().setClass(this, Quotes.class);    
			spec = myHost.newTabSpec("quote").setIndicator("Quotes",                      
					res.getDrawable(R.drawable.ic_tab_artists))                  
					.setContent(intent);    		
			myHost.addTab(spec);
			
			intent = new Intent().setClass(this, Actives.class);    
			spec = myHost.newTabSpec("actives").setIndicator("Actives",                      
					res.getDrawable(R.drawable.shares))                  
					.setContent(intent);    		
			myHost.addTab(spec);			
			
			myHost.setCurrentTab(0);				
		}
		//start the Alert service		
		startService(new Intent(com.benson.stockalert.service.AlertService.class.getName()));		
	}
	
	public TabHost getTab()
	{
		return myHost;
	}
}

