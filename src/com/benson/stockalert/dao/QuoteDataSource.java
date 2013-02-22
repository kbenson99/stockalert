package com.benson.stockalert.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils.InsertHelper;
import android.util.Log;

import com.benson.stockalert.model.Quote;
import com.benson.stockalert.model.QuoteRequest;

public class QuoteDataSource extends FinanceDataSource 
{
	private final String myName = this.getClass().getSimpleName();

	public QuoteDataSource(Context context) 
	{
		dbHelper = new QuoteSQLiteHelper(context);
	}

	public void clearQuoteHistory() 
	{
		this.openToWrite();
		try 
		{
			database.delete(QuoteSQLiteHelper.TABLE_QUOTE, null, null);
			database.delete(QuoteSQLiteHelper.TABLE_QUOTES, null, null);
		} 
		finally 
		{
			if (database != null) {
				this.close();
			}
		}
	}

	public long getLastId() {
		long Id = -1;
		Cursor cursor = null;
		this.openToRead();
		try {
			cursor = database.query("sqlite_sequence", new String[] { "seq" },
					"name = '" + QuoteSQLiteHelper.TABLE_QUOTE + "'", null,
					null, null, null);
			cursor.moveToFirst();
			Id = cursor.getLong(0);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (database != null) {
				this.close();
			}
		}
		return Id;
	}

	private void insertQuote(QuoteRequest request, long insertId, int executed) {
		List<String> stocksInDB = new ArrayList<String>();
		List<Quote> currentQuotes = this.getQuote(insertId);
		for (Quote tmp : currentQuotes) {
			stocksInDB.add(tmp.getTicker());
		}

		this.openToWrite();

		InsertHelper ih = new InsertHelper(database,
				QuoteSQLiteHelper.TABLE_QUOTES);

		// Get the numeric indexes for each of the columns that we're updating
		int quoteId = ih.getColumnIndex(QuoteSQLiteHelper.COLUMN_ID);
		int quoteValue = ih
				.getColumnIndex(QuoteSQLiteHelper.COLUMN_QUOTE_STRING);

		try {
			database.execSQL("PRAGMA synchronous=OFF");
			database.setLockingEnabled(false);
			database.beginTransaction();

			for (String quote : request.getQuote()) {
				if (!stocksInDB.contains(quote)) {
					Log.i(this.myName, quote);

					// Get the InsertHelper ready to insert a single row
					ih.prepareForInsert();

					// Add the data for each column
					ih.bind(quoteId, insertId);
					ih.bind(quoteValue, quote);

					// Insert the row into the database.
					ih.execute();
				}
			}
			database.setTransactionSuccessful();
		} finally {
			database.endTransaction();
			database.setLockingEnabled(true);
			database.execSQL("PRAGMA synchronous=NORMAL");
			ih.close();
		}
	}

	public void addToQuote(QuoteRequest request, long insertId, int executed) {
		try {
			this.insertQuote(request, insertId, executed);
		} finally {
			if (database != null) {
				this.close();
			}
		}

	}

	public void createQuote(QuoteRequest request, int executed) {
		this.openToWrite();
		try {
			ContentValues values = new ContentValues();
			values.put(QuoteSQLiteHelper.COLUMN_EXECUTED, executed);

			long insertId = database.insert(QuoteSQLiteHelper.TABLE_QUOTE,
					null, values);

			Log.i(this.myName, "Quote insert:  ID = " + insertId);

			this.insertQuote(request, insertId, executed);
		} finally {
			if (database != null) {
				this.close();
			}
		}
	}

	public void deleteQuoteRecord(Quote quote) {
		this.openToWrite();
		try {
			database.delete(
					QuoteSQLiteHelper.TABLE_QUOTES,
					QuoteSQLiteHelper.COLUMN_REQUEST_ID + " = "
							+ quote.getRequestId(), null);
			// database.delete(QuoteSQLiteHelper.TABLE_QUOTE,
			// QuoteSQLiteHelper.COLUMN_ID
			// + " = " + quote.getId(), null);

		} finally {
			if (database != null) {
				this.close();
			}
		}
	}

	public void deleteQuoteRequest(Quote quote) {
		this.openToWrite();
		try {
			database.delete(QuoteSQLiteHelper.TABLE_QUOTES,
					QuoteSQLiteHelper.COLUMN_ID + " = " + quote.getId(), null);
			database.delete(QuoteSQLiteHelper.TABLE_QUOTE,
					QuoteSQLiteHelper.COLUMN_ID + " = " + quote.getId(), null);
		} finally {
			if (database != null) {
				this.close();
			}
		}
	}

	public List<QuoteRequest> getPreviousQuotes() {
		List<QuoteRequest> quotes = new ArrayList<QuoteRequest>();

		Cursor cursor = null;
		this.openToRead();
		try {
			String query = "select a." + QuoteSQLiteHelper.COLUMN_ID + ", b."
					+ QuoteSQLiteHelper.COLUMN_REQUEST_ID + ", b."
					+ QuoteSQLiteHelper.COLUMN_QUOTE_STRING + " from "
					+ QuoteSQLiteHelper.TABLE_QUOTE + " a, "
					+ QuoteSQLiteHelper.TABLE_QUOTES + " b where a."
					+ QuoteSQLiteHelper.COLUMN_ID + " = b."
					+ QuoteSQLiteHelper.COLUMN_ID + " order by 1";
			cursor = database.rawQuery(query, null);

			cursor.moveToFirst();
			long quoteId = -1;
			QuoteRequest request = new QuoteRequest();
			while (!cursor.isAfterLast()) {
				long id = cursor.getLong(0);
				// Log.i(this.myName, "History: " + cursor.getString(2));
				if (quoteId == id) {
					request.addQuote(cursor.getString(2).toUpperCase(
							Locale.ENGLISH));
				} else {
					if (quoteId != -1) {
						quotes.add(request);
					}

					quoteId = id;
					request = new QuoteRequest();
					request.setId(quoteId);
					request.setRequestId(cursor.getLong(1));
					request.addQuote(cursor.getString(2).toUpperCase());
				}

				cursor.moveToNext();
			}

			if (request.getQuote().size() > 0) {
				quotes.add(request);
			}
		}

		finally {
			if (cursor != null) {
				cursor.close();
			}
			if (database != null) {
				this.close();
			}
		}
		return quotes;
	}

	private List<Quote> getQuote(long Id) {
		List<Quote> quotes = new ArrayList<Quote>();

		Cursor cursor = null;
		Quote quote;
		this.openToRead();
		try {
			cursor = database.rawQuery("select * from "
					+ QuoteSQLiteHelper.TABLE_QUOTES + " where "
					+ QuoteSQLiteHelper.COLUMN_ID + " = " + Id, null);

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				quote = cursorToQuote(cursor);
				quotes.add(quote);

				cursor.moveToNext();
			}
		} finally {
			if (cursor != null) {
				cursor.close();
			}
			if (database != null) {
				this.close();
			}
		}
		return quotes;
	}

	public List<Quote> getCurrentQuote() {
		return this.getQuote(this.getLastId());
	}

	public List<Quote> getHistoryQuote(long Id) {
		return this.getQuote(Id);
	}

	private Quote cursorToQuote(Cursor cursor) {
		Quote quote = new Quote();
		quote.setRequestId(cursor
				.getLong(QuoteSQLiteHelper.COLUMN_REQUEST_INDEX));
		quote.setId(cursor.getLong(QuoteSQLiteHelper.COLUMN_ID_INDEX));
		quote.setTicker(cursor
				.getString(QuoteSQLiteHelper.COLUMN_QUOTE_STRING_INDEX));
		return quote;
	}
}
