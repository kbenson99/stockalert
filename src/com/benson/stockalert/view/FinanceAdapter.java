package com.benson.stockalert.view;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.benson.stockalert.R;
import com.benson.stockalert.dao.StockQuote;
import com.benson.stockalert.model.Stock;
import com.benson.stockalert.utility.Constants;

public abstract class FinanceAdapter extends ArrayAdapter 
{	
	protected ArrayList<? super Stock> items;
	private ArrayList<? super Stock> orig_items;

	protected StockQuote m_stockquote;
	
	private JSONArray localJSONArray = null;
	protected JSONObject localJSONObject = null;
	
	private List<JSONObject> m_jsonObjects;
	
	private List<String> invalidTickers = new ArrayList<String>();

	protected String myName       = this.getClass().getSimpleName();
	
	protected String m_stockString;

	public List<String> m_stockList = new ArrayList<String>();

	protected DecimalFormat decimalFormat = new DecimalFormat("#.##");

	protected int resourceId;
	
	static class ViewHolder 
	{
		TextView tickerTxtView;
		TextView lastQuoteTxtView;
		TextView changeTxtView;
		TextView changePercentTxtView;
		TextView stockNameTxtView;
		
		TextView lowPriceTxtView;
		TextView highPriceTxtView;
		TextView openPriceTxtView;
		TextView previousClosePriceTxtView;
		
		TextView volumeTxtView;
		TextView volume30TxtView;	
		
		TextView breakOutPriceTxtView;
		TextView breakDistanceTxtView;
	}	

	@SuppressWarnings("unchecked")
	public FinanceAdapter(Context context, int textViewResourceId, ArrayList<? super Stock> items) 
	{
		super(context, textViewResourceId, items);
		
		this.m_stockquote = new StockQuote(context);
		this.m_jsonObjects = new ArrayList<JSONObject>();

		this.items = items;		
		this.setOriginalItems();
		
		this.resourceId = textViewResourceId;
	}	
	
    public void setDataList(List<?> m_quotes) 
	{
		m_stockList.clear();

		Stock mquote;
		for (Object e : m_quotes) 
		{
			mquote = (Stock) e;
			m_stockList.add(mquote.getTicker());
		}
	}    

    @SuppressWarnings("unchecked")
	public void setOriginalItems()
	{
		this.orig_items = (ArrayList<? super Stock>) this.items.clone();
	}
		
	protected int getResourceId()
	{
		return resourceId;
	}

	private void addInvalidTicker(String ticker)
	{
		if (!getInvalidTickers().contains(ticker))
		{
			getInvalidTickers().add(ticker);	
		}		
	}
	
	public List<String> getInvalidTickers()
	{
		return this.invalidTickers;
	}

	
	public List<String> getStockListSansInvalidTickers()
	{
		this.m_stockList.removeAll(getInvalidTickers());
		return this.m_stockList;
	}

	public int getStockCalls() 
	{
		return this.m_stockquote.getStockCalls();
	}

	public void setStockString() 
	{
		m_stockString = StringUtils.join(this.m_stockList, ',');
	}

	public String getStockString() {
		return m_stockString;
	}
	
		
	
	public void populateJsonObjects()
	{
		this.m_jsonObjects.clear();
		
		if (this.m_stockList.size() > 0)
		{			
			try {
				if (this.m_stockList.size() == 1) 
				{
					localJSONObject = this.m_stockquote.getJsonStockObject(getStockString());
					if (localJSONObject != null)
					{
						this.m_jsonObjects.add(localJSONObject);					
					}
					this.m_stockquote.incrementStockCalls();
				} 
				else 
				{
					this.localJSONArray = this.m_stockquote.getJsonStockArray(getStockString());
					this.m_stockquote.incrementStockCalls();						
	
					if (this.localJSONArray != null) 
					{
						JSONObject tempJsonObject = null;
							
						for (int i = 0; i < localJSONArray.length(); ++i) 
						{
							tempJsonObject = localJSONArray.getJSONObject(i);
							this.m_jsonObjects.add(tempJsonObject);
						}
					}
				}
			} 
			catch (NullPointerException e) 
			{
				Log.i(this.myName, "Failed to obtain JSONObject for " + getStockString(), e);
			} 
			catch (JSONException e) 
			{
				Log.i(this.myName, "Failed to obtain JSONObject for " + getStockString(), e);
			}	
		}
		
	}	
	
