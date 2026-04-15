package com.carlossilvadev.projeto_integrador_web_app.dto.product;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.carlossilvadev.projeto_integrador_web_app.dto.category.CategoryDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;

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
	private Set<CategoryDTO> categories = new HashSet<>();
	
	// Construtores
	public ProductDTO(Product product) {
		this.id = product.getId();
		this.nome = product.getNome();
		this.descricao = product.getDescricao();
		this.preco = product.getPreco();
		this.imgUrl = product.getImgUrl();
		
		if (product.getCategories() != null) {
			this.categories = new HashSet<>(product.getCategories().stream().map(CategoryDTO::new).collect(Collectors.toSet()));
		}
	}

	public ProductDTO() {
	}
	
	// getters
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public double getPreco() {
		return preco;
	}

	public String getImgUrl() {
		return imgUrl;
	}
	
	public Set<CategoryDTO> getCategories() {
		return categories;
	}
}
