package com.gillsoft.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Coordinates implements Serializable {

	private static final long serialVersionUID = 7192768452852687671L;

	private BigDecimal latitude;
	
	private BigDecimal longitude;
	
	public BigDecimal getLatitude() {
		return latitude;
	}
	
	public void setLatitude(BigDecimal latitude) {
		this.latitude = latitude;
	}
	
	public BigDecimal getLongitude() {
		return longitude;
	}
	
	public void setLongitude(BigDecimal longitude) {
		this.longitude = longitude;
	}
	
}
