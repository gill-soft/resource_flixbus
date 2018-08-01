package com.gillsoft.model;

import java.io.Serializable;

public class OrderResult implements Serializable {

	private static final long serialVersionUID = 2138709536982759414L;

	private Order order;

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}

}
