package com.gillsoft.model;

import java.io.Serializable;
import java.util.Map;

public class Cart implements Serializable {

	private static final long serialVersionUID = 1219309940350511301L;

	CartPrice price;
	
	private Map<String, CartItem> items;

	public CartPrice getPrice() {
		return price;
	}

	public void setPrice(CartPrice price) {
		this.price = price;
	}

	public Map<String, CartItem> getItems() {
		return items;
	}

	public void setItem(Map<String, CartItem> items) {
		this.items = items;
	}

}
