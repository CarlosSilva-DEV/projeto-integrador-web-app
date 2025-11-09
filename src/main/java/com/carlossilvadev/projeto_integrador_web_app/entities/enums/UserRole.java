package com.carlossilvadev.projeto_integrador_web_app.entities.enums;

public enum UserRole {
	ROLE_ADMIN("ROLE_ADMIN"),
	ROLE_USER("ROLE_USER");
	
	private String role;
	
	// Construtor
	UserRole(String role) {
		this.role = role;
	}
	
	// getters e setters
	public String getRole() {
		return role;
	}
}