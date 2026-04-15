package com.carlossilvadev.projeto_integrador_web_app.dto.payment;

import java.time.Instant;

import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

public class PaymentDTO {
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
	private Instant moment;
	
	private OrderDTO order;
	private PaymentStatus status;
	private String pixQrCode;
	private String pixCopiaCola;
	
	// Construtor
	public PaymentDTO(Payment payment) {
		this.id = payment.getId();
		this.moment = payment.getMoment();
		this.order = new OrderDTO(payment.getOrder());
		this.status = payment.getStatus();
	}
	
	public PaymentDTO(Payment payment, String pixQrCode, String pixCopiaCola, PaymentStatus status) {
		this.id = payment.getId();
		this.moment = payment.getMoment();
		this.order = new OrderDTO(payment.getOrder());
		this.status = status;
		this.pixQrCode = pixQrCode;
		this.pixCopiaCola = pixCopiaCola;
	}
	
	public PaymentDTO() {
	}
	
	// getters
	public Long getId() {
		return id;
	}

	public Instant getMoment() {
		return moment;
	}

	public OrderDTO getOrder() {
		return order;
	}

	public PaymentStatus getStatus() {
		return status;
	}

	public String getPixQrCode() {
		return pixQrCode;
	}

	public String getPixCopiaCola() {
		return pixCopiaCola;
	}
}
