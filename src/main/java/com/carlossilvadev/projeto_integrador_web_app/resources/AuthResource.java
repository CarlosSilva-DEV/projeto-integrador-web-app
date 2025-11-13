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


@RestController
@RequestMapping("/auth")
@CrossOrigin
public class AuthResource {
	
	@Autowired
	private AuthService authService;
	
	// endpoint de login
	@PostMapping(value = "/login")
	public ResponseEntity<AccessDTO> login(@RequestBody AuthenticationDTO authDto) {
		AccessDTO token = authService.login(authDto);
		return ResponseEntity.ok(token);
	}
	
	// endpoint de registro de novo user
	@PostMapping(value = "/register")
	public ResponseEntity<?> register(@RequestBody RegisterDTO registerDto) {
		UserDTO newUser = authService.register(registerDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newUser.getId()).toUri();
		return ResponseEntity.created(uri).body(newUser);
	}
}