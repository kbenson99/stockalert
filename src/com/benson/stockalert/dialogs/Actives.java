package com.benson.stockalert.dialogs;

import java.util.Calendar;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.benson.stockalert.R;
import com.benson.stockalert.R.id;
import com.benson.stockalert.R.layout;
import com.benson.stockalert.dao.ActiveDataSource;
import com.benson.stockalert.dao.StockQuote;
import com.benson.stockalert.utility.Constants;

public class Actives extends Activity
{
    private final String myName = this.getClass().getSimpleName();
    
    private ActiveDataSource datasource;
    
    private EditText	m_ticker;
    private Spinner		m_broker;
    private EditText	m_quantity;
    private DatePicker	m_date;
    private EditText	m_price;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_active);

        m_ticker = (EditText) findViewById(R.id.activeTicker);
        m_price = (EditText) findViewById(R.id.activePrice);
        m_broker = (Spinner) findViewById(R.id.spinnerBroker);
        m_quantity = (EditText) findViewById(R.id.activeQuantity);
        m_date = (DatePicker) findViewById(R.id.activeDate);
        
        this.initializeDatePicker();
    }
    
    private void initializeDatePicker()
    {
		final Calendar c = Calendar.getInstance();
		int year = c.get(Calendar.YEAR);
		int month = c.get(Calendar.MONTH);
		int day = c.get(Calendar.DAY_OF_MONTH);
		
		// set current date into datepicker
		m_date.init(year, month, day, null);       	
    }

    public void onClick(View view)
    {

        switch (view.getId())
        {
            case R.id.add_stock_directly_button:
                try
                {
                    StockQuote m_stockquote = new StockQuote(this);
//                    Log.d(myName, datasource.getAllStocks()
//                        + "");
                    try
                    {
                    	
                        JSONObject jsonObject = m_stockquote.getJsonStockObject(this.m_ticker.getText().toString());

                        if (jsonObject.getString(Constants.JSON_EXCHANGE_KEY).equals("null"))
                        {
                        	Toast.makeText(Actives.this, this.m_ticker.getText().toString() + " is not a valid stock symbol", Toast.LENGTH_SHORT).show();
                        }
                        else
                        {
                        	StringBuilder sb=new StringBuilder();
                        	sb.append(this.m_date.getMonth() +1);
                        	sb.append('-');
                        	sb.append(this.m_date.getDayOfMonth());
                        	sb.append('-');
                        	sb.append(this.m_date.getYear());                        	                 	
                        	
                        	datasource = new ActiveDataSource(this);
                        	datasource.createActive(this.m_ticker.getText().toString(), 
                        						Integer.parseInt(this.m_quantity.getText().toString()),
                        						Double.parseDouble(this.m_price.getText().toString()),
                        						this.m_broker.getSelectedItem().toString(),
                        						sb.toString() 
                        						);
                        	
	                        Log.i(this.myName, "Active ticker "
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

//                Intent i = getIntent(); //get the intent that has been called, i.e you did called with startActivityForResult();
//                setResult(Activity.RESULT_OK, i);  //now you can use Activity.RESULT_OK, its irrelevant whats the resultCode    
//                                
//                this.finish();
//                break;
            case R.id.cancel_stock_directly_button:
            {
                //this.finish();
                break;
            }
        }
    }
}
