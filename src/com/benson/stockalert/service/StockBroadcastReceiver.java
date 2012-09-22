package com.benson.stockalert.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Vibrator;
import android.util.Log;

import com.benson.stockalert.R;
import com.benson.stockalert.StockQuote;
import com.benson.stockalert.TabDemo;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.utility.Network;
import com.benson.stockalert.utility.Stock;
import com.benson.stockalert.utility.StockDataSource;

public class StockBroadcastReceiver extends BroadcastReceiver
{

    private final String myName = this.getClass().getSimpleName();
    private Context      myContext;

    @SuppressWarnings("static-access")
    @Override
    public void onReceive(Context context, Intent intent)
    {
        Log.i(myName, "Stock Broadcast checking if there is work to do");
        this.myContext = context;

        int dailyStart = Integer.parseInt(context.getString(R.integer.dailyStart));
        int dailyStop = Integer.parseInt(context.getString(R.integer.dailyStop));

        Calendar cal = Calendar.getInstance();

        if (cal.getTimeZone().getDisplayName().equals("GMT+00:00"))
        {
            // TimeZone z = cal.getTimeZone();
            int offset = -14400000;
            int offsetHrs = offset / 1000 / 60 / 60;
            cal.add(Calendar.HOUR_OF_DAY, (offsetHrs));
        }

        Log.i(myName, "Current Time:  "
            + cal.getTime());

        int day = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR_OF_DAY);

