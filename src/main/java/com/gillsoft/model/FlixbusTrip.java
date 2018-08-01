package com.gillsoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlixbusTrip implements Serializable {

	private static final long serialVersionUID = 993049579017487343L;
	
	private String uid;

	private Station from;
	
	private Station to;
	
	@JsonProperty("line_code")
	private String lineCode;
	
	private TripTime departure;
	
	private List<Passenger> passengers;
	
	private List<TripItem> items;
	
	private List<FlixbusStop> stops;

	public Station getFrom() {
		return from;
	}

	public void setFrom(Station from) {
		this.from = from;
	}

	public Station getTo() {
		return to;
	}

	public void setTo(Station to) {
		this.to = to;
	}

	public TripTime getDeparture() {
		return departure;
	}

	public void setDeparture(TripTime departure) {
		this.departure = departure;
	}

	public List<Passenger> getPassengers() {
		if (passengers == null) {
			passengers = new ArrayList<>();
		}
		return passengers;
	}

	public void setPassengers(List<Passenger> passengers) {
		this.passengers = passengers;
	}

	public List<TripItem> getItems() {
		if (items == null) {
			items = new ArrayList<>();
		}
		return items;
	}

	public void setItems(List<TripItem> items) {
		this.items = items;
	}

	public List<FlixbusStop> getStops() {
		return stops;
	}

	public void setStops(List<FlixbusStop> stops) {
		this.stops = stops;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getLineCode() {
		return lineCode;
	}

	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
	
}
