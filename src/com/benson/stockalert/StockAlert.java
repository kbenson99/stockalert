package com.benson.stockalert;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class StockAlert extends Activity  {

	  Button btn;

	  @Override
	  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);

	    //btn = new Button(this);
	    //btn.setOnClickListener(this);
	    //updateTime();
	    //setContentView(btn);
	  }
	  
		public void onClick( View view ) {
			switch (view.getId()) {
			}
		}
	
}
