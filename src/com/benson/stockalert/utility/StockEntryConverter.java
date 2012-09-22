package com.benson.stockalert.utility;

import com.googlecode.jcsv.writer.CSVEntryConverter;

public class StockEntryConverter implements CSVEntryConverter<Stock> {
	  
	@Override
	  public String[] convertEntry(Stock p) {
	    String[] columns = new String[2];

	    columns[0] = p.getStock();
	    columns[1] = String.valueOf(p.getBreakout());

	    return columns;
	  }
}


