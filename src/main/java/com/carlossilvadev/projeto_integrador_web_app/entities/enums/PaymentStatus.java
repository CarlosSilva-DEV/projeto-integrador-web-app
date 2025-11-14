package com.carlossilvadev.projeto_integrador_web_app.entities.enums;

public enum PaymentStatus {
	PENDENTE("PENDENTE"),
	PROCESSANDO("PROCESSANDO"),
	PAGO("PAGO");
	
	private String status;
	
	PaymentStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}