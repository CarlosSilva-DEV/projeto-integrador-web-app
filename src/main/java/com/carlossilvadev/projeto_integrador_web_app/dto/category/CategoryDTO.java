package com.carlossilvadev.projeto_integrador_web_app.dto.category;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CategoryDTO {
	private Long id;
	
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 2, max = 20, message = "Nome deve ter entre 2 a 20 caracteres")
	private String nome;
	
	// Construtores
	public CategoryDTO(Category category) {
		this.id = category.getId();
		this.nome = category.getNome();
	}
	
	public CategoryDTO() {
	}
	
	// getters
	public Long getId() {
		return id;
	}

	public String getNome() {
		return nome;
	}
}