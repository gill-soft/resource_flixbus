package com.gillsoft.model;

import java.io.Serializable;

public class TripTime implements Serializable {

	private static final long serialVersionUID = -6899322827933354162L;

	private long timestamp;
	
	private String tz;

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public String getTz() {
		return tz;
	}

	public void setTz(String tz) {
		this.tz = tz;
	}

}
