package com.benson.stockalert;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.benson.stockalert.prefs.AlertPreferences;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Network;
import com.benson.stockalert.utility.Stock;
import com.benson.stockalert.utility.StockDataSource;

public class Alerts extends ListActivity
{

    private final String            myName                 = this.getClass().getSimpleName();

    private StockDataSource         datasource             = null;
    private static ProgressDialog   m_ProgressDialog;
    private static ArrayList<Stock> m_stocks               = null;
    private static StockAdapter     m_adapter;

    private Stock                   m_menuSelectedStock    = null;

    private int                     position;
    private ListView                lv;
    View                            header;

    private static Handler          handler;
    private Thread                  progressThread;

    private static boolean          m_progressDialogActive = false;

    private static boolean          hasNetworkConnection   = false;

    Format                          formatter              = new SimpleDateFormat(
                                                               "E, MMM dd, yyyy HH:mm:ss");
    
    static final int 				STATIC_ACTIVITY_ADDSTOCK_RESULT = 2; //positive > 0 integer.    

    @Override
    public void onCreate(Bundle savedInstanceState)
    {

        // Debug.startMethodTracing("trace");
        super.onCreate(savedInstanceState);

        hasNetworkConnection = new Network(this).isOnline();
        setContentView(R.layout.alerts);

        lv = getListView();
        LayoutInflater inflater = getLayoutInflater();
        header = inflater.inflate(R.layout.header, null);
        lv.addHeaderView(header, null, false);

        this.m_adapter = new StockAdapter(this, R.layout.row, new ArrayList<Stock>());
        this.getStocks();
        this.sortStocks();

        if (!hasNetworkConnection)
        {
            // NO NETWORK OR WIFI CONNECTION. DON'T PROCEED!
            Toast mytoast;
            mytoast = Toast.makeText(this, Constants.NO_NETWORK_CONNECTION, Toast.LENGTH_LONG);
            mytoast.show();
        }
        else
        {

            // Create a handler to update the UI
            handler = new Handler();

            this.setupProgress(); // start the progress dialog

            registerForContextMenu(lv);
            registerListItemClicked();
            setListAdapter(this.m_adapter);

            TextView updated = (TextView) header.findViewById(R.id.lastUpdate);

            Date date = new Date();
            updated.setText("Updated: "
                + formatter.format(date));
        }
    }

    // Save the thread
    @Override
    public Object onRetainNonConfigurationInstance()
    {
        return progressThread;
    }

