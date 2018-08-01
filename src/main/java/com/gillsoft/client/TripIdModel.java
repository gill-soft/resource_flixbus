package com.gillsoft.client;

import com.gillsoft.model.AbstractJsonModel;

public class TripIdModel extends AbstractJsonModel {

	private static final long serialVersionUID = -4570318053620484041L;

	private String id;
	
	private String uid;

	public TripIdModel() {

	}

	public TripIdModel(String id, String uid) {
		this.id = id;
		this.uid = uid;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	@Override
	public TripIdModel create(String json) {
		return (TripIdModel) super.create(json);
	}
}
