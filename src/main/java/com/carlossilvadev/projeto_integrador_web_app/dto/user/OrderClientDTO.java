package com.carlossilvadev.projeto_integrador_web_app.dto.user;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;

public class OrderClientDTO {
	private Long id;
	private String nome;
	private String login;
	
	// Construtores (copiando propriedades da entidade User para o DTO)
	public OrderClientDTO(User user) {
		this.id = user.getId();
		this.nome = user.getNome();
		this.login = user.getLogin();
	}
	
	public OrderClientDTO() {
	}
	
	// getters e setters
	public Long getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getLogin() {
		return login;
	}
}