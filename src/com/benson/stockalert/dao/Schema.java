package com.benson.stockalert.dao;

public class Schema 
{	
	public enum DB_CREATE_SQL
	{
		ACTIVES(ActiveSQLiteHelper.ACTIVES_TABLE_CREATE),
		QUOTES(QuoteSQLiteHelper.QUOTES_TABLE_CREATE),
		QUOTE(QuoteSQLiteHelper.QUOTE_TABLE_CREATE),
		ALERTS(AlertSQLiteHelper.TABLE_CREATE);
	
		private String sql;
		
		private DB_CREATE_SQL(String s)
		{
			sql = s;		
		}
		
		public String getSQL()
		{
			return sql;
		}
	}
	
	public enum DB_TABLES
	{
		ACTIVES(ActiveSQLiteHelper.TABLE_ACTIVES),
		QUOTES(QuoteSQLiteHelper.TABLE_QUOTES),
		QUOTE(QuoteSQLiteHelper.TABLE_QUOTE),
		ALERTS(AlertSQLiteHelper.TABLE_STOCKS);
	
		private String table;
		
		private DB_TABLES(String s)
		{
			table = s;		
		}
		
		public String getTable()
		{
			return table;
		}		
	}
}
