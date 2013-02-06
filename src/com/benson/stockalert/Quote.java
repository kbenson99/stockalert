package com.benson.stockalert;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.benson.stockalert.utility.Constants;


public class Quote extends Activity
{

    public final String myName = this.getClass().getSimpleName();

    private EditText    text;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        this.text = (EditText) findViewById(R.id.editText1);


        // // Setup WiFi
        // wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        // // Get WiFi status
        // WifiInfo info = wifi.getConnectionInfo();
        // Log.d( this.myName, info.toString() );
        // Log.d(this.myName, "testing");
        //
        // //Check the our wifi is currently turned on or turned off
        //
        // if(wifi.isWifiEnabled()){
        // wifi.setWifiEnabled(false); // Turn on/off our wifi
        // }else{
        // wifi.setWifiEnabled(true);
        // }

        // textStatus.append("\n\nWiFi Status: " + info.toString());

        // String ns = Context.NOTIFICATION_SERVICE;
        // NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
        // //Notification localNotification = makeNewAlertNotification(this.getApplicationContext(),
        // localStockAlert, str2, "Alert triggered.(" + str3 + ")");
        //
        // int icon = R.drawable.shares;
        // CharSequence tickerText = "Hello";
        // long when = System.currentTimeMillis();
        // Notification notification = new Notification(icon, tickerText, when);
        //
        // Context context = getApplicationContext();
        // CharSequence contentTitle = "My notification";
        // CharSequence contentText = "Hello World!";
        // Intent notificationIntent = new Intent(this, JSONParseActivity.class);
        // PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        // notification.setLatestEventInfo(context, contentTitle, contentText, contentIntent);
        //
        // int HELLO_ID = 1;
        // mNotificationManager.notify(HELLO_ID, notification);


        // this.runStock( str1 );
        //
        // Log.i(this.myName, "url:finance/info?infotype=infoquoteall&q=" + paramString);
        //
        // try
        // {
        // JSONArray localJSONArray = new JSONArray(str1.substring(3));
        // JSONObject localJSONObject = localJSONArray.getJSONObject(0);
        // }
        // catch (JSONException localJSONException)
        // {
        // Log.e(this.myName, "get stocks json fail:" + paramString, localJSONException);
        // }
        // //String str2 = localJSONObject.getString(Table.Stocks.e.name()) + ":" +
        // localJSONObject.getString(Table.Stocks.t.name());
        // //String str3 = localJSONObject.getString(Table.Stocks.l.name());
    }


    public void onClick(View view)
    {
        switch (view.getId())
        {
            case R.id.button1:

                if (this.text.getText().length() == 0)
                {
                    Toast.makeText(this, "Please enter a stock symbol", Toast.LENGTH_LONG).show();
                    return;
                }
                String paramString = this.text.getText().toString(); // "OCLR,CINF,RDN,UGA,ODP";

                try
                {
                    StockQuote m_stockquote = new StockQuote(this);
                    JSONObject localJSONObject = m_stockquote.getJsonStockObject(paramString );


                    Toast.makeText(this, localJSONObject.getString(Constants.JSON_NAME_KEY)
                        + " = " + localJSONObject.getString(Constants.JSON_PRICE_KEY), 1).show();
                    Log.i(this.myName, localJSONObject.getString(Constants.JSON_NAME_KEY)
                        + " = " + localJSONObject.getString(Constants.JSON_PRICE_KEY));
                }
                catch (Exception e)
                {
                    Log.e(this.myName, "Error obtaining quote for "
                        + paramString, e);
                }
        }
    }

}
