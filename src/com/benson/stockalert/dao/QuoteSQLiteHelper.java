package com.benson.stockalert.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class QuoteSQLiteHelper extends FinanceSQLiteHelper 
{
	public static final String TABLE_QUOTES = "quotes";
	public static final String TABLE_QUOTE = "quote";
	
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_REQUEST_ID = "request_id";
	
	public static final String COLUMN_QUOTE_STRING	= "ticker";
	public static final String COLUMN_EXECUTED 		= "executed";
		
	
	public static final int COLUMN_ID_INDEX 		= 1;	
	public static final int COLUMN_REQUEST_INDEX 		= 0;
	public static final int COLUMN_QUOTE_STRING_INDEX 	= 2;
	public static final int COLUMN_EXECUTED_INDEX 		= 1;	
	
	// Database creation sql statement
	public static final String QUOTE_TABLE_CREATE = "create table "
			+ TABLE_QUOTE + "( " 
			+ COLUMN_ID + " integer primary key autoincrement, " 
			+ COLUMN_EXECUTED + " integer default 0 "			
			+ ");";
	
	public static final String QUOTES_TABLE_CREATE = "create table "
			+ TABLE_QUOTES + "( " 
			+ COLUMN_REQUEST_ID + " integer primary key autoincrement, "			
			+ COLUMN_ID + " integer not null, " 
			+ COLUMN_QUOTE_STRING + " text not null "			
			+ ");";	

	public QuoteSQLiteHelper(Context context) 
	{
		super(context);
	}

}


