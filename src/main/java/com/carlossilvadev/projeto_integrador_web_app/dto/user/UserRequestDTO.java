package com.carlossilvadev.projeto_integrador_web_app.dto.user;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserRequestDTO {
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
	
	@NotNull(message = "Role é obrigatória")
	private UserRole role;
	
	// Construtores (copiando propriedades da entidade User para o DTO)
	public UserRequestDTO(User user) {
		this.nome = user.getNome();
		this.login = user.getLogin();
		this.email = user.getEmail();
		this.telefone = user.getTelefone();
		this.senha = user.getSenha();
		this.role = user.getRole();
	}
	
	public UserRequestDTO() {
	}
	
	// getters
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
}