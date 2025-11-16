package com.carlossilvadev.projeto_integrador_web_app.dto;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserUpdateDTO {
	private Long id;
	
	@Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
	private String nome;
	
	@Size(min = 3, max = 15, message = "Login deve ter entre 3 e 15 caracteres")
	private String login;
	
	@Email(message = "Email deve ser válido: email_address@domain.com")
	private String email;
	
	@Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$", message = "Telefone deve estar no formato: (XX) XXXXX-XXXX")
	private String telefone;
	
	@Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	private String senha;
	
	private UserRole role;
	
	// Construtores (copiando propriedades da entidade User para o DTO)
	public UserUpdateDTO(User user) {
		BeanUtils.copyProperties(user, this);
	}
	
	public UserUpdateDTO() {
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
	
	public String getEmail() {
		return email;
	}
	
	public String getTelefone() {
		return telefone;
	}
	
	public String getSenha() {
		return senha;
	}
	
	public UserRole getRole() {
		return role;
	}
	
	// métodos has (update de usuário)
	public boolean hasNome() {
		return nome != null;
	}
	
	public boolean hasLogin() {
		return login != null;
	}
	
	public boolean hasEmail() {
		return email != null;
	}
	
	public boolean hasTelefone() {
		return telefone != null;
	}
	
	public boolean hasSenha() {
		return senha != null;
	}
	
	public boolean hasRole() {
		return role != null;
	}
}