package com.benson.stockalert.prefs;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;

import com.benson.stockalert.R;
import com.benson.stockalert.service.AlertAlarm;


public class AlertPreferences extends PreferenceActivity 
{
		
	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		
		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
		

		sharedPrefs.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener() {   
			public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {  
				AlertAlarm myalarm = new AlertAlarm( AlertPreferences.this );
				myalarm.start();							
		   }     
			}); 
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) { 
		menu.add(Menu.NONE, 0, 0, "Show current settings");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case 0:
	            startActivity(new Intent(this, ShowSettingsActivity.class));
	            return true;
	    }
	    return false;
	}
	
		
}







