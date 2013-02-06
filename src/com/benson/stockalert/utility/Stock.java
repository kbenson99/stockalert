package com.benson.stockalert.utility;

import java.io.Serializable;


public class Stock implements Serializable{
	private long id;
	private String stock;
	private String exchange;
	private double breakout;
	private int alerted;
	
	
	public Stock(String stock, String exchange, double breakout, int alerted) {
		this.setStock(stock);
		this.setExchange(exchange);
		this.setBreakout(breakout);
		this.setAlerted(alerted);
		
	}
	
	public Stock(){};

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStock() {
		return stock;
	}


	public void setStock(String stock) {
		this.stock = stock;
	}

	public String getExchange() {
		return exchange;
	}	

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}	
	
	public double getBreakout() {	
		return breakout;
	}

	public void setBreakout(double breakout) {
		this.breakout = breakout;
	}
	
	
	public int getAlerted() {
		return alerted;
	}

	public void setAlerted(int alerted) {
		this.alerted = alerted;
	}	
	
	public boolean hasBroken(Double currentPrice) {	
		double m_current = currentPrice.doubleValue();
		return (m_current >= this.breakout );
	}
	
	
	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString() {
		return "ID = " + id + ", ticker = " + stock + ", breakout = " + breakout + ", alerted " + alerted;
	}
}


