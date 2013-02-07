package com.benson.stockalert;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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

import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Stock;

public class StockAdapter extends ArrayAdapter<Stock>
{

    private  HashMap          m_stockMap         = new HashMap();
    public ArrayList<Stock> items;
    private StockQuote       m_stockquote;
    public JSONArray        localJSONArray     = null;
    public JSONObject       localJSONObject    = null;

    private String           myName;
    private String           m_stockString;

    DecimalFormat            decimalFormat      = new DecimalFormat("#.##");
    
    private boolean			 updateInProgress = false;

    public StockAdapter(Context context, int textViewResourceId, ArrayList<Stock> items)
    {

        super(context, textViewResourceId, items);

        this.myName = this.getContext().getClass().getSimpleName();

        this.m_stockquote = new StockQuote(context);
        this.items = items;
    }
    
    private List getStockList()
    {
    	List<String> mylist = new ArrayList<String>();
    	for (Stock mstock : this.items)
    	{
    		mylist.add( mstock.getStock() );
    	}
    	return mylist;
    }
    
    public void setUpdateInProgress(boolean value)
    {
    	this.updateInProgress = value;
    }


    
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        View v = convertView;
        if (v == null)
        {
        	LayoutInflater inflater = (LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            v = inflater.inflate(R.layout.row, null);
        }

        Stock o = items.get(position);
        
        if (o != null && this.updateInProgress == false)
        {
        	Log.i(this.myName, "StockAdapter updating view");
            TextView ticker = (TextView) v.findViewById(R.id.ticker);
            TextView lastQuote = (TextView) v.findViewById(R.id.lastQuote);
            TextView change = (TextView) v.findViewById(R.id.lastChange);
            TextView changeperc = (TextView) v.findViewById(R.id.ChangePercentage);
            TextView breakOut = (TextView) v.findViewById(R.id.BreakOut);
            TextView stockName = (TextView) v.findViewById(R.id.name);
            TextView breakDistance = (TextView) v.findViewById(R.id.BreakDistance);

//            TextView lowPrice = (TextView) v.findViewById(R.id.lo);
//            TextView highPrice = (TextView) v.findViewById(R.id.high);

            try
            {
                this.m_stockString = StringUtils.join(getStockList(), ',');


                if (getStockList().size() == 1)
                {
                	if (localJSONObject == null) 
                	{
	                    localJSONObject = this.m_stockquote.getJsonStockObject(this.m_stockString);
	                    this.m_stockquote.m_stockCalls++;
                	}
                }
                else
                {
                    if (localJSONArray == null)
                    {
                        if (m_stockString.length() == 0)
                        {
//                            localJSONArray = this.m_stockquote.getJsonStockArray(o.getStock());
                            localJSONObject = this.m_stockquote.getJsonStockObject(o.getStock());
                        }
                        else
                        {
                            localJSONArray = this.m_stockquote.getJsonStockArray(m_stockString);
                        }
                        this.m_stockquote.m_stockCalls++;
                    }

                    if (localJSONArray != null)
                    {
                        if (m_stockMap.size() == 0)
                        {
                            for (int i = 0; i < localJSONArray.length(); ++i)
                            {
                                localJSONObject = localJSONArray.getJSONObject(i);

                                m_stockMap.put(
                                    localJSONObject.getString(Constants.JSON_TICKER_KEY),
                                    localJSONObject);
                            }
                        }
                    }

                    if (m_stockMap.containsKey(o.getStock()))
                    {
                        localJSONObject = (JSONObject) m_stockMap.get(o.getStock());
                    }
                    else
                    {
//                        localJSONArray = this.m_stockquote.getJsonStockArray(o.getStock());
//                        localJSONObject = localJSONArray.getJSONObject(0);
                        localJSONObject = this.m_stockquote.getJsonStockObject(o.getStock());
                        this.m_stockquote.m_stockCalls++;
                    }
                }

            }
            catch (NullPointerException e)
            {
                Log.e(this.myName, "Failed to obtain JSONObject for "
                    + o.getStock(), e);
            }
            catch (JSONException e)
            {
                Log.e(this.myName, "Failed to obtain JSONObject for "
                    + o.getStock(), e);
            }

            boolean m_stockBrokeout = false;

            double currentPrice = 10000;
            try
            {
                currentPrice = Double.parseDouble(localJSONObject
                    .getString(Constants.JSON_PRICE_KEY));
                m_stockBrokeout = o.hasBroken(currentPrice);
            }
            catch (JSONException je)
            {
                Log.e(this.myName, "Failed to obtain stock information for "
                    + o.getStock());
            }

            ticker.setText(o.getStock());

            String direction = "";
            try
            {
                lastQuote.setText(direction + localJSONObject.getString(Constants.JSON_PRICE_KEY)); 
                if (localJSONObject.getString(Constants.JSON_CHANGE_SIGN_KEY).equals("d"))
                {
                	direction = "-";
                }
                
            	              
            }
            catch (JSONException je)
            {
                Log.e(this.myName, "Failed to obtain current quote for "
                    + o.getStock());
            }

            try
            {

            	String priceChange = localJSONObject.getString(Constants.JSON_CHANGE_KEY);
                change.setText(direction + localJSONObject.getString(Constants.JSON_CHANGE_KEY));
                
                
//                lowPrice.setText(localJSONObject.getString(Constants.JSON_LO_KEY));
//                highPrice.setText(localJSONObject.getString(Constants.JSON_HI_KEY));
            }
            catch (JSONException je)
            {
                Log.e(this.myName, "Failed to obtain last change for "
                    + o.getStock());
            }

            try
            {
                SpannableString text;

                String stockChange = localJSONObject
                    .getString(Constants.JSON_CHANGE_PERCENT_KEY);
                stockChange = stockChange.substring(0, stockChange.indexOf("%") - 1);
                double percChange = Double.parseDouble(stockChange);

                if (direction.equals("-"))
                {
                    stockChange = "("
                        + percChange + ")";
                }
                else
                {
                    stockChange = percChange
                        + "";
                }

                text = new SpannableString(stockChange
                    + "%");

                if (direction.equals("-"))
                {
                    text.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), 0);
                }
                else
                {
                    text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), 0);
                    text.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                        text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                        text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                changeperc.setText(text, BufferType.SPANNABLE);

            }
            catch (JSONException je)
            {
                Log.e(this.myName, "Failed to obtain last change % for "
                    + o.getStock());
            }


            SpannableString disText;
            double dist = ( (currentPrice - o.getBreakout() ) / o.getBreakout() ) *100;

            disText = new SpannableString( decimalFormat.format(dist) +"%");

            if (dist < 0)
            {
                disText.setSpan(new ForegroundColorSpan(Color.RED), 0, disText.length(), 0);
            }
            else
            {
                disText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, disText.length(), 0);
                disText.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                    disText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                disText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                    disText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }

            breakDistance.setText(disText, BufferType.SPANNABLE);


            breakOut.setText(decimalFormat.format(o.getBreakout()));

            try
            {
                String name = localJSONObject.getString(Constants.JSON_NAME_KEY);

                SpannableString text;

                if (m_stockBrokeout)
                {
                    text = new SpannableString(name);

                    text.setSpan(new ForegroundColorSpan(Color.GREEN), 0, text.length(), 0);
                    text.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                        text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                        text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                else
                {
                    text = new SpannableString(name);
                    text.setSpan(new StrikethroughSpan(), 0, text.length(), 0);
                }

                stockName.setText(text, BufferType.SPANNABLE);
            }
            catch (JSONException je)
            {
                Log.e(this.myName, "Failed to obtain ticket name for "
                    + o.getStock());
            }

        }
        
        Log.i(this.myName, "Stock calls:  " +this.m_stockquote.m_stockCalls);
        return v;
    }
}
