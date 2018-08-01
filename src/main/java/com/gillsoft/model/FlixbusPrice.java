package com.gillsoft.model;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

public class FlixbusPrice implements Serializable {

	private static final long serialVersionUID = 6935346684965691570L;

	@JsonProperty("total_tax")
	private BigDecimal totalTax;
	
	private BigDecimal donation;
	
	private BigDecimal total;
	
	@JsonProperty("base_total")
	private BigDecimal baseTotal;
	
	private BigDecimal discount;
	
	private BigDecimal credit;
	
	private BigDecimal subtotal;
	
	@JsonProperty("subtotal_net")
	private BigDecimal subtotalNet;
	
	private BigDecimal value;
	
	@JsonProperty("with_donation")
	private boolean withDonation;
	
	private String currency;

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

	public BigDecimal getCredit() {
		return credit;
	}

	public void setCredit(BigDecimal credit) {
		this.credit = credit;
	}

	public BigDecimal getSubtotal() {
		return subtotal;
	}

	public void setSubtotal(BigDecimal subtotal) {
		this.subtotal = subtotal;
	}

	public BigDecimal getSubtotalNet() {
		return subtotalNet;
	}

	public void setSubtotalNet(BigDecimal subtotalNet) {
		this.subtotalNet = subtotalNet;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public boolean isWithDonation() {
		return withDonation;
	}

	public void setWithDonation(boolean withDonation) {
		this.withDonation = withDonation;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

}
