package com.gillsoft.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TripItem implements Serializable {

	private static final long serialVersionUID = -5277176586909432638L;

	private String id;
	
	private String type;
	
	private String status;
	
	private boolean transborder;
	
	@JsonProperty("sale_restriction")
	private boolean saleRestriction;
	
	@JsonProperty("price_total_sum")
	private BigDecimal price;
	
	private String uid;
	
	@JsonProperty("operated_by")
	private List<Operator> operators;

	private Available available;

	private TripTime departure;

	private TripTime arrival;

	@JsonProperty("info_message")
	private String infoMessage;

	private List<FlixbusStop> stops;

	@JsonProperty("line_code")
	private String lineCode;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isTransborder() {
		return transborder;
	}

	public void setTransborder(boolean transborder) {
		this.transborder = transborder;
	}

	public boolean isSaleRestriction() {
		return saleRestriction;
	}

	public void setSaleRestriction(boolean saleRestriction) {
		this.saleRestriction = saleRestriction;
	}

	public BigDecimal getPrice() {
		return price;
	}

	public void setPrice(BigDecimal price) {
		this.price = price;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public List<Operator> getOperators() {
		if (operators == null) {
			operators = new ArrayList<>();
		}
		return operators;
	}

	public void setOperators(List<Operator> operators) {
		this.operators = operators;
	}

	public Available getAvailable() {
		return available;
	}

	public void setAvailable(Available available) {
		this.available = available;
	}

	public TripTime getDeparture() {
		return departure;
	}

	public void setDeparture(TripTime departure) {
		this.departure = departure;
	}

	public TripTime getArrival() {
		return arrival;
	}

	public void setArrival(TripTime arrival) {
		this.arrival = arrival;
	}
	
	public String getInfoMessage() {
		return infoMessage;
	}

	public void setInfoMessage(String infoMessage) {
		this.infoMessage = infoMessage;
	}

	public List<FlixbusStop> getStops() {
		return stops;
	}

	public void setStops(List<FlixbusStop> stops) {
		this.stops = stops;
	}

	public String getLineCode() {
		return lineCode;
	}

	public void setLineCode(String lineCode) {
		this.lineCode = lineCode;
	}
}
