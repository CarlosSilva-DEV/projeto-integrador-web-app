package com.carlossilvadev.projeto_integrador_web_app.resources;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.services.UserService;

@RestController // anotação que define a classe como um controlador Rest (camada de recursos)
@RequestMapping(value = "/users") // define o caminho do recurso
public class UserResource {
	@Autowired
	private UserService service; // injeção de dependência da camada de serviços
	
	@GetMapping // indica que o método responde requisições Get no caminho Users do HTTP
	public ResponseEntity<List<User>> findAll() { // método que retorna lista de usuários
		List<User> lista = service.findAll();
		return ResponseEntity.ok().body(lista);
	}
	
	@GetMapping(value = "/{id}") // definindo que o caminho URL vai receber params de Id
	public  ResponseEntity<User> findById(@PathVariable Long id) {
		User obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
}