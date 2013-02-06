package com.benson.stockalert;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.benson.stockalert.utility.Network;
import com.benson.stockalert.utility.Stock;

public class Chart extends Activity{

	  WebView c;
	  private Stock m_paramStock;
	  
	  @Override
	  public void onCreate(Bundle savedInstanceState)
	  {

		// Debug.startMethodTracing("trace");
		super.onCreate(savedInstanceState);
		
		m_paramStock = (Stock)getIntent().getExtras().getSerializable(this.getString(R.string.StockKey));
	
	
		setContentView(R.layout.chart);
		
		this.c = ((WebView)findViewById(R.id.webview));
		
		this.c.loadUrl("http://ichart.finance.yahoo.com/z?s=" + m_paramStock.getStock() + "&t=4m&q=c&l=off&z=l&p=m20,m50,m200&a=r14,vm&f=l1");
	}

}
