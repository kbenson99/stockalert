package com.benson.stockalert.model;

import java.io.Serializable;


public class Alert extends Stock implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2481668587816737401L;

	private double breakout;
	private int alerted;
	
	public Alert(){};
	
	public Alert(String ticker, String exchange, double breakout, int alerted) 
	{
		this.setTicker(ticker);
		this.setExchange(exchange);
		this.setBreakout(breakout);
		this.setAlerted(alerted);		
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
	
}


