package com.benson.stockalert.utility;

import com.googlecode.jcsv.reader.CSVEntryParser;

public class StockEntryParser implements CSVEntryParser<Stock> {
	  
	@Override
	  public Stock parseEntry(String... data) {
	    if (data.length != 2) {
	      throw new IllegalArgumentException("data is not a valid stock record");
	    }
	                
	    String stock = data[0];
	    String exchange = "UNKNOWN";
	    double breakout = Double.parseDouble(data[1]);
	                
	    return new Stock(stock, exchange, breakout);
	  }	
}

