package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;

public class CategoryDTO {
	private Long id;
	private String nome;
	private Set<Product> products = new HashSet<>();
	
	// Construtores
	public CategoryDTO(Category category) {
		BeanUtils.copyProperties(category, this);
		
		if (category.getProducts() != null) {
			this.products = new HashSet<>(category.getProducts());
		}
	}
	
	public CategoryDTO() {
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

	public Set<Product> getProducts() {
		return products;
	}
}