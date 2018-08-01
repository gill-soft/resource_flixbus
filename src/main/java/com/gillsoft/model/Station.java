package com.gillsoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Station implements Serializable {

	private static final long serialVersionUID = -332270396824982828L;

	private String id;
	
	private String name;
	
	private String address;
	
	@JsonProperty("full_address")
	private String fullAddress;
	
	private Coordinates coordinates;
	
	private List<String> pairs;
	
	private Country country;
	
	@JsonProperty("city_id")
	private String cityId;
	
	private String warnings;
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getFullAddress() {
		return fullAddress;
	}
	
	public void setFullAddress(String fullAddress) {
		this.fullAddress = fullAddress;
	}
	
	public Coordinates getCoordinates() {
		return coordinates;
	}
	
	public void setCoordinates(Coordinates coordinates) {
		this.coordinates = coordinates;
	}
	
	public List<String> getPairs() {
		if (pairs == null) {
			pairs = new ArrayList<>();
		}
		return pairs;
	}
	
	public void setPairs(List<String> pairs) {
		this.pairs = pairs;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public void setCountry(Country country) {
		this.country = country;
	}

	public String getCityId() {
		return cityId;
	}

	public void setCityId(String cityId) {
		this.cityId = cityId;
	}

	public String getWarnings() {
		return warnings;
	}

	public void setWarnings(String warnings) {
		this.warnings = warnings;
	}
	
}
