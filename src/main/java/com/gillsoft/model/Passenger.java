package com.gillsoft.model;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Passenger implements Serializable {

	private static final long serialVersionUID = -1725504289434662482L;

	private String firstname;
	private String lastname;
	private String phone;
	private String birthdate;
	private String type;

	@JsonProperty("reference_id")
	private String referenceId;

	@JsonProperty("parental_permission")
	private boolean permission;

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastname() {
		return lastname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getBirthdate() {
		return birthdate;
	}

	public void setBirthdate(String birthdate) {
		this.birthdate = birthdate;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getReferenceId() {
		return referenceId;
	}

	public void setReferenceId(String referenceId) {
		this.referenceId = referenceId;
	}

	public boolean isPermission() {
		return permission;
	}

	public void setPermission(boolean permission) {
		this.permission = permission;
	}

}
