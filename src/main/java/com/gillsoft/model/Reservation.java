package com.gillsoft.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Reservation implements Serializable {

	private static final long serialVersionUID = -3140129599837272549L;

	private long id;
	
	private String token;
	
	@JsonProperty("expired_at")
	private TripTime expired;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public TripTime getExpired() {
		return expired;
	}

	public void setExpired(TripTime expired) {
		this.expired = expired;
	}

}
