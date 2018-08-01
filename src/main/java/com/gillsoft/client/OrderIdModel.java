package com.gillsoft.client;

import java.util.ArrayList;
import java.util.List;

import com.gillsoft.model.AbstractJsonModel;
import com.gillsoft.model.ReservationResult;

public class OrderIdModel extends AbstractJsonModel {
	
	private static final long serialVersionUID = 5661521517528841959L;
	
	private List<ReservationResult> services = new ArrayList<>();

	public List<ReservationResult> getServices() {
		return services;
	}

	public void setServices(List<ReservationResult> services) {
		this.services = services;
	}
	
	@Override
	public OrderIdModel create(String json) {
		return (OrderIdModel) super.create(json);
	}
	
}
