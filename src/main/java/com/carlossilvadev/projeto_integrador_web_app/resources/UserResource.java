package com.carlossilvadev.projeto_integrador_web_app.resources;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlossilvadev.projeto_integrador_web_app.dto.UserDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
	
	@Autowired
	private UserService service;
	
	//============================ ENDPOINTS USUÁRIOS ==========================================================================
	
	// método que retorna usuário atual logado (users & admins)
	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getCurrentUser() {
		UserDTO userDto = service.getCurrentUser();
		return ResponseEntity.ok().body(userDto);
	}
	
	@PutMapping("/profile")
	public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserDTO userDto) {
		UserDTO updatedUser = service.updateCurrentUser(userDto);
		return ResponseEntity.ok().body(updatedUser);
	}
	
	@DeleteMapping("/profile")
	public ResponseEntity<Void> deleteCurrentUser() {
		service.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}
	
	// ============================ ENDPOINTS ADMINISTRATIVOS ==================================================================
	
	// método que retorna lista de usuários (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<UserDTO>> findAll() {
		List<UserDTO> lista = service.findAll();
		return ResponseEntity.ok().body(lista);
	}
	
	// buscar pelo id (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		UserDTO obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	// inserir user manualmente (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<UserDTO> insert(@RequestBody UserDTO obj) {
		UserDTO userDto = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDto.getId()).toUri();
		return ResponseEntity.created(uri).body(userDto);
	}
	
	// deletar user (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	// atualizar user (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDto) {
		UserDTO updatedUser = service.update(id, userDto);
		return ResponseEntity.ok().body(updatedUser);
	}
}