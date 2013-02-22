package com.benson.stockalert.utility;

import com.benson.stockalert.model.Alert;
import com.googlecode.jcsv.reader.CSVEntryParser;

public class StockEntryParser implements CSVEntryParser<Alert> {
	  
	@Override
	  public Alert parseEntry(String... data) 
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

	                
	    return new Alert(stock, exchange, breakout, alerted);
	  }	
}

