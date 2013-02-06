package com.benson.stockalert;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.StockDataSource;

public class AddStock extends Activity
{

    private final String myName = this.getClass().getSimpleName();

    private EditText     m_ticker;
    private EditText     m_breakout;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_stock);

        m_ticker = (EditText) findViewById(R.id.add_stock_symbol_directly);
        m_breakout = (EditText) findViewById(R.id.breakout);

    }

    public void onClick(View view)
    {
        StockDataSource datasource = null;


        switch (view.getId())
        {
            case R.id.add_stock_directly_button:
                try
                {
                    StockQuote m_stockquote = new StockQuote(this);
                    datasource = new StockDataSource(this);
                    datasource.open();

//                    Log.d(myName, datasource.getAllStocks()
//                        + "");

                    try
                    {

                        JSONObject jsonObject = m_stockquote.getJsonStockObject(this.m_ticker.getText().toString());

                        if (jsonObject.getString(Constants.JSON_EXCHANGE_KEY).equals("null"))
                        {
                        	Toast.makeText(AddStock.this, this.m_ticker.getText().toString() + " is not a valid stock symbol", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {	                        
	                        datasource.createStock(this.m_ticker.getText().toString(),
	                            jsonObject.getString(Constants.JSON_EXCHANGE_KEY),
	                            Double.parseDouble(this.m_breakout.getText().toString()), 
	                            Constants.STOCK_NOT_ALERTED);
	                        Log.i(this.myName, "Stock ticker "
	                            + this.m_ticker.getText().toString() + " added to database");
                        }
                    }
                    catch (JSONException je)
                    {
                        Log.e(this.myName, "Failed to obtain stock information for "
                            + this.m_ticker.getText().toString());
                    }

                }
                finally
                {
                    if (datasource != null)
                    {
                        datasource.close();
                    }
                }

                Intent i = getIntent(); //get the intent that has been called, i.e you did called with startActivityForResult();
                setResult(Activity.RESULT_OK, i);  //now you can use Activity.RESULT_OK, its irrelevant whats the resultCode    
                                
                this.finish();
                break;
            case R.id.cancel_stock_directly_button:
            {
                this.finish();
                break;
            }
        }
    }
}
