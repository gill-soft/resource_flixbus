package com.gillsoft.model;

import java.io.Serializable;

import org.springframework.core.ParameterizedTypeReference;

public class AuthenticateResult implements Serializable {

	private static final long serialVersionUID = 2820224116847122261L;
	
	private static final ParameterizedTypeReference<AuthenticateResult> typeRef = new ParameterizedTypeReference<AuthenticateResult>() { };

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
	
	public static ParameterizedTypeReference<AuthenticateResult> getTypeReference() {
		return typeRef;
	}

}
