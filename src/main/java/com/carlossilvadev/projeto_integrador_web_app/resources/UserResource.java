package com.carlossilvadev.projeto_integrador_web_app.resources;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.http.ResponseEntity;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;


@RestController // anotação que define a classe como um controlador Rest (camada de recursos)
@RequestMapping(value = "/users") // define o caminho do recurso
public class UserResource {
	@GetMapping // indica que o método responde requisições Get no caminho Users do HTTP
	public ResponseEntity<User> findAll() {
		User u = new User(1L, "Maria", "maria@gmail.com", "999999999", "12345"); // objeto para teste de requisição
		return ResponseEntity.ok().body(u); // retornando o objeto
	}
}
