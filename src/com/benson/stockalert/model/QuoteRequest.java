package com.benson.stockalert.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import com.benson.stockalert.R;

import android.util.Log;


public class QuoteRequest implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2481668587816737401L;
	
	private final String myName = this.getClass().getSimpleName();	
	protected static final int		LAYOUT_ID = R.layout.alerts;
	
	
	/**
	 * We don't want to retrieve sub URL data pulls from a web site.
	 * If true, a web site parse is in progress and is not to query another web site.
	 */
	private boolean pulledFromUrl = false;
	
	private long id;
	private long requestId;
	private Set<String> quoteSet = new HashSet<String>();
	private List<String> quoteList = new ArrayList<String>();
	
	private List<String> invalidSites = new ArrayList<String>();
	
	private static final String delims = "[ ,]+";
	private static final Pattern goodPattern = Pattern.compile("[^a-zA-Z]");
	private static final String garbageToStrip = "'$,.\"/;:[]*&^%#@)(!-_=+|<>?";
	private static final String urlTag = "http://";
	
	public QuoteRequest(String quote, String invalidWebSites) 
	{
		this.setInvalidSites(invalidWebSites);		
		this.setQuote(quote);
	}
	
	public QuoteRequest(){};

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) 
	{
		this.requestId = requestId;
	}
	
	private void setInvalidSites(String sites)
	{
		for (String site : sites.split(QuoteRequest.delims))
		{
			this.invalidSites.add(site);
		}	
	}

	public boolean isUrlData() {
		return pulledFromUrl;
	}

	public void setIsUrlData(boolean pulledFromUrl) {
		this.pulledFromUrl = pulledFromUrl;
	}

	public List<String> getQuote() 
	{
		for (String value : this.quoteSet)
		{
			this.quoteList.add(value);
		}
		return quoteList;
	}
	
	public void addQuote(String value)
	{
		this.quoteSet.add(value);
	}
	
	
	private List<String> parseUrlData(String url_text) 
	{
		List<String> url_quotes = new ArrayList<String>();
		
		boolean validSite = true;
				
		for (String site : this.invalidSites)
		{
			if (url_text.startsWith(site))
			{
				validSite = false;
				break;
			}
		}

		if (validSite)
		{
			try
			{
	            HttpClient client = new DefaultHttpClient();
	            HttpGet request = new HttpGet(url_text);
	            
	            // Get the response
	            ResponseHandler<String> responseHandler = new BasicResponseHandler();
	            String response_str = client.execute(request, responseHandler);
	            QuoteRequest req = new QuoteRequest();
	            req.setIsUrlData(true);
	            	            
	            req.setQuote(response_str);
	            url_quotes = req.getQuote();
	            
	            //Log.i(this.myName, StringUtils.join(req.getQuote(), "," ));					
			}
			catch (IOException ioe)
			{
				ioe.printStackTrace();
			}			
		}

		return url_quotes;
	}
	
	public void setQuote(String data)
	{	
		String[] tokens = data.split(QuoteRequest.delims);

		for (String token : tokens)
		{		
			if (token.charAt(0) == '$' && data.indexOf("$") != -1)
			{
//				Log.i(this.myName, "1 - " + token);
				this.addToQuote(token.substring(1, token.length()));
			}			
			else if (data.indexOf("$") == -1 && !token.contains(QuoteRequest.urlTag) 
					&& data.indexOf(QuoteRequest.urlTag) == -1)
			{
//				Log.i(this.myName, "2 - " + token);
				this.addToQuote(token);
			}			
			else if (token.contains(QuoteRequest.urlTag) && !this.isUrlData())
			{			
				for (String value : this.parseUrlData(token) )
				{
					this.quoteSet.add(value);	
				}						
			}
		}		
	}
	
	private void addToQuote(String value)
	{
		value = StringUtils.strip(value, QuoteRequest.garbageToStrip);
		value = StringUtils.remove(value, "<br");
		boolean hasSpecialChar = QuoteRequest.goodPattern.matcher(value).find();
		if (!hasSpecialChar)
		{
			if (value.length() > 0)
			{
				this.quoteSet.add(value);		
			}			
		}			
	}
		
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return "ID = " + id + ", ticker = " + quoteList;
	}
}


