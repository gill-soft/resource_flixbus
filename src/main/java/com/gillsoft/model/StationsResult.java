package com.gillsoft.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.ParameterizedTypeReference;

public class StationsResult implements Serializable {

	private static final long serialVersionUID = -6919267841013360706L;
	
	private static final ParameterizedTypeReference<StationsResult> typeRef = new ParameterizedTypeReference<StationsResult>() { };

	private List<Station> stations;

	public List<Station> getStations() {
		if (stations == null) {
			stations = new ArrayList<>();
		}
		return stations;
	}

	public void setStations(List<Station> stations) {
		this.stations = stations;
	}

	public static ParameterizedTypeReference<StationsResult> getTypeReference() {
		return typeRef;
	}

}
