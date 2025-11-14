package com.carlossilvadev.projeto_integrador_web_app.entities.enums;

public enum OrderStatus {
	AGUARDANDO_PAGAMENTO("AGUARDANDO_PAGAMENTO"),
	PROCESSANDO_PAGAMENTO("PROCESSANDO_PAGAMENTO"),
	PAGO("PAGO"),
	CANCELADO("CANCELADO");
	
	private String status;
	
	OrderStatus(String status) {
		this.status = status;
	}
	
	public String getStatus() {
		return status;
	}
}