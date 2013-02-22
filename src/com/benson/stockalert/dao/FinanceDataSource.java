package com.benson.stockalert.dao;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

abstract public class FinanceDataSource {

	// Database fields
	protected SQLiteDatabase database;
	protected FinanceSQLiteHelper dbHelper;
	
	public void openToWrite() throws SQLException 
	{
		database = dbHelper.getWritableDatabase();
	}

	public void openToRead() throws SQLException 
	{
		database = dbHelper.getReadableDatabase();
	}		

	public void close() {
		dbHelper.close();
	}

}
