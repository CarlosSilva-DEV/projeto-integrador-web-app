package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.util.List;

public class OrderCreateDTO {
	private List<OrderItemDTO> items;
	
	public List<OrderItemDTO> getItems() {
		return items;
	}
	
	public void setItems(List<OrderItemDTO> items) {
		this.items = items;
	}
}
