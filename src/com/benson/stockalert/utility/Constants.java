package com.benson.stockalert.utility;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

public class Constants {

		public static final String DEBUG = "Debug";
		public static final String INFO = "INFO";
		
		
		public static final String JSON_EXCHANGE_KEY			= "StockExchange";
		public static final String JSON_TICKER_KEY 				= "Symbol";
		public static final String JSON_PRICE_KEY				= "LastTradePriceOnly";
		public static final String JSON_CHANGE_KEY				= "Change";
		public static final String JSON_CHANGE_PERCENT_KEY		= "ChangeinPercent";
		public static final String JSON_NAME_KEY				= "Name";
		
		public static final String NO_NETWORK_CONNECTION		= "No Network Connection";
		
		public static final String DATABASE_NAME 				= "stockalerts.db";
		
		public static final String STOCK_CSV_NAME				= "stocks.csv";
		
		public static final String exportDir = new File(Environment.getExternalStorageDirectory(), "FinanceAlerts" + File.separator + "data").getAbsolutePath();
		
		
		public static final String CSV_EXPORT					= "Exporting";
		public static final String CSV_LOAD						= "Loading";
		
		private static ArrayList JSON_FIELDS					= new ArrayList();
		
		
		public static ArrayList QueryFields()
		{
			JSON_FIELDS.clear();
			JSON_FIELDS.add( JSON_EXCHANGE_KEY);
			
			JSON_FIELDS.add(JSON_TICKER_KEY );
			JSON_FIELDS.add(JSON_PRICE_KEY	);
			JSON_FIELDS.add(JSON_CHANGE_KEY	);
			JSON_FIELDS.add(JSON_CHANGE_PERCENT_KEY	);
			JSON_FIELDS.add(JSON_NAME_KEY );
			
			return JSON_FIELDS;		
		}
}
