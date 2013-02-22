package com.benson.stockalert;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.LayoutInflater.Factory;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.benson.stockalert.dao.FinanceDataSource;
import com.benson.stockalert.model.Stock;
import com.benson.stockalert.prefs.AlertPreferences;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Network;
import com.benson.stockalert.view.FinanceAdapter;

abstract public class StockActivity extends ListActivity
{
	protected final String myName = this.getClass().getSimpleName();
	
	protected int					LAYOUT_ID = 0;
	
	protected int 					MENU_ID = 0;
	protected int 					CONTEXT_MENU_ID = 0;
	protected int					FILTER_MENU_ID = R.menu.filter;	

	protected FinanceDataSource datasource = null;
	
	protected ProgressDialog m_ProgressDialog;
	protected List<?> m_stocks = null;
	
	protected FinanceAdapter m_adapter;

	protected Stock m_menuSelectedStock = null;

	protected int position;
	protected ListView listView;
	View header;
	
	private Handler handler;


	private boolean hasNetworkConnection = false;

	Format formatter = new SimpleDateFormat("E, MMM dd, yyyy HH:mm:ss");

	
	abstract protected int getLayoutId();
	abstract protected int getAdapterLayoutId();  
	abstract protected int getMenuId();
	abstract protected int getContextMenuId();  
	abstract protected boolean applyMenuChoice(MenuItem item);
	abstract protected void setup();
	abstract protected void getData(); 

	
	@Override
	public void onCreate(Bundle savedInstanceState) 
	{

		// Debug.startMethodTracing("trace");
		super.onCreate(savedInstanceState);

		hasNetworkConnection = new Network(this).isOnline();		

		if (!hasNetworkConnection) 
		{
			// NO NETWORK OR WIFI CONNECTION. DON'T PROCEED!
			Toast mytoast;
			mytoast = Toast.makeText(this, Constants.NO_NETWORK_CONNECTION,
					Toast.LENGTH_LONG);
			mytoast.show();
			this.finish();
		} 
		else 
		{
			this.setupLayout();
		}
	}  
	
	protected void setupLayout()
	{
		setContentView(this.getLayoutId());
		
		listView = getListView();
		LayoutInflater inflater = getLayoutInflater();
		header = inflater.inflate(R.layout.header, null);
		listView.addHeaderView(header, null, false);
		
        registerForContextMenu(listView);
        registerListItemClicked();
        
        this.setup();
        
        this.updateGuiScreen();        
	}
	
	protected int getFilterMenuId()
	{
		return this.FILTER_MENU_ID;
	}	
	
    protected void updateGuiScreen()
    {    			
		handler = new Handler();

        startGuiUpdate();				
    }	

    protected void sort()
    {
        Collections.sort(this.m_stocks, new Comparator<Object>()
        {
            public int compare(Object o1, Object o2)
            {
            	Stock p1 = (Stock) o1;
            	Stock p2 = (Stock) o2;
                return p1.getTicker().compareToIgnoreCase(p2.getTicker());
            }
        });
    }
    
	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(this.getFilterMenuId(), menu);
		inflater.inflate(this.getMenuId(), menu);
		return true;
	}

    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//    MenuInflater inflater = getMenuInflater();
