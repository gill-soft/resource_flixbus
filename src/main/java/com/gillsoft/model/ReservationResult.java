package com.gillsoft.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.joda.time.DateTime;
import org.springframework.core.ParameterizedTypeReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.gillsoft.LocalityServiceController;
import com.gillsoft.client.RestClient;

public class ReservationResult implements Serializable {

	private static final long serialVersionUID = -3796036471239929764L;
	
	private static final ParameterizedTypeReference<ReservationResult> typeRef = new ParameterizedTypeReference<ReservationResult>() { };

	@JsonProperty(access = Access.WRITE_ONLY)
	private Integer code = -1;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private String message;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Boolean result;
	
	@JsonProperty("payment_id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long paymentId;
	
	@JsonProperty("order_id")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private Long orderId;
	
	@JsonProperty("download_hash")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String hash;

	private Reservation reservation;
	
	@JsonProperty(access = Access.WRITE_ONLY)
	private Cart cart;

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Boolean isResult() {
		return result;
	}

	public void setResult(Boolean result) {
		this.result = result;
	}

	public Long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(Long paymentId) {
		this.paymentId = paymentId;
	}

	public Reservation getReservation() {
		return reservation;
	}

	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}
	
	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
	}

	public static ParameterizedTypeReference<ReservationResult> getTypeReference() {
		return typeRef;
	}
	
	@JsonIgnore
	public Segment getSegment() {
		Segment segment = new Segment();
		CartItem cartItem = cart.getItems().values().iterator().next();
		segment.setId(cartItem.getTrip().getUid());
		segment.setNumber(cartItem.getLineDirection().getCode());
		segment.setDepartureDate(new DateTime(cartItem.getDeparture().getTimestamp() * 1000).toDate());
		segment.setArrivalDate(new DateTime(cartItem.getArrival().getTimestamp() * 1000).toDate());
		segment.setDeparture(LocalityServiceController.getLocality(String.valueOf(cartItem.getFrom().getId())));
		segment.setArrival(LocalityServiceController.getLocality(String.valueOf(cartItem.getTo().getId())));
		return segment;
	}
	
	@JsonIgnore
	public Price getPrice() {
		Price price = new Price();
		price.setCurrency(Currency.valueOf(RestClient.CURRENCY));
		price.setAmount(cart.getPrice().getTotal().divide(new BigDecimal(cart.getItems().values().iterator().next().getPassengerCount())));
		price.setVat(cart.getPrice().getTotalTax());
		// тариф и комиссии
		createTariffAndCommissions(price);
		return price;
	}
	
	@JsonIgnore
	private void createTariffAndCommissions(Price price) {
		Tariff priceTariff = new Tariff();
		priceTariff.setValue(price.getAmount());
		price.setTariff(priceTariff);
		
		if (cart.getPrice().isWithServiceFee() && cart.getPrice().getServiceFee() != null
				&& cart.getPrice().getServiceFee().compareTo(BigDecimal.ZERO) > 0) {
			if (price.getCommissions() == null) {
				price.setCommissions(new ArrayList<>());
			}
			Commission commission = new Commission();
			commission.setName("SERVICE FEE");
			commission.setValue(cart.getPrice().getServiceFee());
			commission.setType(ValueType.FIXED);
			commission.setValueCalcType(CalcType.IN);
			price.getCommissions().add(commission);
		}
		
		if (cart.getPrice().isWithDonation() && cart.getPrice().getDonation() != null
				&& cart.getPrice().getDonation().compareTo(BigDecimal.ZERO) > 0) {
			if (price.getCommissions() == null) {
				price.setCommissions(new ArrayList<>());
			}
			Commission commission = new Commission();
			commission.setName("CO2 COMPENSATION DONATION");
			commission.setValue(cart.getPrice().getDonation());
			commission.setType(ValueType.FIXED);
			commission.setValueCalcType(CalcType.IN);
			price.getCommissions().add(commission);
		}
	}

}
