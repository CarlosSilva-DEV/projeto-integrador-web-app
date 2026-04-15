package com.carlossilvadev.projeto_integrador_web_app.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class RegisterDTO {
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
}