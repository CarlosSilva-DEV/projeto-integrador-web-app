package com.carlossilvadev.projeto_integrador_web_app.dto.user;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;

public class UserResponseDTO {
	private Long id;
	private String nome;
	private String login;
	private String email;
	private String telefone;
	
	private UserRole role;
	
	// Construtores (copiando propriedades da entidade User para o DTO)
	public UserResponseDTO(User user) {
		this.id = user.getId();
		this.nome = user.getNome();
		this.login = user.getLogin();
		this.email = user.getEmail();
		this.telefone = user.getTelefone();
		this.role = user.getRole();
	}
	
	public UserResponseDTO() {
	}
	
	// getters
	public Long getId() {
		return id;
	}
	
	public String getNome() {
		return nome;
	}
	
	public String getLogin() {
		return login;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public UserRole getRole() {
		return role;
	}
}