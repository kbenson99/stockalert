package com.benson.stockalert.utility;

import java.io.File;
import java.util.ArrayList;

import android.os.Environment;

public class Constants {

		public static final String DEBUG = "Debug";
		public static final String INFO = "INFO";
		
		public static final int    STOCK_NOT_ALERTED			= 0;
		public static final int    STOCK_ALERTED			    = 1;
		
		public static final String JSON_EXCHANGE_KEY			= "exch";
		public static final String JSON_CHANGE_SIGN_KEY			= "chg_sign";
		public static final String JSON_TICKER_KEY 				= "symbol";
		public static final String JSON_PRICE_KEY				= "last";
		public static final String JSON_CHANGE_KEY				= "chg_t";
		public static final String JSON_CHANGE_PERCENT_KEY		= "pchg";
		public static final String JSON_NAME_KEY				= "name";
		
		public static final String JSON_HI_KEY				= "hi";
		public static final String JSON_LO_KEY				= "lo";
		
		
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
