package com.carlossilvadev.projeto_integrador_web_app.dto;

public class AccessDTO {
	private String token;
	
	// Construtor
	public AccessDTO(String token) {
		super();
		this.token = token;
	}
	
	// getters e setters
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
}