package com.benson.stockalert.model;

import java.io.Serializable;


abstract public class Stock implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2481668587816737401L;
	private long id;
	private String ticker;
	private String exchange;
	private String name;
		

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTicker() {
		return ticker;
	}


	public void setTicker(String stock) {
		this.ticker = stock;
	}

	public String getExchange() {
		return exchange;
	}	

	public void setExchange(String exchange) {
		this.exchange = exchange;
	}		
	
	public String getName() {
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}
	
}