    // dismiss dialog if activity is destroyed
    @Override
    protected void onDestroy()
    {
        if (m_ProgressDialog != null
            && m_ProgressDialog.isShowing())
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

    private void setupProgress()
    {
        m_ProgressDialog = new ProgressDialog(Alerts.this);
        m_ProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        m_ProgressDialog.setMessage("Loading alerts...");
        m_ProgressDialog.setCancelable(true);
        m_ProgressDialog.show();

        this.m_progressDialogActive = true;

        progressThread = new MyThread();
        progressThread.start();

    }

    static public class MyRunnable implements Runnable
    {
        public void run()
        {
        	m_adapter.setNotifyOnChange(false);
        	m_adapter.setUpdateInProgress(true);
        	
            if (m_adapter != null)
            {
                if (m_adapter.getCount() == 0)
                {
                    for (Stock stock : m_stocks)
                    {
                        m_adapter.add(stock);
                    }
                }                
            }

            m_adapter.setUpdateInProgress(false);
            m_adapter.notifyDataSetChanged();
            
            m_ProgressDialog.dismiss();
            m_progressDialogActive = false;
        }
    }

    // public void run() {
    //
    // if (m_stocks == null || m_stocks.size() == 0)
    // {
    // handler.sendEmptyMessage(0);
    // }
    // else
    // {
    // int populated = 0;
    // while (this.m_adapter.m_dataPullComplete == 0)
    // {
    // if (populated == 0 )
    // {
    // for (Stock stock : m_stocks ) {
    // m_adapter.add(stock);
    // }
    //
    // try {
    // Thread.sleep( 100 );
    // }
    // catch (InterruptedException e) {
    // Log.e("ERROR", "Thread Interrupted");
    // }
    // m_adapter.notifyDataSetChanged();
    // }
    // populated = 1;
    //
    //
    // }
    // handler.sendEmptyMessage(0);
    // }
    // }

    @SuppressWarnings("unchecked")
    private void sortStocks()
    {
        Collections.sort(this.m_stocks, new Comparator()
        {

            public int compare(Object o1, Object o2)
            {
                Stock p1 = (Stock) o1;
                Stock p2 = (Stock) o2;
                return p1.getStock().compareToIgnoreCase(p2.getStock());
            }
        });
    }

    private void registerListItemClicked()
    {
        // TODO Auto-generated method stub
        lv.setOnItemLongClickListener(new OnItemLongClickListener()
        {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
            {
                position = arg2 -1;
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo)
    {

        this.m_menuSelectedStock = this.m_adapter.getItem(position);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alerts_context, menu);
        menu.setHeaderTitle(this.m_menuSelectedStock.getStock());

        super.onCreateContextMenu(menu, v, menuInfo);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        return (applyMenuChoice(item) || super.onContextItemSelected(item));
    }

    private boolean applyMenuChoice(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.DeleteStock:

                this.m_menuSelectedStock = this.m_adapter.getItem(position);

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Delete ticker "
                    + this.m_menuSelectedStock.getStock() + "?").setCancelable(false)
                    .setTitle("Confirm Delete").setIcon(R.drawable.delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            Toast
                                .makeText(
                                    getApplicationContext(),
                                    "Stock "
                                        + Alerts.this.m_menuSelectedStock.getStock() + ", ID "
                                        + Alerts.this.m_menuSelectedStock.getId()
                                        + " has been deleted", Toast.LENGTH_LONG).show();
                            try
                            {
                                Alerts.this.datasource.open();
                                Alerts.this.datasource.deleteStock(Alerts.this.m_menuSelectedStock);
                            }
                            finally
                            {
                                Alerts.this.datasource.close();
                            }
                            refresh();
                        }
                    }).setNegativeButton("No", new DialogInterface.OnClickListener()
                    {
                        public void onClick(DialogInterface dialog, int id)
                        {
                            dialog.cancel();
                        }
                    });
                AlertDialog alert = builder.create();
                alert.show();

                return (true);
            case R.id.EditStock:
                this.m_menuSelectedStock = this.m_adapter.getItem(position);
                Intent i = new Intent(this, EditStock.class);
                i.putExtra(this.getString(R.string.StockKey), this.m_menuSelectedStock);
                startActivity(i);
                return (true);
            case R.id.ViewChart:
                this.m_menuSelectedStock = this.m_adapter.getItem(position);
                Intent c = new Intent(this, Chart.class);
                c.putExtra(this.getString(R.string.StockKey), this.m_menuSelectedStock);
                startActivity(c);
                return (true);
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.alert_menu, menu);
        return true;
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

          if (requestCode == STATIC_ACTIVITY_ADDSTOCK_RESULT) //check if the request code is the one I sent
          {
                 if (resultCode == Activity.RESULT_OK) 
                 {
                     //now export the stocks being tracked to the stock backup file
                     new DatabaseCSVTask(this).execute(Constants.CSV_EXPORT);
                     Log.i("Stock Alert export","Export complete");
                 }
          }


