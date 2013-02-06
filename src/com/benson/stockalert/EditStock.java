package com.benson.stockalert;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Stock;
import com.benson.stockalert.utility.StockDataSource;

public class EditStock extends Activity{

	public final String myName = this.getClass().getSimpleName();
	
	
	private Stock m_paramStock;

	
	private TextView m_ticker;
	private EditText m_breakout;
	
	private CheckBox m_alerted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);        
        setContentView(R.layout.edit_stock);
        
        m_paramStock = (Stock)getIntent().getExtras().getSerializable(this.getString(R.string.StockKey));
        
        Stock stock = new StockDataSource(this).getStock(m_paramStock.getId());
        
        m_ticker = (TextView)findViewById(R.id.edit_stock_symbol);
        m_ticker.setText( m_ticker.getText() + m_paramStock.getStock());
       
        m_breakout = (EditText)findViewById(R.id.edit_breakout);
        m_breakout.setText( stock.getBreakout() +"" );
        
        
        m_alerted = (CheckBox)findViewById(R.id.check_alerted);
        boolean checked = false;
        if (stock.getAlerted() == Constants.STOCK_ALERTED) {
        	checked = true;
        }
        m_alerted.setChecked(checked);              
    }
    
	public void onClick( View view ) {	
		switch (view.getId()) {
			case R.id.edit_stock_directly_button:
				StockDataSource datasource = new StockDataSource(this);
				
				try {					
					datasource.open();
					
					int checked = Constants.STOCK_NOT_ALERTED;
					if (this.m_alerted.isChecked()) {
						checked = Constants.STOCK_ALERTED;
					}
					
					datasource.updateStock( this.m_paramStock.getId(),
											Double.parseDouble( this.m_breakout.getText().toString() ),
											checked
											);					
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
