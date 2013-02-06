package com.benson.stockalert;

import java.security.KeyStore;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.benson.stockalert.utility.MySSLSocketFactory;


public class StockQuote
{

    private final String myName       = this.getClass().getSimpleName();

    private Context      myContext;

    public int           m_stockCalls = 0;
    
    String CONSUMER_KEY = "Trj8ITSCSwINw8DwHUr53GP14t2vtXkLprOd5IYc";
    String CONSUMER_SECRET = "5yiq28ALH5wozfgm0rvbLTOgrBdySTkX5ehvqvsh";
    String ACCESS_TOKEN = "vvZ16uJpD1Ci4OxnupB1BLwL5aWuiAFx8zqLM9NB";
    String ACCESS_TOKEN_SECRET = "4wSQgO5Hzj8lHDLPNpSbE6Av7xUs4ktHOyF3agSx";    

    public StockQuote(Context context)
    {
        this.myContext = context;

    }


    public JSONObject getJsonStockObject(String stock)
    {    	
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider      
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET);
    	consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);        

    	JSONObject jsonObject = null;
    	
        try
        {        	
        	 // create an HTTP request to a protected resource
            HttpGet request = new HttpGet(this.myContext.getString(R.string.QuoteUrl) + stock);

            // sign the request
            consumer.sign(request);

            // send the request
            HttpClient httpClient = getNewHttpClient();
            
            HttpResponse response = httpClient.execute(request);  
            int statusCode = response.getStatusLine().getStatusCode();

            Log.i(this.myName, statusCode + ":" + response.getStatusLine().getReasonPhrase());
            
            String jsonString = IOUtils.toString(response.getEntity().getContent());

//            Log.i(this.myName, jsonString);
            if (jsonString.length() > 0)
            {
                jsonObject = new JSONObject(jsonString).getJSONObject("response");

                //int count = Integer.parseInt(jsonObject.getString("count"));

                jsonObject = jsonObject.getJSONObject("quotes").getJSONObject("quote");

                Log.i(this.myName, jsonObject.getString("name"));
            }
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();        	
        }
        
        																																							
//        catch (Exception e)
//        {
//            Log.e(this.myName, "Error returned for getJsonStockObject: "
//                + m_stockUrl, e);
//        }
        return jsonObject;
    }


    public JSONArray getJsonStockArray(String stock) 
    {
        // create a consumer object and configure it with the access
        // token and token secret obtained from the service provider      
        OAuthConsumer consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY,
                CONSUMER_SECRET);
    	consumer.setTokenWithSecret(ACCESS_TOKEN, ACCESS_TOKEN_SECRET);        

    	JSONObject jsonObject = null;
    	
    	JSONArray localJSONArray = null;
    	
        try
        {        	
        	 // create an HTTP request to a protected resource
        	HttpGet request = new HttpGet(this.myContext.getString(R.string.QuoteUrl) + stock);

            // sign the request
            consumer.sign(request);

            // send the request
            HttpClient httpClient = getNewHttpClient();
            
            HttpResponse response = httpClient.execute(request);  
            int statusCode = response.getStatusLine().getStatusCode();
            
            Log.i(this.myName, statusCode + ":" + response.getStatusLine().getReasonPhrase());
            
            String jsonString = IOUtils.toString(response.getEntity().getContent());

//            Log.i(this.myName, jsonString);
            if (jsonString.length() > 0)
            {
                jsonObject = new JSONObject(jsonString).getJSONObject("response");

                //int count = Integer.parseInt(jsonObject.getString("count"));

                localJSONArray = jsonObject.getJSONObject("quotes").getJSONArray("quote");                
            }
        }
        catch(Exception ee)
        {
        	ee.printStackTrace();        	
        }
       
        return localJSONArray;
    }



    public void incrementStockCalls()
    {
        this.m_stockCalls++;
    }

    public int getStockCalls()
    {
        return this.m_stockCalls;
    }
    
    private HttpClient getNewHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);

            SSLSocketFactory sf = new MySSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));

            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);

            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }
}
