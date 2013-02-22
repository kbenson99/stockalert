package com.benson.stockalert.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class AlertSQLiteHelper extends FinanceSQLiteHelper 
{
	public static final String TABLE_STOCKS = "stocks";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_TICKER	= "ticker";
	public static final String COLUMN_EXCHANGE	= "exchange";
	public static final String COLUMN_BREAKOUT	= "breakout";
	public static final String COLUMN_ALERTED	= "alerted";	
	
	public static final int COLUMN_ID_INDEX 		= 0;
	public static final int COLUMN_STOCK_INDEX 		= 1;
	public static final int COLUMN_EXCHANGE_INDEX 	= 2;
	public static final int COLUMN_BREAKOUT_INDEX 	= 3;
	public static final int COLUMN_ALERTED_INDEX 	= 4;
	
	// Database creation sql statement
	public static final String TABLE_CREATE = "create table "
			+ TABLE_STOCKS + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TICKER + " text not null, " 
			+ COLUMN_EXCHANGE + " text not null, "
			+ COLUMN_BREAKOUT + " decimal, "
			+ COLUMN_ALERTED + " integer default 0 "			
			+ ");";

	public AlertSQLiteHelper(Context context) 
	{
		super(context);
	}
	
}