//    inflater.inflate(this.getMenuId(), menu);
//
//    getLayoutInflater().setFactory(new Factory() {
//    @Override
//    public View onCreateView(String name, Context context,
//    AttributeSet attrs) {
//    	Log.i("BLAH", name);
//
//    if (name.equalsIgnoreCase("com.android.internal.view.menu.IconMenuItemView")) {
//    try {
//    LayoutInflater f = getLayoutInflater();
//    final View view = f.createView(name, null, attrs);
//
//    new Handler().post(new Runnable() {
//    public void run() {
//
//    // set the background drawable
//    //view.setBackgroundResource(R.drawable.my_ac_menu_background);
//
//    // set the text color
//    ((TextView) view).setTextColor(Color.BLUE);
//    }
//    });
//    return view;
//    } catch (InflateException e) {
//    } catch (ClassNotFoundException e) {
//    }
//    }
//    return null;
//    }
//    });
//    return super.onCreateOptionsMenu(menu);
//    }
       
    
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) 
		{
			case R.id.filtergainers:
				this.filterRecords(Constants.FILTER_GAINERS_ONLY);
				return true;
			case R.id.filterlosers:
				this.filterRecords(Constants.FILTER_LOSERS_ONLY);
				return true;		
			case R.id.filterall:
				this.filterRecords(Constants.FILTER_SHOW_ALL);
				return true;
			case R.id.filter5:
				this.filterRecords(Constants.FILTER_LESS_5);
				return true;
			case R.id.filter10:
				this.filterRecords(Constants.FILTER_LESS_10);
				return true;
			case R.id.filter20:
				this.filterRecords(Constants.FILTER_LESS_20);
				return true;	
			case R.id.filter50:
				this.filterRecords(Constants.FILTER_GREATER_20);
				return true;				
			case R.id.preferences:
				Intent prefs = new Intent(this, AlertPreferences.class);
				startActivityForResult(prefs, 0);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}	
	

	// dismiss dialog if activity is destroyed
	@Override
	protected void onDestroy() 
	{
		if (m_ProgressDialog != null && m_ProgressDialog.isShowing()) 
		{
			m_ProgressDialog.dismiss();
			m_ProgressDialog = null;
		}
		super.onDestroy();
	}

	public void onPostCreate(Bundle savedInstanceState) 
	{
		super.onPostCreate(savedInstanceState);
	}

	protected void setupProgress() 
	{
		m_ProgressDialog = new ProgressDialog(this);
		m_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		m_ProgressDialog.setMessage("Loading stock data ...");
		m_ProgressDialog.setCancelable(true);
		m_ProgressDialog.show();
	}
	
	private String getUpdatedText()
	{
		Date date = new Date();
		String updatedText = "Updated: " + formatter.format(date);
		return updatedText;
	}

    protected void filterRecords(int filterType)
    {
    	this.m_adapter.filterRecords(filterType);
    	
    	
   		TextView updated = (TextView) header.findViewById(R.id.lastUpdate);

   		String value = this.getUpdatedText();
		if (filterType != Constants.FILTER_SHOW_ALL)
		{
			value = "(filtered) " + value;
		}
		updated.setText(value);     	
    }

    private void startGuiUpdate() 
    {
        // display the progressbar on the screen
    	this.setupProgress();
    	
    	//get our data from the DB
		this.getData();
		this.sort();

        // start the data retrieval task in a new thread
        Thread myThread = new Thread() 
        {
            public void run () 
            {
        		m_adapter.setDataList(m_stocks);
        		m_adapter.setStockString();
        		
        		//get the JSON objects
        		m_adapter.populateJsonObjects();
        		

                // this will handle the post task. 
                // it will run when the time consuming task gets finished
                handler.post(new Runnable() 
                {
                    @SuppressWarnings("unchecked")
					@Override
                    public void run() 
                    {
                        // Update your UI or 
                        // do any Post job after the time consuming task
                        // remember to dismiss the progress dialog here. 
                		m_adapter.setNotifyOnChange(false);
                		
                		m_adapter.clear();

            			if (m_adapter.getCount() == 0) 
            			{
            				for (Object stock : m_stocks) 
            				{
//            					Log.i("The thread", st.getStock());
            					m_adapter.add(stock);
            				}
            			}
                		
                		m_adapter.setOriginalItems();
                		
                		m_adapter.notifyDataSetChanged();
                		
                		m_ProgressDialog.dismiss();
                		
                		TextView updated = (TextView) header.findViewById(R.id.lastUpdate);

                		updated.setText(StockActivity.this.getUpdatedText());                		
                    }
                });
            }
        };

        myThread.start();
    }    


	protected void registerListItemClicked() {
		listView.setOnItemLongClickListener(new OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				position = arg2 - 1;
				return false;
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) 
	{
		this.m_menuSelectedStock = (Stock) this.m_adapter.getItem(position);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(this.getContextMenuId(), menu);
		menu.setHeaderTitle(this.m_menuSelectedStock.getName());

		super.onCreateContextMenu(menu, v, menuInfo);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		return (applyMenuChoice(item) || super.onContextItemSelected(item));
	}


    @Override
    public void onResume ()
    {    
    	super.onResume();
    	//refresh();    	
    }

	protected void refresh() 
	{
		Log.i(myName, "Refreshing " + this.myName + " View");

        if (!new Network(this).isOnline())
        {
            Toast mytoast;
            mytoast = Toast.makeText(this, Constants.NO_NETWORK_CONNECTION, Toast.LENGTH_LONG);
            mytoast.show();
        }
        else
        {
        	this.updateGuiScreen();            
        }
	}


//	private class MyThread extends Thread {
//		@Override
//		public void run() 
//		{
//			try 
//			{
//				// // Simulate a slow network
//				// try {
//				// new Thread().sleep(2000);
//				// } catch (InterruptedException e) {
//				// e.printStackTrace();
//				// }
//				handler.post(new MyRunnable());
//			} 
//			catch (Exception e) 
//			{
//				e.printStackTrace();
//			}
//		}
//	}
}
