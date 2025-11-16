package com.carlossilvadev.projeto_integrador_web_app.dto;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductDTO {
	private Long id;
	
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 1, max = 50, message = "Nome deve ter entre 1 a 50 caracteres")
	private String nome;
	
	@NotBlank(message = "Descrição é obrigatória")
	@Size(min = 10, max = 50, message = "Descrição deve ter entre 10 a 50 caracteres")
	private String descricao;
	
	@NotNull(message = "Preço é obrigatório")
	@Positive(message = "Preço deve ser maior que zero")
	@DecimalMin(value = "0.01", message = "Preço deve ser no mínimo R$ 0.01")
	@Digits(integer = 6, fraction = 2, message = "Preço deve ter no máximo 6 digitos inteiros e 2 digitos decimais")
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
