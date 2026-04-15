package com.carlossilvadev.projeto_integrador_web_app.dto.auth;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

public class AuthenticationDTO {
	
	@NotBlank(message = "Login é obrigatório")
	@JsonProperty("login")
	private String username;
	
	@NotBlank(message = "Senha é obrigatória")
	@JsonProperty("senha")
	private String password;
	
	// getters
	public String getUsername() {
		return username;
	}
	
	public String getPassword() {
		return password;
	}
}
