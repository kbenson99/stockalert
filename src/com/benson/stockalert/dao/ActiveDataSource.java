package com.benson.stockalert.dao;

import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;

import com.benson.stockalert.model.Active;

public class ActiveDataSource extends FinanceDataSource 
{
    private final String		myName = this.getClass().getSimpleName();	

	public ActiveDataSource(Context context) 
	{
		dbHelper = new ActiveSQLiteHelper(context);
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
	
	
	public void clearActives()
	{
		this.openToWrite();
		try
		{
			database.delete(ActiveSQLiteHelper.TABLE_ACTIVES, null, null);
		}
		finally
		{
			if (database != null)
			{
				this.close();
			}
		}			
	}	
	

	public void createActive(String stock, int quantity, String broker, String date) 
	{	
		this.openToWrite();		
		
        InsertHelper ih = new InsertHelper(database, ActiveSQLiteHelper.TABLE_ACTIVES);
        
        // Get the numeric indexes for each of the columns that we're updating
        final int stockValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_TICKER);
        final int quantityValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_QTY);
        final int brokerValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_BROKER);
        final int dateValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_DATE);
        
        database.execSQL("PRAGMA synchronous=OFF");
        database.setLockingEnabled(false);
        database.beginTransaction();
        try 
        {			 
            // Get the InsertHelper ready to insert a single row
            ih.prepareForInsert();
 
            // Add the data for each column
            ih.bind(stockValue, stock.toUpperCase(Locale.ENGLISH));	               
            ih.bind(quantityValue, quantity);
            ih.bind(brokerValue, broker);
            ih.bind(dateValue, date);
 
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

	public void deleteStock(Active stock) 
	{
		this.openToWrite();
		long id = stock.getId();
		try
		{
			database.delete(ActiveSQLiteHelper.TABLE_ACTIVES, ActiveSQLiteHelper.COLUMN_ID
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

//	public List<Alert> getAllStocks() {
//		List<Alert> stocks = new ArrayList<Alert>();
//		
//		this.openToRead();
//		Cursor cursor = null;
//		try {
//			cursor = database.query(AlertSQLiteHelper.TABLE_STOCKS,
//									allColumns, null, null, null, null, null);
//
//			cursor.moveToFirst();
//			while (!cursor.isAfterLast()) 
//			{
//				Alert stock = cursorToStock(cursor);
//				stocks.add(stock);
//				
//				cursor.moveToNext();
//			}		
//		}
//		finally {
//			if (cursor != null)
//			{
//				cursor.close();
//			}
//			if (database != null)
//			{
//				this.close();
//			}
//		}
//		return stocks;
//	}
	


	private Active cursorToActive(Cursor cursor) {
		Active active = new Active();
		active.setId(cursor.getLong(ActiveSQLiteHelper.COLUMN_ID_INDEX));
		active.setTicker(cursor.getString(ActiveSQLiteHelper.COLUMN_TICKER_INDEX));
		active.setBroker(cursor.getString(ActiveSQLiteHelper.COLUMN_BROKER_INDEX));
		active.setDate(cursor.getString(ActiveSQLiteHelper.COLUMN_DATE_INDEX));
		active.setActive(cursor.getInt(ActiveSQLiteHelper.COLUMN_ACTIVE_INDEX));
		active.setQuantity(cursor.getInt(ActiveSQLiteHelper.COLUMN_QTY_INDEX));
		return active;
	}
}


;