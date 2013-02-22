package com.benson.stockalert;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.benson.stockalert.dao.AlertDataSource;
import com.benson.stockalert.dao.FinanceDataSource;
import com.benson.stockalert.dao.QuoteDataSource;
import com.benson.stockalert.dialogs.AddStock;
import com.benson.stockalert.dialogs.EditStock;
import com.benson.stockalert.dialogs.Quote;
import com.benson.stockalert.model.Alert;
import com.benson.stockalert.model.QuoteRequest;
import com.benson.stockalert.model.Stock;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.view.StockAlertAdapter;

public class Alerts extends StockActivity 
{
	protected final String 			myName = this.getClass().getSimpleName();
	
	protected static final int		LAYOUT_ID = R.layout.alerts;
	protected static final int		ADAPTER_LAYOUT_ID = R.layout.stockalertrow;
	
	
	protected static final int		MENU_ID = R.menu.alert_menu;
	protected static final int		CONTEXT_MENU_ID = R.menu.alerts_context;
	
	static final int 				STATIC_ACTIVITY_ADDSTOCK_RESULT = 2; // positive > 0 integer.
	static final int 				STATIC_ACTIVITY_QUOTE_RESULT = 3; // positive > 0 integer.

	
	protected void setup()
	{
		this.datasource = new AlertDataSource(this);
		
        this.m_adapter = new StockAlertAdapter(this, this.getAdapterLayoutId(), 
				new ArrayList<Stock>());
		
		setListAdapter(this.m_adapter);
    }		
	

	protected int getLayoutId()
	{
		return Alerts.LAYOUT_ID;
	}	

	protected int getAdapterLayoutId()
	{
		return Alerts.ADAPTER_LAYOUT_ID;
	}
	
	protected int getMenuId()
	{
		return Alerts.MENU_ID;
	}
	
	protected int getContextMenuId()
	{
		return Alerts.CONTEXT_MENU_ID;
	}

   @Override
    public boolean onPrepareOptionsMenu(Menu menu) 
    {    	
    	if (m_adapter.m_stockList.size() <= 0)
    	{
    		MenuItem item = menu.findItem(R.id.refresh_alerts);
    		item.setVisible(false); 
    		
    		item = menu.findItem(R.id.filtermenu);
    		item.setVisible(false);
    	}
    	else
    	{
    		MenuItem item = menu.findItem(R.id.refresh_alerts);
    		item.setVisible(true);  		
    		
    		item = menu.findItem(R.id.filtermenu);
    		//item.setVisible(true);    		
    	}
    	
    	return super.onPrepareOptionsMenu(menu);
    } 
   

	protected boolean applyMenuChoice(MenuItem item) 
	{
		switch (item.getItemId()) {
		case R.id.DeleteStock:

			this.m_menuSelectedStock = (Alert) this.m_adapter.getItem(position);

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Delete ticker " + this.m_menuSelectedStock.getTicker()
							+ "?")
					.setCancelable(false)
					.setTitle("Confirm Delete")
					.setIcon(R.drawable.delete)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									Toast.makeText(
											getApplicationContext(),
											"Stock "
													+ Alerts.this.m_menuSelectedStock
															.getTicker()
													+ ", ID "
													+ Alerts.this.m_menuSelectedStock
															.getId()
													+ " has been deleted",
											Toast.LENGTH_LONG).show();

									((AlertDataSource) Alerts.this.datasource).deleteStock((Alert) Alerts.this.m_menuSelectedStock);
									
									refresh();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			AlertDialog alert = builder.create();
			alert.show();

			return (true);
		case R.id.EditStock:
			this.m_menuSelectedStock = (Alert) this.m_adapter.getItem(position);
			Intent i = new Intent(this, EditStock.class);
			i.putExtra(this.getString(R.string.StockKey), this.m_menuSelectedStock);
			startActivity(i);
			return (true);
		case R.id.ViewChart:
			this.m_menuSelectedStock = (Alert) this.m_adapter.getItem(position);
			Intent c = new Intent(this, Chart.class);
			c.putExtra(this.getString(R.string.StockKey), this.m_menuSelectedStock);
			startActivity(c);
			return (true);
		}
		return false;
	}



	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) 
	{

		if (requestCode == STATIC_ACTIVITY_ADDSTOCK_RESULT) // check if the request code is the one I sent
		{
			if (resultCode == Activity.RESULT_OK) {
				// now export the stocks being tracked to the stock backup file				
				new DatabaseCSVTask(this).execute(Constants.CSV_EXPORT);

				this.refresh();
				Log.i("Stock Alert export", "Export complete");
			}
		}

		if (requestCode == STATIC_ACTIVITY_QUOTE_RESULT) // check if the request code is the one I sent
		{
			if (resultCode == Activity.RESULT_OK) {
				String extra = data
						.getStringExtra(Constants.INTER_ACTIVITY_QUOTE_TAG);
				Log.i(this.myName, "Quote dialog data:  " + extra);

				FinanceDataSource quoteDatasource = new QuoteDataSource(this);
				QuoteRequest rq = new QuoteRequest(extra, this.getString(R.string.InvalidWebSites));
//                Log.i(myName, rq.getQuote().toString());
				((QuoteDataSource) quoteDatasource).createQuote(rq, Constants.QUOTE_NOT_EXECUTED);

				FinanceTab ft = (FinanceTab) this.getParent();
				ft.getTab().setCurrentTab(1);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		//Log.i(this.myName, "Item selected: " + item.getItemId());
		switch (item.getItemId()) 
		{						
			case R.id.quote_stock:
				Intent quote = new Intent(this, Quote.class);
				startActivityForResult(quote, STATIC_ACTIVITY_QUOTE_RESULT);
				return true;
	
			case R.id.add_ticker:
				Intent addstock = new Intent(this, AddStock.class);
				startActivityForResult(addstock, STATIC_ACTIVITY_ADDSTOCK_RESULT);
				// startActivity(new Intent(this, AddStock.class));
				return true;
			case R.id.refresh_alerts:
				refresh();
				return true;

			case R.id.export_alerts:
				new DatabaseCSVTask(this).execute(Constants.CSV_EXPORT);
				return true;
			case R.id.import_alerts:
				new DatabaseCSVTask(this).execute(Constants.CSV_LOAD);
				refresh();
				return true;
			case R.id.clear_stocks:
				((AlertDataSource) this.datasource).clearStocks();
				Toast.makeText(Alerts.this, "Stock clear complete!",
						Toast.LENGTH_SHORT).show();
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

    
	protected void getData() 
	{
		try 
		{
			this.m_stocks = ((AlertDataSource) datasource).getAllStocks();
		} 
		catch (Exception e) 
		{
			Log.e(myName, "Data fetch failed");
		} 
		// runOnUiThread(returnRes);
	}
}
