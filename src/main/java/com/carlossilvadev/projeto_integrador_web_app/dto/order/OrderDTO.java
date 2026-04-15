package com.carlossilvadev.projeto_integrador_web_app.dto.order;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.carlossilvadev.projeto_integrador_web_app.dto.user.OrderClientDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderDTO {
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
	private Instant moment;
	
	private OrderStatus orderStatus;
	private OrderClientDTO client;
	private Set<OrderItemDTO> items = new HashSet<>();
	private double total;
	
	// Construtores
	public OrderDTO(Order order) {
		this.id = order.getId();
		this.moment = order.getMoment();
		this.orderStatus = order.getOrderStatus();
		this.client = new OrderClientDTO(order.getClient());
		this.total = order.getTotal();
		
		if (order.getItems() != null) {
			this.items = new HashSet<>(order.getItems().stream().map(OrderItemDTO::new).collect(Collectors.toSet()));
		}
	}
	
	public OrderDTO() {
	}
	
	// getters
	public Long getId() {
		return id;
	}
	
	public Instant getMoment() {
		return moment;
	}
	
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	
	public OrderClientDTO getClient() {
		return client;
	}
	
	public Set<OrderItemDTO> getItems() {
		return items;
	}
	
	public double getTotal() {
		return total;
	}
}
