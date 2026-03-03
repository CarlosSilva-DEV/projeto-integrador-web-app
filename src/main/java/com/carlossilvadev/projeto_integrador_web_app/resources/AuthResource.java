package com.carlossilvadev.projeto_integrador_web_app.resources;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlossilvadev.projeto_integrador_web_app.dto.AccessDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.AuthenticationDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.RegisterDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.UserDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/auth")
@CrossOrigin
@Tag(name = "Autenticação", description = "Controller responsável pelas requisições de autenticações de usuários (Login e Cadastro)")
public class AuthResource {
	
	@Autowired
	private AuthService authService;
	
	// endpoint de login
	@PostMapping(value = "/login")
	@Operation(summary = "Realizar login de Usuário", description = "Método responsável pela autenticação de login de um Usuário")
	@ApiResponse(responseCode = "200", description = "Login efetuado com sucesso")
	@ApiResponse(responseCode = "401", description = "Credenciais inválidas")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<AccessDTO> login(@Valid @RequestBody AuthenticationDTO authDto) {
		AccessDTO token = authService.login(authDto);
		return ResponseEntity.ok(token);
	}
	
	// endpoint de registro de novo user
	@PostMapping(value = "/register")
	@Operation(summary = "Cadastrar um novo Usuário", description = "Método responsável por cadastrar um novo Usuário")
	@ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso")
	@ApiResponse(responseCode = "409", description = "Não é possível cadastrar um novo Usuário com login ou email já utilizados")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterDTO registerDto) {
		UserDTO newUser = authService.register(registerDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId()).toUri();
		return ResponseEntity.created(uri).body(newUser);
	}
}