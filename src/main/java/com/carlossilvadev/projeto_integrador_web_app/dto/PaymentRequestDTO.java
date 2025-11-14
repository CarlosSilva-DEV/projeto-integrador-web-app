package com.carlossilvadev.projeto_integrador_web_app.dto;

public class PaymentRequestDTO {
	private Long orderId;
	private String paymentMethod;
	
	// getters e setters
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
}
