package com.benson.stockalert.utility;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper {

	public static final String TABLE_STOCKS = "stocks";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_STOCK = "stock";
	public static final String COLUMN_EXCHANGE = "exchange";
	public static final String COLUMN_BREAKOUT = "breakout";
	public static final String COLUMN_ALERTED = "alerted";	
	
	public static final int COLUMN_ID_INDEX 		= 0;	
	public static final int COLUMN_STOCK_INDEX 		= 1;
	public static final int COLUMN_EXCHANGE_INDEX 	= 2;
	public static final int COLUMN_BREAKOUT_INDEX 	= 3;
	public static final int COLUMN_ALERTED_INDEX 	= 4;
		
	private static final int DATABASE_VERSION = 2;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_STOCKS + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_STOCK + " text not null, " 
			+ COLUMN_EXCHANGE + " text not null, "
			+ COLUMN_BREAKOUT + " decimal, "
			+ COLUMN_ALERTED + " integer default 0 "			
			+ ");";

	public MySQLiteHelper(Context context) {
		super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {	
		database.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MySQLiteHelper.class.getName(),
				"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOCKS);
		onCreate(db);
	}

}


