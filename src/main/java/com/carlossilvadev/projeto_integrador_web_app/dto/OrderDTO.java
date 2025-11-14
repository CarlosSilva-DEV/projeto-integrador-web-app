package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

public class OrderDTO {
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT")
	private Instant moment;
	
	private OrderStatus orderStatus;
	private User client;
	private Set<OrderItemDTO> items = new HashSet<>();
	private double total;
	private Payment payment;
	
	// Construtores
	public OrderDTO(Order order) {
		this.id = order.getId();
		this.moment = order.getMoment();
		this.orderStatus = order.getOrderStatus();
		this.client = order.getClient();
		this.total = order.getTotal();
		this.payment = order.getPayment();
		
		if (order.getItems() != null) {
			this.items = new HashSet<>(order.getItems().stream().map(OrderItemDTO::new).collect(Collectors.toSet()));
		}
	}
	
	public OrderDTO() {
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
	
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public User getClient() {
		return client;
	}
	public void setClient(User client) {
		this.client = client;
	}
	
	public Set<OrderItemDTO> getItems() {
		return items;
	}
	public void setItems(Set<OrderItemDTO> items) {
		this.items = items;
	}
	
	public double getTotal() {
		return total;
	}
	
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
