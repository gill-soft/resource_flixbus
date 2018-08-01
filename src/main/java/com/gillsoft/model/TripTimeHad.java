package com.gillsoft.model;

import java.io.Serializable;

public class TripTimeHad implements Serializable {

	private static final long serialVersionUID = 1303966368428614601L;

	private TripTime eta;

	public TripTime getEta() {
		return eta;
	}

	public void setEta(TripTime eta) {
		this.eta = eta;
	}

}
