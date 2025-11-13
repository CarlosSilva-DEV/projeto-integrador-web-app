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

import com.carlossilvadev.projeto_integrador_web_app.dto.OrderCreateDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.UserDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.OrderService;
import com.carlossilvadev.projeto_integrador_web_app.services.UserService;

@RestController
@RequestMapping(value = "/users")
public class UserResource {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	//============================ ENDPOINTS USUÁRIOS ==========================================================================
	
	// método que retorna usuário atual logado (users & admins)
	@GetMapping("/profile")
	public ResponseEntity<UserDTO> getCurrentUser() {
		UserDTO userDto = userService.getCurrentUser();
		return ResponseEntity.ok().body(userDto);
	}
	
	@PutMapping("/profile")
	public ResponseEntity<UserDTO> updateCurrentUser(@RequestBody UserDTO userDto) {
		UserDTO updatedUser = userService.updateCurrentUser(userDto);
		return ResponseEntity.ok().body(updatedUser);
	}
	
	@DeleteMapping("/profile")
	public ResponseEntity<Void> deleteCurrentUser() {
		userService.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}
	
	// requisições relacionadas ao pedido do usuário
	@GetMapping("/profile/orders")
	public ResponseEntity<List<OrderDTO>> findOrdersByCurrentUser() {
		List<OrderDTO> orders = orderService.findOrdersByCurrentUser();
		return ResponseEntity.ok().body(orders);
	}
	
	@GetMapping("/profile/orders/{id}")
	public ResponseEntity<OrderDTO> findOrderByIdAndCurrentUser(@PathVariable Long id) {
		OrderDTO order = orderService.findByIdAndCurrentUser(id);
		return ResponseEntity.ok().body(order);
	}
	
	@PostMapping("/profile/orders")
	public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreateDTO orderCreateDto) {
		OrderDTO newOrder = orderService.createOrder(orderCreateDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.getId()).toUri();
		return ResponseEntity.created(uri).body(newOrder);
	}
	
	@PostMapping("/profile/orders/{id}/cancel")
	public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
		orderService.cancelOrder(id);
		return ResponseEntity.noContent().build();
	}
	
	
	// ============================ ENDPOINTS ADMINISTRATIVOS ==================================================================
	
	// método que retorna lista de usuários (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	public ResponseEntity<List<UserDTO>> findAll() {
		List<UserDTO> lista = userService.findAll();
		return ResponseEntity.ok().body(lista);
	}
	
	// buscar pelo id (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/{id}")
	public ResponseEntity<UserDTO> findById(@PathVariable Long id) {
		UserDTO obj = userService.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	// inserir user manualmente (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	public ResponseEntity<UserDTO> insert(@RequestBody UserDTO obj) {
		UserDTO userDto = userService.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDto.getId()).toUri();
		return ResponseEntity.created(uri).body(userDto);
	}
	
	// deletar user (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	// atualizar user (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO userDto) {
		UserDTO updatedUser = userService.update(id, userDto);
		return ResponseEntity.ok().body(updatedUser);
	}
}