package com.benson.stockalert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.BufferType;
import android.widget.Toast;

import com.benson.stockalert.prefs.AlertPreferences;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Network;
import com.benson.stockalert.utility.Stock;
import com.benson.stockalert.utility.StockDataSource;
import com.benson.stockalert.utility.StockEntryConverter;
import com.benson.stockalert.utility.StockEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;

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

        this.m_adapter = new StockAdapter(this, R.layout.row, new ArrayList<Stock>(),
            new ArrayList());
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

            // View header = getLayoutInflater().inflate(R.layout.header, null);

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
            if (m_adapter != null)
            {
                if (m_adapter.getCount() == 0)
                {
                    for (Stock stock : m_stocks)
                    {
                        m_adapter.add(stock);
                    }
                }
                m_adapter.notifyDataSetChanged();
            }

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
                position = arg2;
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
                i.putExtra(this.getString(R.string.EditStockKey), this.m_menuSelectedStock);
                startActivity(i);
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
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
        switch (item.getItemId())
        {
            case R.id.add_ticker:
                startActivity(new Intent(this, AddStock.class));
                return true;
            case R.id.refresh_alerts:
                refresh();
                return true;
            case R.id.preferences:
                Intent intent = new Intent(this, AlertPreferences.class);
                startActivityForResult(intent, 0);
                return true;
            case R.id.export_alerts:
                new DatabaseCSVTask().execute(Constants.CSV_EXPORT);
                return true;
            case R.id.import_alerts:
                new DatabaseCSVTask().execute(Constants.CSV_LOAD);
                refresh();
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

            m_adapter.clear(); // clear the list adapter

            // m_adapter.notifyDataSetChanged();
            m_adapter.localJSONArray = null;

            if (m_stocks != null
                && m_stocks.size() > 0)
            {
                for (Stock stock : m_stocks)
                {
                    m_adapter.add(stock);
                }
            }
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
            if (this.m_adapter == null)
            {
                this.m_adapter = new StockAdapter(this, R.layout.row, new ArrayList<Stock>(),
                    new ArrayList());
            }

            this.m_adapter.m_arrayList.clear();

            datasource = new StockDataSource(this);
            datasource.open();
            this.m_stocks = datasource.getAllStocks();

            if (this.m_stocks != null
                && this.m_stocks.size() > 0)
            {
                for (Stock stock : this.m_stocks)
                {
                    this.m_adapter.m_arrayList.add("\""
                        + stock.getStock() + "\"");
                }
            }
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

    private class DatabaseCSVTask extends AsyncTask<String, Void, Boolean>
    {
        private final String         myName = this.getClass().getSimpleName();

        private final ProgressDialog dialog = new ProgressDialog(Alerts.this);

        private String               actionType;

        @Override
        protected void onPreExecute()
        {
            this.dialog.setMessage("Performing database action...");

            this.dialog.show();
        }

        protected Boolean doInBackground(final String... args)
        {
            this.actionType = args[0];

            if (actionType == Constants.CSV_EXPORT)
            {
                return this.export();
            }
            else
            {
                return this.load();
            }
        }

        private boolean load()
        {
            StockDataSource datasource = null;

            try
            {
                InputStream file = new FileInputStream(new File(Constants.exportDir,
                    Constants.STOCK_CSV_NAME));
                Reader csvFile = new InputStreamReader(file);

                CSVReader<Stock> stockReader = new CSVReaderBuilder<Stock>(csvFile).entryParser(
                    new StockEntryParser()).build();
                List<Stock> stocks = stockReader.readAll();

                datasource = new StockDataSource(getApplicationContext());
                datasource.open();

                datasource.clearStocks();

                for (Stock mystock : stocks)
                {
                    datasource.createStock(mystock.getStock(), mystock.getExchange(),
                        mystock.getBreakout());
                }

                file.close();
                stockReader.close();
                csvFile.close();

                return true;
            }
            catch (SQLException sqlEx)
            {
                Log.e("StockAlert load", sqlEx.getMessage(), sqlEx);

                return false;

            }
            catch (IOException e)
            {
                Log.e("StockAlert load", e.getMessage(), e);

                return false;
            }
            finally
            {
                if (datasource != null)
                {
                    datasource.close();
                }
            }
        }

        private boolean export()
        {

            File exportDir = new File(Constants.exportDir);

            if (!exportDir.exists())
            {
                exportDir.mkdirs();
            }

            File file = new File(exportDir, Constants.STOCK_CSV_NAME);
            Log.i(this.myName, file.getAbsolutePath());

            StockDataSource datasource = null;

            try

            {
                datasource = new StockDataSource(getApplicationContext());
                datasource.open();
                ArrayList<Stock> m_stocks = datasource.getAllStocks();

                List<Stock> stocks = new ArrayList<Stock>();
                for (Stock mystock : m_stocks)
                {
                    stocks.add(new Stock(mystock.getStock(), mystock.getExchange(), mystock
                        .getBreakout()));
                }

                FileWriter fwriter = new FileWriter(file);
                CSVWriter<Stock> csvWriter = new CSVWriterBuilder<Stock>(fwriter).entryConverter(
                    new StockEntryConverter()).build();
                csvWriter.writeAll(stocks);

                csvWriter.close();
                return true;

            }
            catch (SQLException sqlEx)

            {
                Log.e("StockAlert export", sqlEx.getMessage(), sqlEx);

                return false;

            }
            catch (IOException e)
            {
                Log.e("StockAlert export", e.getMessage(), e);

                return false;

            }
            finally
            {
                if (datasource != null)
                {
                    datasource.close();
                }
            }

        }

        protected void onPostExecute(final Boolean success)
        {

            if (this.dialog.isShowing())
            {

                this.dialog.dismiss();

            }

            if (success)
            {
                Toast.makeText(Alerts.this, "Action successful!", Toast.LENGTH_SHORT).show();
            }
            else
            {
                Toast.makeText(Alerts.this, "Action failed", Toast.LENGTH_SHORT).show();
            }

        }

    }

    private class StockAdapter extends ArrayAdapter<Stock>
    {

        private HashMap          m_stockMap         = new HashMap();
        private ArrayList<Stock> items;
        private StockQuote       m_stockquote;
        private JSONArray        localJSONArray     = null;
        private JSONObject       localJSONObject    = null;

        private String           myName;
        private String           m_stockString;

        private ArrayList        m_arrayList;

        private int              m_dataPullComplete = 0;

        DecimalFormat            decimalFormat      = new DecimalFormat("#.##");

        public StockAdapter(Context context, int textViewResourceId, ArrayList<Stock> items,
            ArrayList list)
        {

            super(context, textViewResourceId, items);

            this.myName = this.getContext().getClass().getSimpleName();

            this.m_stockquote = new StockQuote(context);
            this.items = items;
            this.m_arrayList = list;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater inflater = getLayoutInflater();

                v = inflater.inflate(R.layout.row, null);
            }

            Stock o = items.get(position);

            if (o != null)
            {
                TextView ticker = (TextView) v.findViewById(R.id.ticker);
                TextView lastQuote = (TextView) v.findViewById(R.id.lastQuote);
                TextView change = (TextView) v.findViewById(R.id.lastChange);
                TextView changeperc = (TextView) v.findViewById(R.id.ChangePercentage);
                TextView breakOut = (TextView) v.findViewById(R.id.BreakOut);
                TextView stockName = (TextView) v.findViewById(R.id.name);
                TextView breakDistance = (TextView) v.findViewById(R.id.BreakDistance);

                try
                {
                    localJSONObject = null;

                    this.m_stockString = StringUtils.join(this.m_arrayList, ',');


                    if (this.m_arrayList.size() == 1)
                    {
                        localJSONObject = this.m_stockquote.getJsonStockObject(this.m_stockString);
                    }
                    else
                    {
                        if (localJSONArray == null)
                        {
                            if (m_stockString.length() == 0)
                            {
                                localJSONArray = this.m_stockquote.getJsonStockArray(o.getStock());
                            }
                            else
                            {
                                localJSONArray = this.m_stockquote.getJsonStockArray(m_stockString);
                            }
                            this.m_stockquote.m_stockCalls++;
                        }

                        if (localJSONArray != null)
                        {
                            if (m_stockMap.size() == 0)
                            {
                                for (int i = 0; i < localJSONArray.length(); ++i)
                                {
                                    localJSONObject = localJSONArray.getJSONObject(i);

                                    m_stockMap.put(
                                        localJSONObject.getString(Constants.JSON_TICKER_KEY),
                                        localJSONObject);
                                }
                            }
                        }

                        if (m_stockMap.containsKey(o.getStock()))
                        {
                            localJSONObject = (JSONObject) m_stockMap.get(o.getStock());
                        }
                        else
                        {
                            localJSONArray = this.m_stockquote.getJsonStockArray(o.getStock());
                            localJSONObject = localJSONArray.getJSONObject(0);
                            this.m_stockquote.m_stockCalls++;
                        }
                    }

                }
                catch (NullPointerException e)
                {
                    Log.e(this.myName, "Failed to obtain JSONObject for "
                        + o.getStock(), e);
                }
                catch (JSONException e)
                {
                    Log.e(this.myName, "Failed to obtain JSONObject for "
                        + o.getStock(), e);
                }

                boolean m_stockBrokeout = false;

                double currentPrice = 10000;
                try
                {
                    currentPrice = Double.parseDouble(localJSONObject
                        .getString(Constants.JSON_PRICE_KEY));
                    m_stockBrokeout = o.hasBroken(currentPrice);
                }
                catch (JSONException je)
                {
                    Log.e(this.myName, "Failed to obtain stock information for "
                        + o.getStock());
                }

                ticker.setText(o.getStock());

                try
                {
                    lastQuote.setText(localJSONObject.getString(Constants.JSON_PRICE_KEY));
                }
                catch (JSONException je)
                {
                    Log.e(this.myName, "Failed to obtain current quote for "
                        + o.getStock());
                }

                try
                {
                    change.setText(localJSONObject.getString(Constants.JSON_CHANGE_KEY));
                }
                catch (JSONException je)
                {
                    Log.e(this.myName, "Failed to obtain last change for "
                        + o.getStock());
                }

                try
                {
                    SpannableString text;

                    String stockChange = localJSONObject
                        .getString(Constants.JSON_CHANGE_PERCENT_KEY);
                    stockChange = stockChange.substring(0, stockChange.indexOf("%") - 1);
                    double percChange = Double.parseDouble(stockChange);

                    if (percChange < 0)
                    {
                        stockChange = "("
                            + percChange + ")";
                    }
                    else
                    {
                        stockChange = percChange
                            + "";
                    }

                    text = new SpannableString(stockChange
                        + "%");

                    if (percChange < 0)
                    {
                        text.setSpan(new ForegroundColorSpan(Color.RED), 0, text.length(), 0);
                    }
                    else
                    {
                        text.setSpan(new ForegroundColorSpan(Color.BLACK), 0, text.length(), 0);
                        text.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                            text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                            text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }

                    changeperc.setText(text, BufferType.SPANNABLE);

                }
                catch (JSONException je)
                {
                    Log.e(this.myName, "Failed to obtain last change % for "
                        + o.getStock());
                }


                SpannableString disText;
                double dist = currentPrice
                    - o.getBreakout();

                disText = new SpannableString(decimalFormat.format(dist));

                if (dist < 0)
                {
                    disText.setSpan(new ForegroundColorSpan(Color.RED), 0, disText.length(), 0);
                }
                else
                {
                    disText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, disText.length(), 0);
                    disText.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                        disText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    disText.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                        disText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }

                breakDistance.setText(disText, BufferType.SPANNABLE);


                breakOut.setText(decimalFormat.format(o.getBreakout()));

                try
                {
                    String name = localJSONObject.getString(Constants.JSON_NAME_KEY);

                    SpannableString text;

                    if (m_stockBrokeout)
                    {
                        name = name.toUpperCase();
                        text = new SpannableString(name);

                        text.setSpan(new ForegroundColorSpan(Color.GREEN), 0, text.length(), 0);
                        text.setSpan(new StyleSpan(android.graphics.Typeface.ITALIC), 0,
                            text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        text.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0,
                            text.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                        // create our span sections, and assign a format to
                        // each.
                        // str.setSpan(new
                        // StyleSpan(android.graphics.Typeface.ITALIC), 0, 7,
                        // Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // str.setSpan(new BackgroundColorSpan(0xFFFFFF00), 8,
                        // 19, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // str.setSpan(new
                        // StyleSpan(android.graphics.Typeface.BOLD), 21,
                        // str.length()- 1,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                    else
                    {
                        text = new SpannableString(name);
                        text.setSpan(new StrikethroughSpan(), 0, text.length(), 0);
                    }

                    stockName.setText(text, BufferType.SPANNABLE);
                }
                catch (JSONException je)
                {
                    Log.e(this.myName, "Failed to obtain ticket name for "
                        + o.getStock());
                }

            }
            this.m_dataPullComplete = 1;
            return v;
        }
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
