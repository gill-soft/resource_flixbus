package com.gillsoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

public class TripsResult implements Serializable {

	private static final long serialVersionUID = 3863933717734387875L;
	
	private static final ParameterizedTypeReference<TripsResult> typeRef = new ParameterizedTypeReference<TripsResult>() { };

	private int code = -1;
	
	private String message;
	
	private List<FlixbusTrip> trips;

	public List<FlixbusTrip> getTrips() {
		if (trips == null) {
			trips = new ArrayList<>();
		}
		return trips;
	}

	public void setTrips(List<FlixbusTrip> trips) {
		this.trips = trips;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public static ParameterizedTypeReference<TripsResult> getTypeReference() {
		return typeRef;
	}

}
