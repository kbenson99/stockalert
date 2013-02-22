package com.benson.stockalert.model;

import java.io.Serializable;


public class Quote extends Stock implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2481668587816737401L;
	private long requestId;

	public Quote(){};
	
	public Quote(String quote) {
		this.setTicker(quote);		
	}

	public long getRequestId() {
		return requestId;
	}

	public void setRequestId(long requestId) {
		this.requestId = requestId;
	}

}


