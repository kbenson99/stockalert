package com.benson.stockalert.utility;

import com.googlecode.jcsv.writer.CSVEntryConverter;

public class StockEntryConverter implements CSVEntryConverter<Stock> {
	  
	@Override
	  public String[] convertEntry(Stock p) {
	    String[] columns = new String[3];

	    columns[0] = p.getStock();
	    columns[1] = String.valueOf(p.getBreakout());
	    columns[2] = String.valueOf(p.getAlerted());

	    return columns;
	  }
}


