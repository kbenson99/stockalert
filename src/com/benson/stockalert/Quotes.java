package com.benson.stockalert;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import android.app.Activity;
import android.content.Intent;
import android.text.ClipboardManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

import com.benson.stockalert.dao.QuoteDataSource;
import com.benson.stockalert.model.Quote;
import com.benson.stockalert.model.QuoteRequest;
import com.benson.stockalert.model.Stock;
import com.benson.stockalert.utility.Constants;
import com.benson.stockalert.view.QuoteAdapter;


public class Quotes extends StockActivity
{
	protected final String 			myName = this.getClass().getSimpleName();
	
	protected static final int		LAYOUT_ID = R.layout.alerts;
	protected static final int		ADAPTER_LAYOUT_ID = R.layout.stockquoterow;	
	
	protected static final int		MENU_ID = R.menu.quote_menu;
	protected static final int		CONTEXT_MENU_ID = R.menu.quote_context;   
	
    static final int 				STATIC_ACTIVITY_QUOTE_RESULT = 3; //positive > 0 integer. 
    static final int 				STATIC_ACTIVITY_ADD_TICKER_RESULT = 23; //positive > 0 integer.
    
    static final int				HISTORY_SUBMENU_ITEMID = -9999;
    static boolean					HISTORY_SUBMENU_ACTIVE = false;
    
    int								HISTORY_QUOTE_ID = -1;
    
    ClipboardManager 				mClipboard;
    
     
   
	protected void setup()
	{
    	mClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
    	
    	this.datasource = new QuoteDataSource(this);
    	
        this.m_adapter = new QuoteAdapter(this, this.getAdapterLayoutId(), 
        								new ArrayList<Stock>());
        
        setListAdapter(this.m_adapter);
	} 
	
	protected int getLayoutId()
	{
		return Quotes.LAYOUT_ID;
	}	

	protected int getAdapterLayoutId()
	{
		return Quotes.ADAPTER_LAYOUT_ID;
	}
	
	protected int getMenuId()
	{
		return Quotes.MENU_ID;
	}
	
	protected int getContextMenuId()
	{
		return Quotes.CONTEXT_MENU_ID;
	}	

 
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {          
          if (requestCode == STATIC_ACTIVITY_QUOTE_RESULT) //check if the request code is the one I sent
          {
                 if (resultCode == Activity.RESULT_OK) 
                 {
                	 String extra = data.getStringExtra(Constants.INTER_ACTIVITY_QUOTE_TAG);
                     Log.i(this.myName, "Quote dialog data:  " +extra );
                     
                     QuoteRequest rq = new QuoteRequest(extra, this.getString(R.string.InvalidWebSites));
                     
//                     Log.i(myName, rq.getQuote().toString());
                     ((QuoteDataSource) datasource).createQuote(rq, Constants.QUOTE_NOT_EXECUTED);
                     
                     HISTORY_QUOTE_ID = -1;
                     this.refresh();
                 }
          }
          if (requestCode == STATIC_ACTIVITY_ADD_TICKER_RESULT) //check if the request code is the one I sent
          {
                 if (resultCode == Activity.RESULT_OK) 
                 {
                	 String extra = data.getStringExtra(Constants.INTER_ACTIVITY_QUOTE_TAG);
                     Log.i(this.myName, "Add quote dialog data:  " +extra );
                     
                     this.m_menuSelectedStock = (Quote) this.m_adapter.getItem(0);
                     
                     QuoteRequest rq = new QuoteRequest(extra, this.getString(R.string.InvalidWebSites));
//                     Log.i(myName, rq.getQuote().toString());
                     ((QuoteDataSource) this.datasource).addToQuote(rq, this.m_menuSelectedStock.getId(), Constants.QUOTE_NOT_EXECUTED);
                                          
                     this.refresh();
                 }
          }          
          
        super.onActivityResult(requestCode, resultCode, data);
    } 
    

    
    
