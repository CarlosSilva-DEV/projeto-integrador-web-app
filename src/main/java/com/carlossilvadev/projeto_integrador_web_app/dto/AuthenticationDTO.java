package com.carlossilvadev.projeto_integrador_web_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationDTO {
	
	@NotBlank(message = "Login é obrigatório")
	@JsonProperty("login")
	private String username;
	
	@NotBlank(message = "Senha é obrigatória")
	@JsonProperty("senha")
	private String password;
	
	// getters e setters
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
}