	protected void setJSONObject(String currentStock)
	{
		this.localJSONObject = null;
		
		try
		{
			for (JSONObject tempJsonObject : m_jsonObjects)
			{			
				if (currentStock.toLowerCase().equals(tempJsonObject.getString(Constants.JSON_TICKER_KEY).toLowerCase(Locale.getDefault()))) 
				{
					localJSONObject = tempJsonObject;
					break;
				}
			}
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
    @Override
    public int getCount() 
    {
        return this.items.size();
    }	
	

	protected ViewHolder getViewHolder(View row) 
	{
		ViewHolder viewHolder;
		
	    if (row == null) 
	    {
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
		
		Stock o = (Stock) items.get(position);
		String currentStock = o.getTicker();
		
//		Log.i(this.myName, "Setting generic stock elements for " + currentStock);	
			
		ViewHolder viewHolder = this.getViewHolder(row);
		
	    if (row == null) 
	    {
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
		
		this.setJSONObject(currentStock);			

		
		List<TextView> fields = new ArrayList<TextView>();
		fields.add(viewHolder.volume30TxtView);
		fields.add(viewHolder.volumeTxtView);
		fields.add(viewHolder.openPriceTxtView);
		fields.add(viewHolder.previousClosePriceTxtView);
		fields.add(viewHolder.highPriceTxtView);
		fields.add(viewHolder.lowPriceTxtView);
		fields.add(viewHolder.stockNameTxtView);
		fields.add(viewHolder.changePercentTxtView);
		fields.add(viewHolder.changeTxtView);
		fields.add(viewHolder.lastQuoteTxtView);
		
		for (TextView view : fields)
		{
			view.setText("");
		}		
		
		if (localJSONObject == null)
		{
			//keep track of our bad stock tickers
			this.addInvalidTicker(currentStock.toLowerCase());
			
			SpannableString stockNametext;

			stockNametext = new SpannableString(currentStock.toUpperCase());

			stockNametext.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0, 
					stockNametext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			stockNametext.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, 
					stockNametext.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);				
			stockNametext.setSpan(new ForegroundColorSpan(Color.RED), 0, stockNametext.length(), 0);

			stockNametext.setSpan(new StrikethroughSpan(), 0, stockNametext.length(), 0);
			
			viewHolder.tickerTxtView.setText(stockNametext, BufferType.SPANNABLE);
		}
		else 
		{
			int priceMoveDirection = 1;
			String priceMoveSign = "";

			viewHolder.tickerTxtView.setText(currentStock.toUpperCase());

			double currentPrice = 0;

			try 
			{
				currentPrice = Double.parseDouble(localJSONObject.getString(Constants.JSON_PRICE_KEY));

				if (localJSONObject.getString(Constants.JSON_CHANGE_SIGN_KEY).equals("d")) 
				{
					priceMoveDirection = -1;
					priceMoveSign = "-";
				}

				viewHolder.lastQuoteTxtView.setText("" + currentPrice);

				viewHolder.changeTxtView.setText(priceMoveSign	+ localJSONObject.getString(Constants.JSON_CHANGE_KEY));

				viewHolder.openPriceTxtView.setText("Op: " + localJSONObject.getString(Constants.JSON_OPEN_KEY));
				viewHolder.previousClosePriceTxtView.setText("Pv: " + localJSONObject.getString(Constants.JSON_PREVIOUS_CLOSE_KEY));
				viewHolder.highPriceTxtView.setText("Hi: " + localJSONObject.getString(Constants.JSON_HI_KEY));
				viewHolder.lowPriceTxtView.setText("Lo: " + localJSONObject.getString(Constants.JSON_LO_KEY));
				

				NumberFormat numberFormat = NumberFormat.getInstance();
				numberFormat.setGroupingUsed(true);
				int volume = Integer.parseInt(localJSONObject.getString(Constants.JSON_VOLUME_KEY));
				viewHolder.volumeTxtView.setText("Vol: " + numberFormat.format(volume));
				volume = Integer.parseInt(localJSONObject.getString(Constants.JSON_30VOLUME_KEY));
				viewHolder.volume30TxtView.setText("Vol30: " + numberFormat.format(volume));

				String stockChange = localJSONObject.getString(Constants.JSON_CHANGE_PERCENT_KEY);
				stockChange = stockChange.substring(0, stockChange.indexOf("%") - 1);
				double percChange = Double.parseDouble(stockChange);

				if (priceMoveDirection < 0) 
				{
					stockChange = "(" + percChange + ")";
				} 
				else 
				{
					stockChange = "" + percChange;
				}

				SpannableString spannableText = new SpannableString(stockChange	+ "%");

				if (priceMoveDirection < 0) 
				{
					spannableText.setSpan(new ForegroundColorSpan(Color.RED), 0, spannableText.length(), 0);
				} 
				else 
				{
					spannableText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableText.length(), 0);
					spannableText.setSpan(new StyleSpan( android.graphics.Typeface.ITALIC), 0, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
					spannableText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				}
				viewHolder.changePercentTxtView.setText(spannableText,BufferType.SPANNABLE);

				String stockname = localJSONObject.getString(Constants.JSON_NAME_KEY);
				SpannableString stockNametext = new SpannableString(stockname);			
				viewHolder.stockNameTxtView.setText(stockNametext, BufferType.SPANNABLE);				
			} 
			catch (JSONException je) 
			{
				Log.e(this.myName, "Failed to obtain current quote for " + currentStock);
			}
		}
		
		return row;
	}
	


   public void filterRecords(int filter)
    {
        // Create new empty list to add matched elements to
	   List<? super Stock> filtered = new ArrayList<Stock>();
        
        // examine each element to build filtered list
        // remember to always use your original items list
        for (Object s : this.orig_items)
        {
        	Stock test = (Stock) s;
        	
//	        	Log.i(this.myName, "Checking filter for stock:  " + test.getStock());
        	this.setJSONObject(test.getTicker());	        	
        	
        	if (this.localJSONObject == null)
        	{
        		//no Data from the API
        		continue;	        		
        	}
        	
        	if (filter > 1)
        	{
        		//user wants to filter on price
        		try
        		{
            		double price = localJSONObject.getDouble(Constants.JSON_PRICE_KEY);
            		switch (filter)
            		{
            			case Constants.FILTER_LESS_5:
            				if (price <= 5)
            				{
            					filtered.add(test);
            				}
            				break; 
            			case Constants.FILTER_LESS_10:     
            				if (price <= 10)
            				{
            					filtered.add(test);
            				}            				
            				break;
            			case Constants.FILTER_LESS_20:
            				if (price <= 20)
            				{
            					filtered.add(test);
            				}            				
        					break;
            			case Constants.FILTER_GREATER_20:
            				if (price > 20)
            				{
            					filtered.add(test);
            				}            				
        					break;
            			default:
            				break;
            		}            		
        		}
               	catch (JSONException pe)
            	{
            	}   
        	}
        	else
        	{
            	int priceMoveDirection = Constants.FILTER_GAINERS_ONLY;
            	try
            	{
    				if (localJSONObject.getString(Constants.JSON_CHANGE_SIGN_KEY).equals("d")) 
    				{
    					priceMoveDirection = Constants.FILTER_LOSERS_ONLY;
    				}
    				
    				if (localJSONObject.getString(Constants.JSON_CHANGE_SIGN_KEY).equals("u")) 
    				{
    					priceMoveDirection = Constants.FILTER_GAINERS_ONLY;
    				}    				
    				
    				switch(filter)
    				{
    					case Constants.FILTER_LOSERS_ONLY:
    						if (priceMoveDirection == Constants.FILTER_LOSERS_ONLY)
    						{
    							filtered.add(test);    							
    						}
    						break;
    					case Constants.FILTER_GAINERS_ONLY:
    						if (priceMoveDirection == Constants.FILTER_GAINERS_ONLY)
    						{
    							filtered.add(test);
    						}
    						break;
    					default:
    						filtered.add(test);
    						break;    				
    				}
            	}
            	catch (JSONException e)
            	{
            	}      		
        	}
        }
        
        //set new (filtered) list as the current list of items to be shown
        this.items.clear();
        for (Object e : filtered)
        {
        	Stock s = (Stock) e;
        	this.items.add(s);
        }
        
        //notify ListView to Rebuild
        notifyDataSetChanged();
    }	   

}
