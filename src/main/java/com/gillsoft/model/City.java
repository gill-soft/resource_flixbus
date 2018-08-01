package com.gillsoft.model;

import java.io.Serializable;

public class City implements Serializable {

	private static final long serialVersionUID = 7008207316805948832L;

	private long id;
	
	private String name;
	
	private Coordinates coordinates;
	
	private Country country;
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public Coordinates getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public void setCountry(Country country) {
		this.country = country;
	}
	
}
