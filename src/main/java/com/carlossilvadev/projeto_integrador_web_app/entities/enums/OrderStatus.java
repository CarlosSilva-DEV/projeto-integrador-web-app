package com.carlossilvadev.projeto_integrador_web_app.entities.enums;

public enum OrderStatus {
	// atribuição de números fixos aos enums
	AGUARDANDO_PAGAMENTO(1),
	PAGO(2),
	ENVIADO(3),
	ENTREGUE(4),
	CANCELADO(5);
	
	private int code;
	
	private OrderStatus(int code) {
		this.code = code;
	}
	
	// getter para acessar código do enum
	public int getCode() {
		return code;
	}
	
	// função que recebe um código e verifica qual o enum correspondente
	public static OrderStatus valueOf(int code) {
		for (OrderStatus value : OrderStatus.values()) {
			if (value.getCode() == code) {
				return value;
			}
		}
		throw new IllegalArgumentException("Invalid OrderStatus code");
	}
}