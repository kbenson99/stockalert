package com.benson.stockalert.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class ActiveSQLiteHelper extends FinanceSQLiteHelper 
{
	public static final String TABLE_ACTIVES = "actives";
	
	public static final String COLUMN_ID = "_id";
	
	public static final String COLUMN_TICKER		= "ticker";
	public static final String COLUMN_QTY			= "quantity";
	public static final String COLUMN_BUY_PRICE		= "buyprice";
	public static final String COLUMN_BROKER 		= "broker";
	public static final String COLUMN_DATE	 		= "date";
	public static final String COLUMN_ACTIVE 		= "active";

	public static final int COLUMN_ID_INDEX 		= 0;
	public static final int COLUMN_TICKER_INDEX		= 1;
	public static final int COLUMN_QTY_INDEX		= 2;
	public static final int COLUMN_BUY_PRICE_INDEX	= 3;
	public static final int COLUMN_BROKER_INDEX 	= 4;
	public static final int COLUMN_DATE_INDEX		= 5;
	public static final int COLUMN_ACTIVE_INDEX		= 6;	
	
	
	public static final String ACTIVES_TABLE_CREATE = "create table "
			+ TABLE_ACTIVES + "( " 		
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_TICKER + " text not null, " 
			+ COLUMN_QTY + " integer not null, "
			+ COLUMN_BUY_PRICE + " decimal not null, "
			+ COLUMN_BROKER + " text not null, "
			+ COLUMN_DATE + " CHAR(10) not null, "
			+ COLUMN_ACTIVE + " integer default 1 "			
			+ ");";	

	public ActiveSQLiteHelper(Context context) {
		super(context);
	}


}