    protected boolean applyMenuChoice(MenuItem item)
    {
        switch (item.getItemId())
        {
        	case R.id.ViewChart:
                this.m_menuSelectedStock = (Stock) this.m_adapter.getItem(position);
                Intent chartIntent = new Intent(this, Chart.class);
                chartIntent.putExtra(this.getString(R.string.QuoteKey), this.m_menuSelectedStock);
                startActivity(chartIntent);
                return (true);
        	case R.id.Delete:
        		this.m_menuSelectedStock = (Quote) this.m_adapter.getItem(position);

				((QuoteDataSource) this.datasource).deleteQuoteRecord((Quote) this.m_menuSelectedStock);
				
				this.refresh();

                return (true);                
        }
        return false;
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) 
    {    	
    	if (menu.findItem(HISTORY_SUBMENU_ITEMID) != null)
    	{
        	menu.removeItem(HISTORY_SUBMENU_ITEMID);    		
    	}
    	
    	List<QuoteRequest> history = ((QuoteDataSource) datasource).getPreviousQuotes();
    	
    	Log.i(this.myName, "Adapter size:  " + m_adapter.m_stockList.size());
    	
    	if (m_adapter.m_stockList.size() <= 0)
    	{
    		MenuItem item = menu.findItem(R.id.copyquote);
    		item.setVisible(false);
    		
    		item = menu.findItem(R.id.refresh_quotes);
    		item.setVisible(false);     		
    		
    		item = menu.findItem(R.id.add_ticker);
    		item.setVisible(false);
    		
    		item = menu.findItem(R.id.delete_quote);
    		item.setVisible(false);
    		
    		item = menu.findItem(R.id.filtermenu);
    		item.setVisible(false);    		
    	}
    	else
    	{
    		MenuItem item = menu.findItem(R.id.copyquote);
    		item.setVisible(true);
    		
    		item = menu.findItem(R.id.refresh_quotes);
    		item.setVisible(true); 
    		
    		item = menu.findItem(R.id.add_ticker);
    		item.setVisible(true);
    		
    		item = menu.findItem(R.id.delete_quote);
    		item.setVisible(true);    		
    		
    		item = menu.findItem(R.id.filtermenu);
    		item.setVisible(true);    		    		
    	}

    	if (history.size() > 0)
    	{
    		MenuItem item = menu.findItem(R.id.clearquotes);
    		item.setVisible(true);
    		
        	SubMenu historyMenu = menu.addSubMenu(Menu.NONE, HISTORY_SUBMENU_ITEMID, 4, "Quote History");    	
        	
        	int cnt = 1;
        	
        	for (QuoteRequest record : history)
        	{
        		Long id = record.getId();

        		historyMenu.add(6, id.intValue(), cnt, StringUtils.join(record.getQuote(), ", "));
                cnt++;
        	}

        	historyMenu.setGroupCheckable(6, true, true);   		
    	} 
    	else
    	{
    		MenuItem item = menu.findItem(R.id.clearquotes);
    		item.setVisible(false);
    	}    	
    	
    	return super.onPrepareOptionsMenu(menu);
    }   

      

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle item selection
//    	Log.i(this.myName, "Item selected: " + item.getItemId());
        switch (item.getItemId())
        {
	        case R.id.delete_quote:
	        	this.m_menuSelectedStock = (Quote) this.m_adapter.getItem(0);
	        	
	        	((QuoteDataSource) this.datasource).deleteQuoteRequest((Quote) this.m_menuSelectedStock);
	
				HISTORY_QUOTE_ID = -1;
				this.refresh();
	            return true;        
        	case R.id.add_ticker:        		
	    		Intent addTicker = new Intent(this, com.benson.stockalert.dialogs.Quote.class);
	    		startActivityForResult(addTicker, STATIC_ACTIVITY_ADD_TICKER_RESULT);        		
        		return true;
        	case R.id.copyquote:
        		mClipboard.setText(StringUtils.join( m_adapter.getStockListSansInvalidTickers(), ' '));
        		return true;
	    	case R.id.quote_stock:
	    		Intent quote = new Intent(this, com.benson.stockalert.dialogs.Quote.class);
	    		startActivityForResult(quote, STATIC_ACTIVITY_QUOTE_RESULT);
	    		return true;        
            case R.id.refresh_quotes:
                refresh();
                return true;
 
            case R.id.clearquotes:
            	((QuoteDataSource) this.datasource).clearQuoteHistory();

				HISTORY_QUOTE_ID = -1;
				this.refresh();
                return true;
            case HISTORY_SUBMENU_ITEMID:
            	HISTORY_SUBMENU_ACTIVE = true;
            	return true;
            default:
            	if (HISTORY_SUBMENU_ACTIVE)
            	{
            		HISTORY_SUBMENU_ACTIVE = false;
            		HISTORY_QUOTE_ID = item.getItemId();      
            		
            		this.updateGuiScreen();
            	}
          
            	return super.onOptionsItemSelected(item);                   
        }
    }
       
    
	protected void getData() 
	{
		try 
		{
	    	if (HISTORY_QUOTE_ID > -1)
	    	{
	            Long Id = Long.parseLong( String.valueOf( HISTORY_QUOTE_ID ));
	            this.m_stocks = ((QuoteDataSource) datasource).getHistoryQuote(Id);   		
	    	}
	    	else
	    	{
	    		this.m_stocks = ((QuoteDataSource) datasource).getCurrentQuote();
	    	}
		} 
		catch (Exception e) 
		{
			Log.e(myName, "Data fetch failed");
		} 
	}   

}
