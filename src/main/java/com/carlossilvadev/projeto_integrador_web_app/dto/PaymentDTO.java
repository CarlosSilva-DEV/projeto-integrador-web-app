package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.time.Instant;

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
		this.status = PaymentStatus.PENDENTE;
	}
	
	public PaymentDTO(Payment payment, String pixQrCode, String pixCopiaCola) {
		this.id = payment.getId();
		this.moment = payment.getMoment();
		this.order = new OrderDTO(payment.getOrder());
		this.status = PaymentStatus.PENDENTE;
		this.pixQrCode = pixQrCode;
		this.pixCopiaCola = pixCopiaCola;
	}
	
	public PaymentDTO() {
	}
	
	// getters e setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Instant getMoment() {
		return moment;
	}
	public void setMoment(Instant moment) {
		this.moment = moment;
	}

	public OrderDTO getOrder() {
		return order;
	}
	public void setOrder(OrderDTO order) {
		this.order = order;
	}

	public PaymentStatus getStatus() {
		return status;
	}
	public void setStatus(PaymentStatus status) {
		this.status = status;
	}

	public String getPixQrCode() {
		return pixQrCode;
	}
	public void setPixQrCode(String pixQrCode) {
		this.pixQrCode = pixQrCode;
	}

	public String getPixCopiaCola() {
		return pixCopiaCola;
	}
	public void setPixCopiaCola(String pixCopiaCola) {
		this.pixCopiaCola = pixCopiaCola;
	}
}
