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
    
    private List<String> 	m_stockList = new ArrayList<String>();

    DecimalFormat            decimalFormat      = new DecimalFormat("#.##");
    
    public StockAdapter(Context context, int textViewResourceId, ArrayList<Stock> items)
    {
        super(context, textViewResourceId, items);

        this.myName = this.getContext().getClass().getSimpleName();

        this.m_stockquote = new StockQuote(context);
        this.items = items;
    }
    
    public void setStockList(ArrayList<Stock> m_stocks)
    {
    	m_stockList.clear();
    	for (Stock mstock : m_stocks)
    	{
    		m_stockList.add( mstock.getStock() );
    	}
    }
    
    public List getStockList()
    {
    	return m_stockList;
    }
    
    public int getStockCalls()
    {
    	return this.m_stockquote.getStockCalls();
    }
    
    public void setStockString()
    {
    	m_stockString = StringUtils.join(getStockList(), ',');
    }
    
    public String getStockString()
    {
    	return m_stockString;
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
        
        
        if (o != null)
        {
            Log.i(this.myName, "Getting view for " + o.getStock());        	
            TextView tickerTxtView = (TextView) v.findViewById(R.id.ticker);
            TextView lastQuoteTxtView = (TextView) v.findViewById(R.id.lastQuote);
            TextView changeTxtView = (TextView) v.findViewById(R.id.lastChange);
            TextView changePercentTxtView = (TextView) v.findViewById(R.id.ChangePercentage);
            TextView breakOutPriceTxtView = (TextView) v.findViewById(R.id.BreakOut);
            TextView stockNameTxtView = (TextView) v.findViewById(R.id.name);
            TextView breakDistanceTxtView = (TextView) v.findViewById(R.id.BreakDistance);

//            TextView lowPrice = (TextView) v.findViewById(R.id.lo);
//            TextView highPrice = (TextView) v.findViewById(R.id.high);

            try
            {

                if (getStockList().size() == 1)
                {
                	if (localJSONObject == null) 
                	{
	                    localJSONObject = this.m_stockquote.getJsonStockObject(getStockString());
	                    this.m_stockquote.incrementStockCalls();
                	}
                }
                else
                {
                    if (localJSONArray == null)
                    {
                    	localJSONArray = this.m_stockquote.getJsonStockArray(getStockString());
                        this.m_stockquote.incrementStockCalls();
                    }

                    if (localJSONArray != null)
                    {
                    	JSONObject tempJsonObject = null;
                        if (m_stockMap.size() == 0)
                        {
                            for (int i = 0; i < localJSONArray.length(); ++i)
                            {
                            	tempJsonObject = localJSONArray.getJSONObject(i);
                                
                                if (o.getStock().equals(tempJsonObject.getString(Constants.JSON_TICKER_KEY)))
                                {
                                	localJSONObject = tempJsonObject;
                                }

                                m_stockMap.put(tempJsonObject.getString(Constants.JSON_TICKER_KEY),
                                				tempJsonObject);
                            }
                        }
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
            
            int priceMoveDirection = 1;
            String priceMoveSign = "";
            boolean m_stockHasBrokenOut = false;
           
            tickerTxtView.setText(o.getStock());

            double currentPrice = 0;
            
            try
            {
                currentPrice = Double.parseDouble(localJSONObject.getString(Constants.JSON_PRICE_KEY));
                m_stockHasBrokenOut = o.hasBroken(currentPrice);
                
                if (localJSONObject.getString(Constants.JSON_CHANGE_SIGN_KEY).equals("d"))
                {
                	priceMoveDirection = -1;
                	priceMoveSign = "-";
                }
                
                lastQuoteTxtView.setText("" + currentPrice);   
                
                changeTxtView.setText(priceMoveSign + localJSONObject.getString(Constants.JSON_CHANGE_KEY));
                
//              lowPrice.setText(localJSONObject.getString(Constants.JSON_LO_KEY));
//              highPrice.setText(localJSONObject.getString(Constants.JSON_HI_KEY));   
                

                String stockChange = localJSONObject.getString(Constants.JSON_CHANGE_PERCENT_KEY);
                stockChange = stockChange.substring(0, stockChange.indexOf("%") - 1);
                double percChange = Double.parseDouble(stockChange);

                if (priceMoveDirection < 0)
                {
                    stockChange = "(" + percChange + ")";
                }
                else
                {
                    stockChange =  "" + percChange;
                }

                SpannableString spannableText = new SpannableString(stockChange + "%");

                if (priceMoveDirection < 0)
                {
                	spannableText.setSpan(new ForegroundColorSpan(Color.RED), 0,spannableText.length(), 0);
                }
                else
                {
                	spannableText.setSpan(new ForegroundColorSpan(Color.BLACK), 0, spannableText.length(), 0);
                	spannableText.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                							spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                	spannableText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                							spannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                changePercentTxtView.setText(spannableText, BufferType.SPANNABLE);  
                
                
                SpannableString disanceSpannableText;
                double dist = ( (currentPrice - o.getBreakout() ) / o.getBreakout() ) *100;

                disanceSpannableText = new SpannableString(decimalFormat.format(dist) +"%");

                if (dist < 0)
                {
                    disanceSpannableText.setSpan(new ForegroundColorSpan(Color.RED), 0, disanceSpannableText.length(), 0);
                }
                else
                {
                    disanceSpannableText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, disanceSpannableText.length(), 0);
                    disanceSpannableText.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                    							disanceSpannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    disanceSpannableText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                    							disanceSpannableText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
                breakDistanceTxtView.setText(disanceSpannableText, BufferType.SPANNABLE);
  
                
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

                stockNameTxtView.setText(stockNametext, BufferType.SPANNABLE);
                
            }
            catch (JSONException je)
            {
                Log.e(this.myName, "Failed to obtain current quote for " + o.getStock());
            }
        }        
        return v;
    }
}
