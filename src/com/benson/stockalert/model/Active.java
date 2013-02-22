package com.benson.stockalert.model;

import java.io.Serializable;


public class Active extends Stock implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2481668587816737401L;
	
	private int quantity;
	private String broker;
	private String date;
	private int active;

	public Active(){};
	
	public Active(String ticker, int quantity, String broker, String date)
	{
		this.setTicker(ticker);	
		this.setQuantity(quantity);
		this.setBroker(broker);
		this.setDate(date);
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public String getBroker() {
		return broker;
	}

	public void setBroker(String broker) {
		this.broker = broker;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getActive() {
		return active;
	}

	public void setActive(int active) {
		this.active = active;
	}	
	
}


