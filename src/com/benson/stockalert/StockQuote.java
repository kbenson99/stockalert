package com.benson.stockalert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.benson.stockalert.utility.Constants;


public class StockQuote
{

    private final String myName       = this.getClass().getSimpleName();

    private Context      myContext;

    public int           m_stockCalls = 0;

    public StockQuote(Context context)
    {
        this.myContext = context;

    }


    public JSONObject getJsonStockObject(String stock)
    {

        JSONObject jsonObject = null;

        String m_stockUrl = this.getYqlUrl(stock);

        try
        {
            String jsonString = this.retrieveJsonString(m_stockUrl);

            Log.i(this.myName, jsonString);
            if (jsonString.length() > 0)
            {
                jsonObject = new JSONObject(jsonString).getJSONObject("query");

                int count = Integer.parseInt(jsonObject.getString("count"));

                jsonObject = jsonObject.getJSONObject("results").getJSONObject("quote");

                Log.i(this.myName, "Records returned for "
                    + stock + " = " + count + "");
            }
        }
        catch (Exception e)
        {
            Log.e(this.myName, "Error returned for getJsonStockObject: "
                + m_stockUrl, e);
        }
        return jsonObject;
    }


    public JSONArray getJsonStockArray(String stock)
    {

        JSONArray localJSONArray = null;

        String m_stockUrl = this.getYqlUrl(stock);
        try
        {
            String jsonString = this.retrieveJsonString(m_stockUrl);

            // Log.i(this.myName, jsonString);
            if (jsonString.length() > 0)
            {
                JSONObject jsonObject = new JSONObject(jsonString).getJSONObject("query");

                int count = Integer.parseInt(jsonObject.getString("count"));

                localJSONArray = jsonObject.getJSONObject("results").getJSONArray("quote");

                Log.i(this.myName, "Records returned for "
                    + stock + " = " + count + "");
            }
        }
        catch (Exception e)
        {
            Log.e(this.myName, "Error returned for getJsonStockArray: "
                + m_stockUrl, e);
        }
        return localJSONArray;
    }


    private String getYqlUrl(String stock)
    {
        String m_stock = stock.replaceAll("\"", "%22");
        
        if (!m_stock.startsWith("%22"))
        {
        	m_stock =  "%22" + m_stock;
        }
        if (!m_stock.endsWith("%22"))
        {
        	m_stock = m_stock + "%22";
        }
        
        Log.i(this.myName, "Stock = " + m_stock);
        
        String yqlString = "select+"
            + StringUtils.join(Constants.QueryFields(), ',')
            + "+from+yahoo.finance.quotes+where+symbol+in+(" + m_stock
            + ")&env=store://datatables.org/alltableswithkeys&format=json";

        String s = this.myContext.getString(R.string.QuoteUrl)
            + yqlString;
        Log.d(this.myName, s);
        return s;
    }

    public void incrementStockCalls()
    {
        this.m_stockCalls++;
    }

    public int getStockCalls()
    {
        return this.m_stockCalls;
    }


    private String retrieveJsonString(String url)
    {
        Log.i(this.myName, url);
        StringBuilder sb = new StringBuilder();

        try
        {
            BufferedReader reader = this.retrieveReader(url);

            boolean verbose = Boolean.parseBoolean(this.myContext.getString(R.bool.verbose));

            String line;
            // Read buffer Line By Line
            while ((line = reader.readLine()) != null)
            {
                if (verbose)
                    Log.d(this.myName, line
                        + "\n");
                sb.append(line
                    + "\n");
                // sb.append( "\n" );
            }
            reader.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return sb.toString();
    }


    private BufferedReader retrieveReader(String url)
    {

        HttpClient client = new DefaultHttpClient();
        InputStream source = null;

        try
        {
            // Log.i( this.myName, url );
            HttpGet method = new HttpGet(url);

            HttpResponse getResponse = client.execute(method);
            final int statusCode = getResponse.getStatusLine().getStatusCode();

            if (statusCode != HttpStatus.SC_OK)
            {
                Log.w(this.myName, "Error "
                    + statusCode + " for URL " + url);
                return null;
            }

            HttpEntity getResponseEntity = getResponse.getEntity();

            source = getResponseEntity.getContent();

            BufferedReader reader = new BufferedReader(new InputStreamReader(source, "UTF-8"), 1000);

            // CopyInputStream cis = new CopyInputStream(source);
            // BufferedReader verboseReader = new BufferedReader(new InputStreamReader(
            // cis.getCopy(), "UTF-8" ), 1000 ); //buffer size set to 1000

            return reader;
        }
        catch (IOException e)
        {
            Log.w(this.myName, "Error returned for URL: "
                + url, e);
        }
        return null;
    }
}
