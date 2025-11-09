package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;
import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;

public class OrderDTO {
	private Long id;
	private Instant moment;
	private Integer orderStatus;
	private User client;
	private Set<OrderItem> items = new HashSet<>();
	private Payment payment;
	
	// Construtores
	public OrderDTO(Order order) {
		BeanUtils.copyProperties(order, this);
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
	
	public Integer getOrderStatus() {
		return orderStatus;
	}
	public void setOrderStatus(Integer orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public User getClient() {
		return client;
	}
	public void setClient(User client) {
		this.client = client;
	}
	
	public Set<OrderItem> getItems() {
		return items;
	}
	public void setItems(Set<OrderItem> items) {
		this.items = items;
	}
	
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
}
