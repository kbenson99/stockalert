package com.benson.stockalert.utility;

import com.benson.stockalert.model.Alert;
import com.googlecode.jcsv.writer.CSVEntryConverter;

public class StockEntryConverter implements CSVEntryConverter<Alert> {
	  
	@Override
	  public String[] convertEntry(Alert p) {
	    String[] columns = new String[3];

	    columns[0] = p.getTicker();
	    columns[1] = String.valueOf(p.getBreakout());
	    columns[2] = String.valueOf(p.getAlerted());

	    return columns;
	  }
}


