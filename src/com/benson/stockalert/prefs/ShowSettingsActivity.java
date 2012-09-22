package com.benson.stockalert.prefs;


import com.benson.stockalert.R;
import com.benson.stockalert.R.id;
import com.benson.stockalert.R.layout;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

public class ShowSettingsActivity extends Activity {

 @Override
 protected void onCreate(Bundle savedInstanceState) {

  super.onCreate(savedInstanceState);
  setContentView(R.layout.show_settings_layout);

  SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

  StringBuilder builder = new StringBuilder();

  builder.append("\n" + sharedPrefs.getBoolean("check_quotes", false));
  builder.append("\n" + sharedPrefs.getString("quote_interval", "-1"));

  TextView settingsTextView = (TextView) findViewById(R.id.settings_text_view);
  settingsTextView.setText(builder.toString());

 }

}
