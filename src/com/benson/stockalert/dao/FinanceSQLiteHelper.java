package com.benson.stockalert.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.benson.stockalert.utility.Constants;

public class FinanceSQLiteHelper extends SQLiteOpenHelper 
{	
	private final String myName = this.getClass().getSimpleName();	
	
	private static final int DATABASE_VERSION = 7;

	public FinanceSQLiteHelper(Context context) 
	{
		super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) 
	{		
		for (Schema.DB_CREATE_SQL value : Schema.DB_CREATE_SQL.values())
		{
			database.execSQL(value.getSQL());
		}
	}
	
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) 
	{
		//Do nothing
		Log.i(QuoteSQLiteHelper.class.getName(), "onUpgrade() within " + QuoteSQLiteHelper.class.getName());
		Log.i(QuoteSQLiteHelper.class.getName(),
						"Upgrading database from version " + oldVersion + " to "
						+ newVersion + ", which will destroy all old data");
		
		for (Schema.DB_TABLES value : Schema.DB_TABLES.values())
		{
			db.execSQL("DROP TABLE IF EXISTS " + value.getTable());
		}
		
		onCreate(db);
	}

}


