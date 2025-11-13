package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class ProductDTO {
	private Long id;
	private String nome;
	private String descricao;
	private double preco;
	private String imgUrl;
	private Set<Category> categories = new HashSet<>();
	
	@JsonIgnore
	private Set<OrderItem> items = new HashSet<>();
	
	// Construtores
	public ProductDTO(Product product) {
		BeanUtils.copyProperties(product, this);
		
		if (product.getCategories() != null) {
			this.categories = new HashSet<>(product.getCategories());
		}
	}

	public ProductDTO() {
	}
	
	// getters e setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public double getPreco() {
		return preco;
	}
	public void setPreco(double preco) {
		this.preco = preco;
	}

	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	public Set<Category> getCategories() {
		return categories;
	}

	public Set<OrderItem> getItems() {
		return items;
	}
}
