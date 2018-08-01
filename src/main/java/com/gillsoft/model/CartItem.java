package com.gillsoft.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartItem implements Serializable{
	
	private static final long serialVersionUID = 2025356102710626571L;

	private TripItem trip;
	
	private Station from;
	
	private Station to;
	
	@JsonProperty("line_direction")
	private LineDirection lineDirection;
	
	private TripTime departure;
	
	private TripTime arrival;
	
	@JsonProperty("passenger_count")
	private int passengerCount;

	public TripItem getTrip() {
		return trip;
	}

	public void setTrip(TripItem trip) {
		this.trip = trip;
	}

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

	public LineDirection getLineDirection() {
		return lineDirection;
	}

	public void setLineDirection(LineDirection lineDirection) {
		this.lineDirection = lineDirection;
	}

	public TripTime getDeparture() {
		return departure;
	}

	public void setDeparture(TripTime departure) {
		this.departure = departure;
	}

	public TripTime getArrival() {
		return arrival;
	}

	public void setArrival(TripTime arrival) {
		this.arrival = arrival;
	}

	public int getPassengerCount() {
		return passengerCount;
	}

	public void setPassengerCount(int passengerCount) {
		this.passengerCount = passengerCount;
	}
}
