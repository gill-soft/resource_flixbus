package com.gillsoft.model;

import java.io.Serializable;

public class LineDirection implements Serializable {

	private static final long serialVersionUID = -6785743213474122974L;

	private String code;
	
	private String direction;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

}