        // handleStocksAlerts();
        if (day != cal.SATURDAY
            && day != cal.SUNDAY)
        {
            Log.i(myName, "Not on the weekend!  So, let's check the time.");
            if (hour >= dailyStart
                && hour <= dailyStop)
            {
                Log.i(myName, "Stock Broadcast will now do some work");
                handleStocksAlerts();
            }
            else
            {
                Log.i(myName, "Stock Broadcast is not within service time.");
            }

        }
        else
        {
            Log.i(myName, "It's the weekend, taking a break!");
        }
    }


    private void handleStocksAlerts()
    {
        int cnt = 1;
        if (!new Network(this.myContext).isOnline())
        {
            while (cnt <= 4)
            {
                if (!new Network(this.myContext).isOnline())
                {
                    try
                    {
                        Log.i(this.myName,
                            "No network connection for alert notification.  Try again in 45secs");
                        Thread.sleep(45000);
                    }
                    catch (InterruptedException ie)
                    {
                        // don't care if interrupted
                    }
                    cnt++;
                }
                else
                {
                    Log.i(this.myName, "Got a network connection after "
                        + cnt + " attempts");
                    this.checkStocksForAlerts();
                    cnt = 5;
                }
            }
            Log.i(this.myName,
                "No network connection for alert notification.  Try again at the scheduled time");
        }
        else
        {
            this.checkStocksForAlerts();
        }
    }

    private void checkStocksForAlerts()
    {
        Log.i(this.myName, "Beginning checking stocks for target breach");
        HashMap m_stockMap = new HashMap();
        StockQuote m_stockquote = new StockQuote(this.myContext);
        StockDataSource datasource = new StockDataSource(this.myContext);

        JSONArray localJSONArray;
        JSONObject localJSONObject = null;

        ArrayList<Stock> mystocks;
        try
        {

            ArrayList arrayList = new ArrayList();

            StringBuffer sb = new StringBuffer();

            NotificationManager notificationManager = (NotificationManager) this.myContext
                .getSystemService(this.myContext.NOTIFICATION_SERVICE);

            datasource.open();
            mystocks = datasource.getAllStocks();

            if (mystocks != null
                && mystocks.size() > 0)
            {
                for (Stock alertstock : mystocks)
                {
                    arrayList.add("\""
                        + alertstock.getStock() + "\"");
                }

                if (arrayList.size() > 0)
                {

                    String m_stockString = StringUtils.join(arrayList, ',');
                    Log.d(this.myName, "m_stockString = "
                        + m_stockString);

                    if (arrayList.size() > 1)
                    {
                        localJSONArray = m_stockquote.getJsonStockArray(m_stockString);

                        for (int i = 0; i < localJSONArray.length(); ++i)
                        {
                            localJSONObject = localJSONArray.getJSONObject(i);
                            m_stockMap.put(localJSONObject.getString(Constants.JSON_TICKER_KEY),
                                localJSONObject);
                        }
                    }
                    else
                    {
                        JSONObject json = m_stockquote.getJsonStockObject(m_stockString);
                        m_stockMap.put(json.getString(Constants.JSON_TICKER_KEY), json);
                    }


                    // Log.i(this.myName, "m_stockMap length:  " + m_stockMap.size());

                    int cnt = 0;
                    for (Stock stock : mystocks)
                    {

                        if (stock.getAlerted() == 0)
                        {

                            if (!m_stockMap.containsKey(stock.getStock()))
                            {
                                int icon = android.R.drawable.btn_star_big_on;
                                StringBuffer alertmsg = new StringBuffer();
                                alertmsg.append("Check the ticker for "
                                    + stock.getStock());
                                long when = System.currentTimeMillis();

                                Notification notification = new Notification(icon, "", when);

                                notification.flags = Notification.DEFAULT_LIGHTS
                                    | Notification.FLAG_AUTO_CANCEL;

                                // Context context = getApplicationContext();
                                CharSequence contentTitle = "BAD STOCK DATA";
                                Intent notificationIntent = new Intent(this.myContext,
                                    TabDemo.class);
                                PendingIntent contentIntent = PendingIntent.getActivity(
                                    this.myContext, 0, notificationIntent, 0);
                                notification.setLatestEventInfo(this.myContext, contentTitle,
                                    alertmsg.toString(), contentIntent);

                                cnt++;
                                notificationManager.notify(cnt, notification);

                                // Get instance of Vibrator from current Context
                                Vibrator v = (Vibrator) this.myContext
                                    .getSystemService(Context.VIBRATOR_SERVICE);

                                // Vibrate for 300 milliseconds
                                v.vibrate(300);

                            }
                            else
                            {
                                localJSONObject = (JSONObject) m_stockMap.get(stock.getStock());

                                boolean m_stockBrokeout = false;
                                try
                                {
                                    m_stockBrokeout = stock.hasBroken(Double
                                        .parseDouble(localJSONObject
                                            .getString(Constants.JSON_PRICE_KEY)));
                                }
                                catch (JSONException je)
                                {
                                    Log.e(this.myName, "Failed to obtain stock information for "
                                        + stock.getStock());
                                }

                                if (m_stockBrokeout
                                    && stock.getAlerted() == 0)
                                {
                                    int icon = android.R.drawable.btn_star_big_on;
                                    StringBuffer alertmsg = new StringBuffer();
                                    alertmsg.append("Cur:  "
                                        + localJSONObject.getString(Constants.JSON_PRICE_KEY));
                                    alertmsg.append("; ");
                                    alertmsg.append("Break:  "
                                        + stock.getBreakout());
                                    long when = System.currentTimeMillis();

                                    Notification notification = new Notification(icon, "", when);

                                    notification.flags = Notification.DEFAULT_LIGHTS
                                        | Notification.FLAG_AUTO_CANCEL;

                                    // Context context = getApplicationContext();
                                    CharSequence contentTitle = "Stock Breakout for "
                                        + stock.getStock();
                                    Intent notificationIntent = new Intent(this.myContext,
                                        TabDemo.class);
                                    PendingIntent contentIntent = PendingIntent.getActivity(
                                        this.myContext, 0, notificationIntent, 0);
                                    notification.setLatestEventInfo(this.myContext, contentTitle,
                                        alertmsg.toString(), contentIntent);

                                    cnt++;
                                    notificationManager.notify(cnt, notification);


                                    // Get instance of Vibrator from current Context
                                    Vibrator v = (Vibrator) this.myContext
                                        .getSystemService(Context.VIBRATOR_SERVICE);

                                    // This example will cause the phone to vibrate "SOS" in Morse
                                    // Code
                                    // In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
                                    // There are pauses to separate dots/dashes, letters, and words
                                    // The following numbers represent millisecond lengths
                                    int dot = 200; // Length of a Morse Code "dot" in milliseconds
                                    int dash = 500; // Length of a Morse Code "dash" in milliseconds
                                    int short_gap = 200; // Length of Gap Between dots/dashes
                                    int medium_gap = 500; // Length of Gap Between Letters
                                    int long_gap = 1000; // Length of Gap Between Words
                                    long[] pattern = {
                                        0, // Start immediately
                                        dot, short_gap, dot, short_gap, dot, // s
                                        medium_gap, dash, short_gap, dash, short_gap, dash, // o
                                        medium_gap, dot, short_gap, dot, short_gap, dot, // s
                                        long_gap, dash, short_gap, dash, short_gap, dash, // o
                                        medium_gap};

                                    // Only perform this pattern one time (-1 means "do not repeat")
                                    v.vibrate(pattern, -1);


                                    datasource.updateStockAlert(stock.getId(), 1);
                                }
                            }


                        }
                    }
                }
            }
            Log.i(this.myName, "Completed checking stocks for target breach");
        }
        catch (Exception e)
        {
            Log.e(this.myName, e.getMessage());
        }
        finally
        {
            if (datasource != null)
            {
                datasource.close();
            }
        }
    }

}
