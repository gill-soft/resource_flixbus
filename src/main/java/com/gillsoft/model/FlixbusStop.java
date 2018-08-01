package com.gillsoft.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlixbusStop implements Serializable {

	private static final long serialVersionUID = -4896377258325679740L;
	
	private TripTime departure;
	
	private TripTime arrival;
	
	private int sequence;
	
	private Station station;
	
	@JsonProperty("departure_had")
	private TripTimeHad departureHad;

	@JsonProperty("arrival_had")
	private TripTimeHad arrivalHad;

	public TripTime getDeparture() {
		return (departureHad != null && departureHad.getEta() != null && !departureHad.equals(departure)) ? departureHad.getEta() : departure;
	}

	public void setDeparture(TripTime departure) {
		this.departure = departure;
	}

	public TripTime getArrival() {
		return (arrivalHad != null && arrivalHad.getEta() != null && !arrivalHad.equals(arrival)) ? arrivalHad.getEta() : arrival;
	}

	public void setArrival(TripTime arrival) {
		this.arrival = arrival;
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public Station getStation() {
		return station;
	}

	public void setStation(Station station) {
		this.station = station;
	}

	public TripTimeHad getDepartureHad() {
		return departureHad;
	}

	public void setDepartureHad(TripTimeHad departureHad) {
		this.departureHad = departureHad;
	}

	public TripTimeHad getArrivalHad() {
		return arrivalHad;
	}

	public void setArrivalHad(TripTimeHad arrivalHad) {
		this.arrivalHad = arrivalHad;
	}
	
	

}
