package com.carlossilvadev.projeto_integrador_web_app.dto;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserDTO {
	private Long id;
	
	@NotBlank(message = "Nome é obrigatório")
	@Size(min = 2, max = 50, message = "Nome deve ter entre 2 e 50 caracteres")
	private String nome;
	
	@NotBlank(message = "Login é obrigatório")
	@Size(min = 3, max = 15, message = "Login deve ter entre 3 e 15 caracteres")
	private String login;
	
	@NotBlank(message = "Email é obrigatório")
	@Email(message = "Email deve ser válido: email_address@domain.com")
	private String email;
	
	@NotBlank(message = "Telefone é obrigatório")
	@Pattern(regexp = "^\\(\\d{2}\\)\\s\\d{4,5}-\\d{4}$", message = "Telefone deve estar no formato: (XX) XXXXX-XXXX")
	private String telefone;
	
	@NotBlank(message = "Senha é obrigatória")
	@Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
	private String senha;
	
	private UserRole role;
	
	// Construtores (copiando propriedades da entidade User para o DTO)
	public UserDTO(User user) {
		BeanUtils.copyProperties(user, this);
	}
	
	public UserDTO() {
	}
	
	// getters e setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}
	
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	public UserRole getRole() {
		return role;
	}
	public void setRole(UserRole role) {
		this.role = role;
	}
}