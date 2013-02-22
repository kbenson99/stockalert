package com.benson.stockalert.view;

import java.util.ArrayList;

import org.json.JSONException;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.benson.stockalert.R;
import com.benson.stockalert.model.Alert;
import com.benson.stockalert.model.Stock;
import com.benson.stockalert.utility.Constants;

public class ActiveAdapter extends FinanceAdapter
{
	
	public ActiveAdapter(Context context, int textViewResourceId, 
			ArrayList<? super Stock> items) 
	{
		super(context, textViewResourceId, items);
	}
	        
	@Override
	protected ViewHolder getViewHolder(View row)
	{
		ViewHolder viewHolder;
		
	    if (row == null) 
	    {
//	    	Log.i("StockAdapter", "Getting StockAlert view for " + o.getStock());	
	        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        row = inflater.inflate(this.getResourceId(), null);
	        
	        viewHolder = new ViewHolder();	        
            
			viewHolder.stockNameTxtView = (TextView) row.findViewById(R.id.name);
			viewHolder.tickerTxtView = (TextView) row.findViewById(R.id.ticker);
			viewHolder.lastQuoteTxtView = (TextView) row.findViewById(R.id.lastQuote);
			viewHolder.changeTxtView = (TextView) row.findViewById(R.id.lastChange);
			viewHolder.changePercentTxtView = (TextView) row.findViewById(R.id.ChangePercentage);
			viewHolder.stockNameTxtView = (TextView) row.findViewById(R.id.name);

			viewHolder.lowPriceTxtView = (TextView) row.findViewById(R.id.lowprice);
			viewHolder.highPriceTxtView = (TextView) row.findViewById(R.id.highprice);
			viewHolder.openPriceTxtView = (TextView) row.findViewById(R.id.openprice);
			viewHolder.previousClosePriceTxtView = (TextView) row.findViewById(R.id.previousprice);

			viewHolder.volumeTxtView = (TextView) row.findViewById(R.id.todayvolume);
			viewHolder.volume30TxtView = (TextView) row.findViewById(R.id.volume30); 
			
			viewHolder.breakOutPriceTxtView = (TextView) row.findViewById(R.id.BreakOut);
			viewHolder.breakDistanceTxtView = (TextView) row.findViewById(R.id.BreakDistance);
			
	        row.setTag(viewHolder);
	      }
		
	    viewHolder = (ViewHolder) row.getTag();
	    return viewHolder;
	}
    
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View row = convertView;
		
		Alert o = (Alert) items.get(position);
		
		ViewHolder viewHolder;
		
	    if (row == null) 
	    {
//	    	Log.i("StockAdapter", "Getting StockAlert view for " + o.getStock());	
	        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        row = inflater.inflate(this.getResourceId(), null);
	        
	        viewHolder = new ViewHolder();	        
            
			viewHolder.stockNameTxtView = (TextView) row.findViewById(R.id.name);
			viewHolder.tickerTxtView = (TextView) row.findViewById(R.id.ticker);
			viewHolder.lastQuoteTxtView = (TextView) row.findViewById(R.id.lastQuote);
			viewHolder.changeTxtView = (TextView) row.findViewById(R.id.lastChange);
			viewHolder.changePercentTxtView = (TextView) row.findViewById(R.id.ChangePercentage);
			viewHolder.stockNameTxtView = (TextView) row.findViewById(R.id.name);

			viewHolder.lowPriceTxtView = (TextView) row.findViewById(R.id.lowprice);
			viewHolder.highPriceTxtView = (TextView) row.findViewById(R.id.highprice);
			viewHolder.openPriceTxtView = (TextView) row.findViewById(R.id.openprice);
			viewHolder.previousClosePriceTxtView = (TextView) row.findViewById(R.id.previousprice);

			viewHolder.volumeTxtView = (TextView) row.findViewById(R.id.todayvolume);
			viewHolder.volume30TxtView = (TextView) row.findViewById(R.id.volume30); 
			
			viewHolder.breakOutPriceTxtView = (TextView) row.findViewById(R.id.BreakOut);
			viewHolder.breakDistanceTxtView = (TextView) row.findViewById(R.id.BreakDistance);
			
	        row.setTag(viewHolder);
	      }
		
	    viewHolder = (ViewHolder) row.getTag();
		
		this.setJSONObject(o.getTicker());
		
		try
		{
			String stockname = this.localJSONObject.getString(Constants.JSON_NAME_KEY);
			o.setName(stockname);
		}		
		catch (JSONException je) 
		{
			Log.e(this.myName, "Failed to obtain name for " + o.getTicker());
		}		


		boolean m_stockHasBrokenOut = false;

		double currentPrice = 0;

		try 
		{			
			currentPrice = Double.parseDouble(localJSONObject.getString(Constants.JSON_PRICE_KEY));
			m_stockHasBrokenOut = o.hasBroken(currentPrice);
		
			viewHolder.breakOutPriceTxtView.setText("BO: ");
			viewHolder.breakOutPriceTxtView.append(Double.toString(o.getBreakout()));

			SpannableString disanceSpannableText;
			double dist = ((currentPrice - o.getBreakout()) / o.getBreakout()) * 100;

			StringBuffer sb = new StringBuffer();
			sb.append("Dist: ");
			sb.append(decimalFormat.format(dist));
			sb.append('%');
			disanceSpannableText = new SpannableString(sb.toString());

			if (dist < 0) 
			{
				disanceSpannableText.setSpan(new ForegroundColorSpan(
											Color.RED), 0, disanceSpannableText.length(), 0);
				disanceSpannableText.setSpan(new StyleSpan(
											android.graphics.Typeface.BOLD), 0,
											disanceSpannableText.length(),
											Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} 
			else 
			{
				disanceSpannableText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0,
												disanceSpannableText.length(), 0);
				disanceSpannableText.setSpan(new StyleSpan(
												android.graphics.Typeface.ITALIC), 0,
												disanceSpannableText.length(),
												Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				disanceSpannableText.setSpan(new StyleSpan(
												android.graphics.Typeface.BOLD), 0,
												disanceSpannableText.length(),
												Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}

			viewHolder.breakDistanceTxtView.setText(disanceSpannableText, BufferType.SPANNABLE);

			String stockname = localJSONObject.getString(Constants.JSON_NAME_KEY);
			SpannableString stockNametext;

			if (m_stockHasBrokenOut) 
			{
				stockNametext = new SpannableString(stockname);

				stockNametext.setSpan(new ForegroundColorSpan(Color.GREEN), 0, stockNametext.length(), 0);
				stockNametext.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 
												stockNametext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				stockNametext.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 
												stockNametext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			} 
			else 
			{
				stockNametext = new SpannableString(stockname);
				stockNametext.setSpan(new StrikethroughSpan(), 0, stockNametext.length(), 0);
			}

			viewHolder.stockNameTxtView.setText(stockNametext, BufferType.SPANNABLE);

		} 
		catch (JSONException je) 
		{
			Log.e(this.myName, "Failed to obtain current quote for " + o.getTicker());
		}

		
		super.getView(position, row, parent);
		
		return row;
	}
}
