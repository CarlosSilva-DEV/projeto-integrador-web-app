package com.carlossilvadev.projeto_integrador_web_app.entities;

import java.io.Serializable;

import com.carlossilvadev.projeto_integrador_web_app.entities.pk.OrderItemPK;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_order_item")
public class OrderItem implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId // anotação para chave composta
	private OrderItemPK id = new OrderItemPK();
	private Integer quantidade;
	private double preco;
	private double subtotal;
	
	// Construtores
	public OrderItem() {
	}

	public OrderItem(Order order, Product product, Integer quantidade, double preco) {
		id.setOrder(order);
		id.setProduct(product);
		this.quantidade = quantidade;
		this.preco = preco;
	}
	
	// getters e setters
	@JsonIgnore
	public Order getOrder() {
		return id.getOrder();
	}
	public void setOrder(Order order) {
		id.setOrder(order);
	}
	
	public Product getProduct() {
		return id.getProduct();
	}
	public void setProduct(Product product) {
		id.setProduct(product);
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public double getPreco() {
		return preco;
	}
	public void setPreco(double preco) {
		this.preco = preco;
	}
	
	// calculo subtotal
	public double getSubtotal() {
		this.subtotal = this.getPreco() * this.getQuantidade();
		return this.subtotal;
	}
	
	// hashcode e equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		OrderItem other = (OrderItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}
