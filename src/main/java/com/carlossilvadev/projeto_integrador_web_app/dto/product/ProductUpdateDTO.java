package com.carlossilvadev.projeto_integrador_web_app.dto.product;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.carlossilvadev.projeto_integrador_web_app.dto.category.CategoryDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class ProductUpdateDTO {
	@Size(min = 1, max = 50, message = "Nome deve ter entre 1 a 50 caracteres")
	private String nome;
	
	@Size(min = 10, max = 50, message = "Descrição deve ter entre 10 a 50 caracteres")
	private String descricao;
	
	@Positive(message = "Preço deve ser maior que zero")
	@DecimalMin(value = "0.01", message = "Preço deve ser no mínimo R$ 0.01")
	@Digits(integer = 6, fraction = 2, message = "Preço deve ter no máximo 6 digitos inteiros e 2 digitos decimais")
	private Double preco;
	
	private String imgUrl;
	private Set<CategoryDTO> categories = new HashSet<>();
	
	// Construtores
	public ProductUpdateDTO(Product product) {
		this.nome = product.getNome();
		this.descricao = product.getDescricao();
		this.preco = product.getPreco();
		this.imgUrl = product.getImgUrl();
		
		if (product.getCategories() != null) {
			this.categories = new HashSet<>(product.getCategories().stream().map(CategoryDTO::new).collect(Collectors.toSet()));
		}
	}

	public ProductUpdateDTO() {
	}
	
	// getters
	public String getNome() {
		return nome;
	}

	public String getDescricao() {
		return descricao;
	}

	public Double getPreco() {
		return preco;
	}

	public String getImgUrl() {
		return imgUrl;
	}
	
	public Set<CategoryDTO> getCategories() {
		return categories;
	}
	
	// métodos has (update de Produto)
	public boolean hasNome() {
		return nome != null;
	}
	
	public boolean hasDescricao() {
		return descricao != null;
	}
	
	public boolean hasPreco() {
		return preco != null;
	}
	
	public boolean hasImgUrl() {
		return imgUrl != null;
	}
	
	public boolean hasCategories() {
		return categories != null;
	}
}
