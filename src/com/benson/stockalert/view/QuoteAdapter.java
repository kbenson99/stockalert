package com.benson.stockalert.view;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import com.benson.stockalert.model.Quote;
import com.benson.stockalert.model.Stock;
import com.benson.stockalert.utility.Constants;

public class QuoteAdapter extends FinanceAdapter
{
	public QuoteAdapter(Context context, int textViewResourceId, 
			ArrayList<? super Stock> items) 
	{
		super(context, textViewResourceId, items);
	}
      
    
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent) 
	{
		View row = convertView;
				
		row = super.getView(position, convertView, parent);
		
		Quote o = (Quote) items.get(position);
		try
		{
			if (this.localJSONObject!= null)
			{
				String stockname = this.localJSONObject.getString(Constants.JSON_NAME_KEY);
				o.setName(stockname);				
			}
		}		
		catch (JSONException je) 
		{
			Log.e(this.myName, "Failed to obtain name for " + o.getTicker());
		}
		
		return row;
	}
}
