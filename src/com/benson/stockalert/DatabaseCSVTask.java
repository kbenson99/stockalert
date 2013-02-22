package com.benson.stockalert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.SQLException;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.benson.stockalert.dao.AlertDataSource;
import com.benson.stockalert.dao.StockQuote;
import com.benson.stockalert.model.Alert;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.StockEntryConverter;
import com.benson.stockalert.utility.StockEntryParser;
import com.googlecode.jcsv.reader.CSVReader;
import com.googlecode.jcsv.reader.internal.CSVReaderBuilder;
import com.googlecode.jcsv.writer.CSVWriter;
import com.googlecode.jcsv.writer.internal.CSVWriterBuilder;

public class DatabaseCSVTask extends AsyncTask<String, Void, Boolean>
{
    private final String         myName = this.getClass().getSimpleName();

    private ProgressDialog 		dialog;

    private String              actionType;
    
    private Context				myContext;
    
    public DatabaseCSVTask(Context myContext)
    {
    	this.myContext = myContext;
    	dialog = new ProgressDialog(this.myContext);    	
    }

    @Override
    protected void onPreExecute()
    {
        this.dialog.setMessage("Performing database action...");

        this.dialog.show();
    }

    protected Boolean doInBackground(final String... args)
    {
        this.actionType = args[0];
        boolean loaded = false;

        if (actionType.equals(Constants.CSV_EXPORT))
        {
            return this.export();
        }
        else
        {
        	try
        	{
        		loaded = this.load();	
        	}
        	catch(JSONException e){}
            
        }
        return loaded;
    }

    private boolean load() throws JSONException
    {
        AlertDataSource datasource = null;

        try
        {
            InputStream file = new FileInputStream(new File(Constants.exportDir,
                Constants.STOCK_CSV_NAME));
            Reader csvFile = new InputStreamReader(file);

            CSVReader<Alert> stockReader = new CSVReaderBuilder<Alert>(csvFile).entryParser(
                new StockEntryParser()).build();
            List<Alert> stocks = stockReader.readAll();

            datasource = new AlertDataSource(this.myContext);

            datasource.clearStocks();

            StockQuote m_stockquote = new StockQuote(this.myContext);
            
            List<String> stockArray = new ArrayList<String>();
            
            for (Alert mystock : stocks)
            {
            	stockArray.add(mystock.getTicker());
            	
            }
            
            String m_stockString = StringUtils.join(stockArray, ',');
            JSONObject localJSONObject    = null;
            JSONArray  localJSONArray = m_stockquote.getJsonStockArray(m_stockString);


            Map<String, JSONObject> m_stockMap = new HashMap<String, JSONObject>();	
            for (int i = 0; i < localJSONArray.length(); ++i)
            {
            	try
            	{            		
	                localJSONObject = localJSONArray.getJSONObject(i);
	
	                m_stockMap.put(localJSONObject.getString(Constants.JSON_TICKER_KEY), localJSONObject);
            	}
                catch (JSONException je)
                {
                    Log.e(this.myName, "Failed to obtain stock information for "
                        + i);
                }                
            }
            
            for (Alert mystock : stocks)
            {
            	if (!m_stockMap.containsKey(mystock.getTicker()))
            	{
                	Toast.makeText(this.myContext, mystock.getTicker().toString() + " is not a valid stock symbol", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    datasource.createStock(mystock.getTicker(), mystock.getExchange(),
                            mystock.getBreakout(), mystock.getAlerted());                     	
                }           	
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

        AlertDataSource datasource = null;

        try

        {
            datasource = new AlertDataSource(this.myContext);

            List<Alert> m_stocks = datasource.getAllStocks();

            List<Alert> stocks = new ArrayList<Alert>();
            for (Alert mystock : m_stocks)
            {
                stocks.add(new Alert(mystock.getTicker(), mystock.getExchange(), mystock
                    .getBreakout(), mystock.getAlerted()));
            }

            FileWriter fwriter = new FileWriter(file);
            CSVWriter<Alert> csvWriter = new CSVWriterBuilder<Alert>(fwriter).entryConverter(
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
            Toast.makeText(this.myContext, "Action successful!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(this.myContext, "Action failed", Toast.LENGTH_SHORT).show();
        }
    }

}
