package com.gillsoft.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CartPrice implements Serializable {

	private static final long serialVersionUID = 590964626208010678L;

	@JsonProperty("total_tax")
	private BigDecimal totalTax; //Total tax value of the cart in the requested currency.

	private BigDecimal donation; //CO2 composensation donation part of the cart in the requested currency.

	private BigDecimal total; //Cart total in the requested currency.

	@JsonProperty("base_total")
	private BigDecimal baseTotal; //Cart total in EUR currency.

	private BigDecimal discount; //Discount of the cart in the requested currency.

	@JsonProperty("service_fee")
	private BigDecimal serviceFee;

	@JsonProperty("with_service_fee")
	private Boolean withServiceFee;

	@JsonProperty("with_donation")
	private Boolean withDonation;

	public BigDecimal getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(BigDecimal totalTax) {
		this.totalTax = totalTax;
	}

	public BigDecimal getDonation() {
		return donation;
	}

	public void setDonation(BigDecimal donation) {
		this.donation = donation;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public BigDecimal getBaseTotal() {
		return baseTotal;
	}

	public void setBaseTotal(BigDecimal baseTotal) {
		this.baseTotal = baseTotal;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}

	public BigDecimal getServiceFee() {
		return serviceFee;
	}

	public void setServiceFee(BigDecimal serviceFee) {
		this.serviceFee = serviceFee;
	}

	public Boolean isWithServiceFee() {
		return withServiceFee;
	}

	public void setWithServiceFee(Boolean withServiceFee) {
		this.withServiceFee = withServiceFee;
	}

	public Boolean isWithDonation() {
		return withDonation;
	}

	public void setWithDonation(Boolean withDonation) {
		this.withDonation = withDonation;
	}
}