        super.onActivityResult(requestCode, resultCode, data);
    }    

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.add_ticker:
            	Intent addstock = new Intent(this, AddStock.class);
            	startActivityForResult(addstock, STATIC_ACTIVITY_ADDSTOCK_RESULT);
                //startActivity(new Intent(this, AddStock.class));
                

                return true;
            case R.id.refresh_alerts:
                refresh();
                return true;
            case R.id.preferences:
                Intent intent = new Intent(this, AlertPreferences.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.export_alerts:
                new DatabaseCSVTask(this).execute(Constants.CSV_EXPORT);
                return true;
            case R.id.import_alerts:
                new DatabaseCSVTask(this).execute(Constants.CSV_LOAD);
                refresh();
                return true;
            case R.id.clear_stocks:
                Alerts.this.datasource.open();
                Alerts.this.datasource.clearStocks();
                Toast.makeText(Alerts.this, "Stock clear complete!", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    // @Override
    // protected void onResume() {
    // Log.i(myName, "RESUMING");
    // if (!new Network(this).isOnline()) {
    // Log.i(this.myName, "No network connection for alert notification....");
    // }
    // else {
    //
    // boolean changes = false;
    // int cnt = 0;
    //
    // cnt ++;
    //
    // //check if the number of tickers on the screen matches the database
    // int currentAdapterLength = 0;
    // if (this.m_adapter != null) {
    // currentAdapterLength = this.m_adapter.getCount();
    // }
    //
    // Log.i(myName, "this.getNumberOfTrackedStocks() = "
    // +this.getNumberOfTrackedStocks());
    // Log.i(myName, "currentAdapterLength =" +currentAdapterLength);
    // if (this.getNumberOfTrackedStocks() > currentAdapterLength ) {
    // //a new ticker was added to our database
    // changes = true;
    // }
    //
    //
    // cnt ++;
    //
    // //check if there is a menu selected context stock
    // if ( this.m_menuSelectedStock != null ) {
    // StockDataSource data = new StockDataSource(this);
    // try {
    // data.open();
    // Stock databasestock = data.getStock(this.m_menuSelectedStock.getId());
    //
    // if ( databasestock.getBreakout() !=
    // this.m_menuSelectedStock.getBreakout()) {
    // this.m_menuSelectedStock = null;
    // changes = true;
    // }
    // }
    // finally {
    // data.close();
    // }
    // }
    //
    // cnt ++;
    //
    // if (changes && !this.m_progressDialogActive) {
    // //we have a change, so refresh the screen
    // refresh();
    // }
    // }
    //
    // super.onResume();
    // }

    private void refresh()
    {
        Log.i(myName, "REFRESHING");

        if (!new Network(this).isOnline())
        {
            Toast mytoast;
            mytoast = Toast.makeText(this, Constants.NO_NETWORK_CONNECTION, Toast.LENGTH_LONG);
            mytoast.show();
        }
        else
        {
            this.getStocks();
            this.sortStocks();

            this.setupProgress(); // start the progress dialog

            m_adapter.setNotifyOnChange(false);
            m_adapter.setUpdateInProgress(true);
            m_adapter.clear(); // clear the list adapter


            if (m_stocks != null
                && m_stocks.size() > 0)
            {
                for (Stock stock : m_stocks)
                {
                    m_adapter.add(stock);
                }
            }
            
            m_adapter.localJSONObject = null;
            m_adapter.localJSONArray = null;
            
            m_adapter.setUpdateInProgress(false);
            m_adapter.notifyDataSetChanged();


            if (header == null)
            {
                LayoutInflater inflater = getLayoutInflater();
                header = inflater.inflate(R.layout.header, null);
            }
            TextView updated = (TextView) header.findViewById(R.id.lastUpdate);

            Date date = new Date();
            updated.setText("Updated: "
                + formatter.format(date));
        }
    }

    private int getNumberOfTrackedStocks()
    {
        StockDataSource data = new StockDataSource(this);
        int size = 0;
        try
        {
            data.open();
            size = data.getAllStocks().size();
        }
        finally
        {
            data.close();
        }
        return size;
    }

    private void getStocks()
    {
        try
        {
            datasource = new StockDataSource(this);
            datasource.open();
            this.m_stocks = datasource.getAllStocks();
        }
        catch (Exception e)
        {
            Log.e(myName, "GetStocks failed");
        }
        finally
        {
            if (datasource != null)
            {
                datasource.close();
            }
        }
        // runOnUiThread(returnRes);
    }
    




    static public class MyThread extends Thread
    {
        @Override
        public void run()
        {
            try
            {
                // // Simulate a slow network
                // try {
                // new Thread().sleep(2000);
                // } catch (InterruptedException e) {
                // e.printStackTrace();
                // }
                handler.post(new MyRunnable());
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
}
