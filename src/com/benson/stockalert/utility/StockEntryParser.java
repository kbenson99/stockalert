package com.benson.stockalert.utility;

import com.googlecode.jcsv.reader.CSVEntryParser;

public class StockEntryParser implements CSVEntryParser<Stock> {
	  
	@Override
	  public Stock parseEntry(String... data) 
		{
	    if (data.length == 0 || data.length == 1 || data.length > 3) {
	      throw new IllegalArgumentException("data is not a valid stock record");
	    }
	                
	    String stock = data[0];
	    String exchange = "UNKNOWN";
	    double breakout = Double.parseDouble(data[1]);
	    
	    int alerted = Constants.STOCK_NOT_ALERTED;
	    if (data.length == 3)
	    {
	    	alerted = Integer.parseInt(data[2]);
	    }

	                
	    return new Stock(stock, exchange, breakout, alerted);
	  }	
}

