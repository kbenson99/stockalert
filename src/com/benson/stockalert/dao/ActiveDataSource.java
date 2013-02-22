package com.benson.stockalert.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.util.Log;

import com.benson.stockalert.model.Active;
import com.benson.stockalert.model.Quote;

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
	

	public void createActive(String stock, int quantity, double buyPrice, String broker, String date) 
	{	
		Log.i(this.myName, date);
		this.openToWrite();		
		
        InsertHelper ih = new InsertHelper(database, ActiveSQLiteHelper.TABLE_ACTIVES);
        
        // Get the numeric indexes for each of the columns that we're updating
        final int stockValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_TICKER);
        final int quantityValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_QTY);
        final int brokerValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_BROKER);
        final int dateValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_DATE);
        final int buyPriceValue = ih.getColumnIndex(ActiveSQLiteHelper.COLUMN_BUY_PRICE);
        
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
            ih.bind(buyPriceValue, buyPrice);            
 
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

	public List<Active> getActives() {
		List<Active> stocks = new ArrayList<Active>();
		
		Cursor cursor = null;
		Active active;
		this.openToRead();
		try {
			cursor = database.rawQuery("select * from "
					+ ActiveSQLiteHelper.TABLE_ACTIVES + " where "
					+ ActiveSQLiteHelper.COLUMN_ACTIVE + " = 1", null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast())
			{
				active = cursorToActive(cursor);
				stocks.add(active);

				cursor.moveToNext();
			}
		} 
		finally 
		{
			if (cursor != null) {
				cursor.close();
			}
			if (database != null) {
				this.close();
			}
		}

		return stocks;
	}
	


	private Active cursorToActive(Cursor cursor) {
		Active active = new Active();
		active.setId(cursor.getLong(ActiveSQLiteHelper.COLUMN_ID_INDEX));
		active.setTicker(cursor.getString(ActiveSQLiteHelper.COLUMN_TICKER_INDEX));
		active.setBroker(cursor.getString(ActiveSQLiteHelper.COLUMN_BROKER_INDEX));
		active.setDate(cursor.getString(ActiveSQLiteHelper.COLUMN_DATE_INDEX));
		active.setActive(cursor.getInt(ActiveSQLiteHelper.COLUMN_ACTIVE_INDEX));
		active.setQuantity(cursor.getInt(ActiveSQLiteHelper.COLUMN_QTY_INDEX));
		active.setBuyPrice(cursor.getDouble(ActiveSQLiteHelper.COLUMN_BUY_PRICE_INDEX));
		return active;
	}
}


;