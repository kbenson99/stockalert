package com.benson.stockalert;


import android.net.Uri;
import android.content.Context;

public class JsonURL {
	
	private Context myContext;
	private Uri.Builder uriBuilder;
	
	public JsonURL( Context c )	{
		this.myContext = c;
	}
	
	public void createJsonMapUri() {
		
		uriBuilder = Uri.parse( this.myContext.getString( R.string.JsonUrl ) ).buildUpon();		
	}
	
	
	public void setQueryParameter( String key, String value ){
		uriBuilder.appendQueryParameter( key, value );
	}
	
	
	public Uri.Builder getUriBuilder(){
		return uriBuilder;
	}

}
