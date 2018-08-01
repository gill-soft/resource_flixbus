package com.gillsoft.model;

import java.io.Serializable;

public class Country implements Serializable {

	private static final long serialVersionUID = -4179275372650194997L;

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
