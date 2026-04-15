package com.carlossilvadev.projeto_integrador_web_app.dto.order;

import com.carlossilvadev.projeto_integrador_web_app.dto.product.ProductDTO;
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
	
	// getters
	public Long getProductId() {
		return productId;
	}
	
	public Integer getQuantidade() {
		return quantidade;
	}
	
	public double getPreco() {
		return preco;
	}

	public double getSubtotal() {
		return subtotal;
	}

	public ProductDTO getProduct() {
		return product;
	}
}
