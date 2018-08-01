package com.gillsoft.model;

import java.io.Serializable;

public class Available implements Serializable {

	private static final long serialVersionUID = -582268696178426241L;

	private int seats;
	
	private int slots;

	public int getSeats() {
		return seats;
	}

	public void setSeats(int seats) {
		this.seats = seats;
	}

	public int getSlots() {
		return slots;
	}

	public void setSlots(int slots) {
		this.slots = slots;
	}

}
