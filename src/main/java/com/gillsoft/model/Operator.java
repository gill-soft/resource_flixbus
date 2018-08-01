package com.gillsoft.model;

import java.io.Serializable;

public class Operator implements Serializable {

	private static final long serialVersionUID = 1089229966603750210L;

	private String key;
	
	private String label;
	
	private String address;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
	
}
