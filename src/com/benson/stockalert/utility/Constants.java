package com.benson.stockalert.utility;

import java.io.File;

import android.os.Environment;

public class Constants {

		public static final String DEBUG = "Debug";
		public static final String INFO = "INFO";

		public static final int    QUOTE_NOT_EXECUTED			= 0;
		public static final int    QUOTE_EXECUTED			    = 1;		
		
		public static final int    STOCK_NOT_ALERTED			= 0;
		public static final int    STOCK_ALERTED			    = 1;
		
		public static final String JSON_EXCHANGE_KEY			= "exch";
		public static final String JSON_CHANGE_SIGN_KEY			= "chg_sign";
		public static final String JSON_TICKER_KEY 				= "symbol";
		public static final String JSON_PRICE_KEY				= "last";
		public static final String JSON_CHANGE_KEY				= "chg_t";
		public static final String JSON_CHANGE_PERCENT_KEY		= "pchg";
		public static final String JSON_NAME_KEY				= "name";
		
		public static final String JSON_HI_KEY					= "hi";
		public static final String JSON_LO_KEY					= "lo";
		public static final String JSON_OPEN_KEY				= "opn";
		public static final String JSON_PREVIOUS_CLOSE_KEY		= "cl";
		

		public static final String JSON_VOLUME_KEY				= "vl";
		public static final String JSON_30VOLUME_KEY			= "adv_30";
		
		
		
		public static final String NO_NETWORK_CONNECTION		= "No Network Connection";
		
		public static final String DATABASE_NAME 				= "stockalerts.db";
		
		public static final String STOCK_CSV_NAME				= "stocks.csv";
		
		public static final String exportDir = new File(Environment.getExternalStorageDirectory(), 
												"FinanceAlerts" + File.separator + "data").getAbsolutePath();
		
		
		public static final String CSV_EXPORT					= "Exporting";
		public static final String CSV_LOAD						= "Loading";
		
		public static final String INTER_ACTIVITY_QUOTE_TAG		= "REQUESTED_QUOTE_TAG";
		
		
		public static final int FILTER_GAINERS_ONLY				= 1;
		public static final int FILTER_LOSERS_ONLY				= -1;
		public static final int FILTER_SHOW_ALL					= 0;		
		public static final int FILTER_LESS_5					= 5;
		public static final int FILTER_LESS_10					= 10;
		public static final int FILTER_LESS_20					= 20;
		public static final int FILTER_GREATER_20				= 25;
		
}
