package com.benson.stockalert.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import android.util.Log;


public class AlertService extends Service {

	private final String myName = this.getClass().getSimpleName();

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.i(myName, "Service is being created");
		
		AlertAlarm myalarm = new AlertAlarm( this );
		myalarm.start();	
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.i(myName, "Service destroying");
	}
	
}
