package com.benson.stockalert;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.benson.stockalert.utility.Stock;
import com.benson.stockalert.utility.StockDataSource;

public class EditStock extends Activity{

	public final String myName = this.getClass().getSimpleName();
	
	
	private Stock m_paramStock;

	
	private TextView m_ticker;
	private EditText m_breakout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);        
        setContentView(R.layout.edit_stock);
        
        m_paramStock = (Stock)getIntent().getExtras().getSerializable("stock");
        
        m_ticker = (TextView)findViewById(R.id.edit_stock_symbol);
        m_ticker.setText( m_ticker.getText() + m_paramStock.getStock());
       
        m_breakout = (EditText)findViewById(R.id.edit_breakout);
        m_breakout.setText( m_paramStock.getBreakout() +"" );
               
    }
    
	public void onClick( View view ) {	
		switch (view.getId()) {
			case R.id.edit_stock_directly_button:
				StockDataSource datasource = new StockDataSource(this);
				
				try {					
					datasource.open();
					
					//Log.d(myName, datasource.getAllStocks() +"" );					
					
					datasource.updateStock( this.m_paramStock.getId(),
											Double.parseDouble( this.m_breakout.getText().toString() )
											);
					
					datasource.updateStockAlert( this.m_paramStock.getId(), 0 );					
				}
				finally {
					if (datasource != null){
						datasource.close();
					}
				}
						
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
