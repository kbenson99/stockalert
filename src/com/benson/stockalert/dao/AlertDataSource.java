package com.benson.stockalert.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;

import com.benson.stockalert.model.Alert;

public class AlertDataSource extends FinanceDataSource 
{
	// Database fields
	private String[] allColumns = new String[5];

	public AlertDataSource(Context context) {
		allColumns[AlertSQLiteHelper.COLUMN_ID_INDEX] = AlertSQLiteHelper.COLUMN_ID;
		allColumns[AlertSQLiteHelper.COLUMN_STOCK_INDEX] = AlertSQLiteHelper.COLUMN_TICKER;
		allColumns[AlertSQLiteHelper.COLUMN_EXCHANGE_INDEX] = AlertSQLiteHelper.COLUMN_EXCHANGE;
		allColumns[AlertSQLiteHelper.COLUMN_BREAKOUT_INDEX] = AlertSQLiteHelper.COLUMN_BREAKOUT;
		allColumns[AlertSQLiteHelper.COLUMN_ALERTED_INDEX] = AlertSQLiteHelper.COLUMN_ALERTED;
			
		dbHelper = new AlertSQLiteHelper(context);
	}
	
	
	public void updateStock(Long Id, double breakout, int alerted) 
	{
		this.openToWrite();
		try
		{
			ContentValues values = new ContentValues();
			values.put(AlertSQLiteHelper.COLUMN_BREAKOUT, breakout );
			values.put(AlertSQLiteHelper.COLUMN_ALERTED, alerted );
			database.update(AlertSQLiteHelper.TABLE_STOCKS, values, AlertSQLiteHelper.COLUMN_ID + "=" + Id, null);			
		}
		finally
		{
			if (database != null)
			{
				this.close();
			}
		}
 
	}
	
	
	public void updateStockAlert(Long Id, int alerted) 
	{
		this.openToWrite();
		try
		{
			ContentValues values = new ContentValues();
			values.put(AlertSQLiteHelper.COLUMN_ALERTED, alerted );
			database.update(AlertSQLiteHelper.TABLE_STOCKS, values, AlertSQLiteHelper.COLUMN_ID + "=" + Id, null);			
		}
		finally
		{
			if (database != null)
			{
				this.close();
			}
		}		
	}
	
	
	public void clearStocks()
	{
		this.openToWrite();
		try
		{
			database.delete(AlertSQLiteHelper.TABLE_STOCKS, null, null);
		}
		finally
		{
			if (database != null)
			{
				this.close();
			}
		}			
	}
	
	

	public void createStock(String stock, String exchange, double breakout, int alerted) 
	{	
		this.openToWrite();
		
		
        InsertHelper ih = new InsertHelper(database, AlertSQLiteHelper.TABLE_STOCKS);
        
        // Get the numeric indexes for each of the columns that we're updating
        final int stockValue = ih.getColumnIndex(AlertSQLiteHelper.COLUMN_TICKER);
        final int exchangeValue = ih.getColumnIndex(AlertSQLiteHelper.COLUMN_EXCHANGE);
        final int breakoutValue = ih.getColumnIndex(AlertSQLiteHelper.COLUMN_BREAKOUT);
        final int alertedValue = ih.getColumnIndex(AlertSQLiteHelper.COLUMN_ALERTED);
        
        database.execSQL("PRAGMA synchronous=OFF");
        database.setLockingEnabled(false);
        database.beginTransaction();
        try 
        {			 
            // Get the InsertHelper ready to insert a single row
            ih.prepareForInsert();
 
            // Add the data for each column
            ih.bind(stockValue, stock.toUpperCase(Locale.ENGLISH));	               
            ih.bind(exchangeValue, exchange);
            ih.bind(breakoutValue, breakout);
            ih.bind(alertedValue, alerted);
 
            // Insert the row into the database.
            ih.execute();
         
			database.setTransactionSuccessful();
        }
        finally 
        {
        	database.endTransaction();
            database.setLockingEnabled(true);
            database.execSQL("PRAGMA synchronous=NORMAL");	        	
            ih.close();            

        	this.close();

        }		
	}

	public void deleteStock(Alert stock) 
	{
		this.openToWrite();
		long id = stock.getId();
		try
		{
			database.delete(AlertSQLiteHelper.TABLE_STOCKS, AlertSQLiteHelper.COLUMN_ID
					+ " = " + id, null);			
		}
		finally
		{
			if (database != null)
			{
				this.close();
			}
		}
	}

	public List<Alert> getAllStocks() {
		List<Alert> stocks = new ArrayList<Alert>();
		
		this.openToRead();
		Cursor cursor = null;
		try {
			cursor = database.query(AlertSQLiteHelper.TABLE_STOCKS,
									allColumns, null, null, null, null, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) 
			{
				Alert stock = cursorToStock(cursor);
				stocks.add(stock);
				
				cursor.moveToNext();
			}		
		}
		finally {
			if (cursor != null)
			{
				cursor.close();
			}
			if (database != null)
			{
				this.close();
			}
		}
		return stocks;
	}
	
	public Alert getStock(Long Id) {	
		Cursor cursor = null;
		Alert stock;
		
		this.openToRead();
		try {
			cursor = database.query(AlertSQLiteHelper.TABLE_STOCKS,
									allColumns, AlertSQLiteHelper.COLUMN_ID + " = " + Id, null,
									null, null, null, null);
			cursor.moveToFirst();
			stock = cursorToStock(cursor);			
		}
		finally {
			if (cursor != null)
			{
				cursor.close();
			}
			if ( database != null)
			{
				this.close();
			}
		}	
		return stock;
	}
	

	private Alert cursorToStock(Cursor cursor) {
		Alert stock = new Alert();
		stock.setId(cursor.getLong( AlertSQLiteHelper.COLUMN_ID_INDEX ));
		stock.setTicker(cursor.getString( AlertSQLiteHelper.COLUMN_STOCK_INDEX ));
		stock.setExchange(cursor.getString(AlertSQLiteHelper.COLUMN_EXCHANGE_INDEX));
		stock.setBreakout(cursor.getDouble(AlertSQLiteHelper.COLUMN_BREAKOUT_INDEX));
		stock.setAlerted(cursor.getInt(AlertSQLiteHelper.COLUMN_ALERTED_INDEX));
		return stock;
	}
}


;