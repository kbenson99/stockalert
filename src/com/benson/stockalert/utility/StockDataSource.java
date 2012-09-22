package com.benson.stockalert.utility;

import java.util.ArrayList;
import java.util.List;

import com.benson.stockalert.R;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StockDataSource {

	// Database fields
	private SQLiteDatabase database;
	private MySQLiteHelper dbHelper;
	private String[] allColumns = new String[5];
	private Context myContext;


	public StockDataSource(Context context) {
		allColumns[MySQLiteHelper.COLUMN_ID_INDEX] = MySQLiteHelper.COLUMN_ID;
		allColumns[MySQLiteHelper.COLUMN_STOCK_INDEX] = MySQLiteHelper.COLUMN_STOCK;
		allColumns[MySQLiteHelper.COLUMN_EXCHANGE_INDEX] = MySQLiteHelper.COLUMN_EXCHANGE;
		allColumns[MySQLiteHelper.COLUMN_BREAKOUT_INDEX] = MySQLiteHelper.COLUMN_BREAKOUT;
		allColumns[MySQLiteHelper.COLUMN_ALERTED_INDEX] = MySQLiteHelper.COLUMN_ALERTED;
		
		this.myContext = context;
		
		dbHelper = new MySQLiteHelper(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}
	
	
	public void updateStock(Long Id, double breakout) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_BREAKOUT, breakout );
		database.update(MySQLiteHelper.TABLE_STOCKS, values, MySQLiteHelper.COLUMN_ID + "=" + Id, null); 
	}
	
	
	public void updateStockAlert(Long Id, int alerted) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_ALERTED, alerted );
		database.update(MySQLiteHelper.TABLE_STOCKS, values, MySQLiteHelper.COLUMN_ID + "=" + Id, null);		
	}
	
	
	public void clearStocks()
	{
		database.delete(MySQLiteHelper.TABLE_STOCKS, null, null);
	}
	
	

	public Stock createStock(String stock, String exchange, double breakout) {
		ContentValues values = new ContentValues();
		values.put(MySQLiteHelper.COLUMN_STOCK, stock.toUpperCase() );
		values.put(MySQLiteHelper.COLUMN_EXCHANGE, exchange );		
		values.put(MySQLiteHelper.COLUMN_BREAKOUT, breakout );
		long insertId = database.insert(MySQLiteHelper.TABLE_STOCKS, null,
				values);
		
		Log.d("db", insertId + "");
		
		Cursor cursor = null;
		Stock newStock;
		try {
			cursor = database.query(MySQLiteHelper.TABLE_STOCKS,
									allColumns, MySQLiteHelper.COLUMN_ID + " = " + insertId, null,
									null, null, null);
			cursor.moveToFirst();
			newStock = cursorToStock(cursor);			
		}
		finally {
			if (cursor != null){
				cursor.close();
			}
		}
		return newStock;
	}

	public void deleteStock(Stock stock) {
		long id = stock.getId();	
		database.delete(MySQLiteHelper.TABLE_STOCKS, MySQLiteHelper.COLUMN_ID
						+ " = " + id, null);
	}

	public ArrayList<Stock> getAllStocks() {
		ArrayList<Stock> stocks = new ArrayList<Stock>();
		
		Cursor cursor = null;
		try {
			cursor = database.query(MySQLiteHelper.TABLE_STOCKS,
									allColumns, null, null, null, null, null);

			Boolean verbose = Boolean.parseBoolean( this.myContext.getString(R.bool.verbose) );
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				Stock stock = cursorToStock(cursor);
				stocks.add(stock);
				if (verbose) {
					Log.d(this.getClass().getSimpleName(), stock.toString() );	
				}
				
				cursor.moveToNext();
			}		
		}
		finally {
			if (cursor != null){
				cursor.close();
			}
		}
		return stocks;
	}
	
	public Stock getStock(Long Id) {	
		Cursor cursor = null;
		Stock stock;
		try {
			cursor = database.query(MySQLiteHelper.TABLE_STOCKS,
									allColumns, MySQLiteHelper.COLUMN_ID + " = " + Id, null,
									null, null, null, null);
			cursor.moveToFirst();
			stock = cursorToStock(cursor);			
		}
		finally {
			if (cursor != null){
				cursor.close();
			}
		}	
		return stock;
	}
	

	private Stock cursorToStock(Cursor cursor) {
		Stock stock = new Stock();
		stock.setId(cursor.getLong( MySQLiteHelper.COLUMN_ID_INDEX ));
		stock.setStock(cursor.getString( MySQLiteHelper.COLUMN_STOCK_INDEX ));
		stock.setExchange(cursor.getString(MySQLiteHelper.COLUMN_EXCHANGE_INDEX));
		stock.setBreakout(cursor.getDouble(MySQLiteHelper.COLUMN_BREAKOUT_INDEX));
		stock.setAlerted(cursor.getInt(MySQLiteHelper.COLUMN_ALERTED_INDEX));
		return stock;
	}
}


;