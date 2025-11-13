package com.carlossilvadev.projeto_integrador_web_app.dto;

import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;

public class OrderItemDTO {
	private Long productId;
	private Integer quantidade;
	private double preco;
	private double subtotal;
	private ProductDTO product;
	
	// Construtor
	public OrderItemDTO() {
	}
	
	public OrderItemDTO(OrderItem orderItem) {
		this.productId = orderItem.getProduct().getId();
		this.quantidade = orderItem.getQuantidade();
		this.preco = orderItem.getPreco();
		this.subtotal = orderItem.getSubtotal();
		this.product = new ProductDTO(orderItem.getProduct());
	}
	
	// getters e setters
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
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

	public double getSubtotal() {
		return subtotal;
	}
	public void setSubtotal(double subtotal) {
		this.subtotal = subtotal;
	}

	public ProductDTO getProduct() {
		return product;
	}
	public void setProduct(ProductDTO product) {
		this.product = product;
	}
}
