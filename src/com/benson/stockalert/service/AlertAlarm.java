package com.benson.stockalert.service;

import com.benson.stockalert.R;
import com.benson.stockalert.R.integer;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class AlertAlarm {

	private final String myName = this.getClass().getSimpleName();
	
	private Context myContext;
	
	public int m_stockCalls = 0;

	public AlertAlarm(Context context) {
		
		this.myContext = context;
	}
	
	public void start() {				
		Log.i(myName, "Alert is being created");
		
		int pause = Integer.parseInt(this.myContext.getString(R.integer.servicePause)) * 1000;

		SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.myContext);

		
		int sharedPause = Integer.parseInt( sharedPrefs.getString("quote_interval", "-1") );
		if (sharedPause != -1 )
		{
			pause = sharedPause;
		}

		boolean shouldStart = sharedPrefs.getBoolean("check_quotes", true);
		if (shouldStart) {
			Log.i(myName, "Starting the Alert notification alarm");
			Log.i(myName,  "Pause between runs is " + pause /1000 + " seconds");
			Intent intent = new Intent(this.myContext, StockBroadcastReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast( this.myContext.getApplicationContext(), 234324243, intent, 0);
	
			AlarmManager alarmManager = (AlarmManager) this.myContext.getSystemService(this.myContext.ALARM_SERVICE);
			long firstTime = 30000; //30 seconds		
			alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, firstTime, pause, pendingIntent);
		}
		else
		{
			cancel();			
		}		
	}
	
	
	public void cancel() {
		Log.i(myName, "Canceling the Alert notification alarm");
		Intent intent = new Intent(this.myContext, StockBroadcastReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast( this.myContext.getApplicationContext(), 234324243, intent, 0);

		AlarmManager alarmManager = (AlarmManager) this.myContext.getSystemService(this.myContext.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}	
}
