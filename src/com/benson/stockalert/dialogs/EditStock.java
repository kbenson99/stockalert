package com.benson.stockalert.dialogs;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import com.benson.stockalert.R;
import com.benson.stockalert.dao.AlertDataSource;
import com.benson.stockalert.model.Alert;
import com.benson.stockalert.utility.Constants;

public class EditStock extends Activity{

	public final String myName = this.getClass().getSimpleName();
	
	
	private Alert m_paramStock;

	
	private TextView m_ticker;
	private EditText m_breakout;
	
	private CheckBox m_alerted;

    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	super.onCreate(savedInstanceState);        
        setContentView(R.layout.edit_stock);
        
        m_paramStock = (Alert)getIntent().getExtras().getSerializable(this.getString(R.string.StockKey));
        
        Alert stock = new AlertDataSource(this).getStock(m_paramStock.getId());
        
        m_ticker = (TextView)findViewById(R.id.edit_stock_symbol);
        m_ticker.setText( m_ticker.getText() + m_paramStock.getTicker());
       
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
				AlertDataSource datasource = new AlertDataSource(this);
				
				try 
				{					
					int checked = Constants.STOCK_NOT_ALERTED;
					if (this.m_alerted.isChecked()) 
					{
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
