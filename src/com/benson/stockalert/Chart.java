package com.benson.stockalert;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.benson.stockalert.model.Alert;
import com.benson.stockalert.model.Quote;

public class Chart extends Activity{

	  WebView view;
	  private String stocktoChart = null;
	  private Alert m_paramStock;
	  private Quote m_paramQuote;
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
		// Debug.startMethodTracing("trace");
		super.onCreate(savedInstanceState);
		
		if (getIntent().getExtras().containsKey(this.getString(R.string.StockKey)))
		{
			m_paramStock = (Alert) getIntent().getExtras().getSerializable(this.getString(R.string.StockKey));
			stocktoChart = m_paramStock.getTicker();
		}
		else
		{
			m_paramQuote = (Quote) getIntent().getExtras().getSerializable(this.getString(R.string.QuoteKey));
			stocktoChart = m_paramQuote.getTicker();
		}		
		
		setContentView(R.layout.chart);
		
		this.view = ((WebView)findViewById(R.id.webview));
		
		this.view.loadUrl("http://ichart.finance.yahoo.com/z?s=" + stocktoChart 
							+ "&t=6m&q=c&l=off&z=l&p=m20,m50,m200&a=r14,vm&f=l1,g,h");
	}

}
