package com.carlossilvadev.projeto_integrador_web_app.resources;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderCreateDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.payment.PaymentDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserRequestDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserResponseDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserUpdateDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.OrderService;
import com.carlossilvadev.projeto_integrador_web_app.services.PaymentService;
import com.carlossilvadev.projeto_integrador_web_app.services.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/users")
@Tag(name = "Usuario", description = "Controller responsável pelas requisições relacionadas a um Usuário")
@SecurityRequirement(name = "bearerAuth")
public class UserResource {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private OrderService orderService;
	
	@Autowired
	private PaymentService paymentService;
	
	//============================ ENDPOINTS USUÁRIOS ==========================================================================
	
	// método que retorna usuário atual logado (users & admins)
	@GetMapping("/profile")
	@Operation(summary = "Obtém o Usuário atual", description = "Método responsável por obter o Usuário atualmente autenticado")
	@ApiResponse(responseCode = "200", description = "Usuário obtido com sucesso")
	@ApiResponse(responseCode = "401", description = "Usuário não autenticado")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<UserResponseDTO> getCurrentUser() {
		UserResponseDTO userDto = userService.getCurrentUserDTO();
		return ResponseEntity.ok().body(userDto);
	}
	
	@PatchMapping("/profile")
	@Operation(summary = "Atualiza dados do Usuário atual", description = "Método responsável por atualizar os dados do Usuário atualmente autenticado")
	@ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
	@ApiResponse(responseCode = "409", description = "Endereço de e-mail fornecido já é utilizado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<UserResponseDTO> updateCurrentUser(@Valid @RequestBody UserUpdateDTO userUpdateDto) {
		UserResponseDTO updatedUser = userService.updateCurrentUser(userUpdateDto);
		return ResponseEntity.ok().body(updatedUser);
	}
	
	@DeleteMapping("/profile")
	@Operation(summary = "Deleta o Usuário atual", description = "Método responsável por deletar o Usuário atualmente autenticado")
	@ApiResponse(responseCode = "204", description = "Usuário excluído com sucesso")
	@ApiResponse(responseCode = "409", description = "Não é possível excluir um Usuário que possua Pedidos vínculados a ele")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<Void> deleteCurrentUser() {
		userService.deleteCurrentUser();
		return ResponseEntity.noContent().build();
	}
	
	//////////////////////////////////// requisições relacionadas ao pedido do usuário
	
	@GetMapping("/profile/orders")
	@Operation(summary = "Obtém todos os Pedidos do Usuário atual", description = "Método que retorna todos os Pedidos do Usuário atualmente autenticado, retorna um List vazio caso não haja nenhum")
	@ApiResponse(responseCode = "200", description = "Pedidos do Usuário atual obtidos com sucesso")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<OrderDTO>> findOrdersByCurrentUser() {
		List<OrderDTO> orders = orderService.findOrdersByCurrentUser();
		return ResponseEntity.ok().body(orders);
	}
	
	@GetMapping("/profile/orders/{id}")
	@Operation(summary = "Obtém Pedido do Usuário atual por ID", description = "Método que retorna um Pedido do Usuário atual com base em um ID")
	@ApiResponse(responseCode = "200", description = "Pedido obtido com sucesso")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<OrderDTO> findOrderByIdAndCurrentUser(@PathVariable Long id) {
		OrderDTO order = orderService.findByIdAndCurrentUser(id);
		return ResponseEntity.ok().body(order);
	}
	
	@PostMapping("/profile/orders")
	@Operation(summary = "Criar um Pedido para o Usuário atual", description = "Método que cria um Pedido para o Usuário atual com base em uma lista de Produtos")
	@ApiResponse(responseCode = "201", description = "Pedido criado com sucesso")
	@ApiResponse(responseCode = "404", description = "Produto não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderCreateDTO orderCreateDto) {
		OrderDTO newOrder = orderService.createOrder(orderCreateDto);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(newOrder.getId()).toUri();
		return ResponseEntity.created(uri).body(newOrder);
	}
	
	@PostMapping("/profile/orders/{id}/cancel")
	@Operation(summary = "Cancelar um Pedido do Usuário atual por ID", description = "Método que cancela um Pedido do Usuário atual com base em um ID")
	@ApiResponse(responseCode = "200", description = "Pedido cancelado com sucesso")
	@ApiResponse(responseCode = "409", description = "Não é possível cancelar um Pedido que já foi pago")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<OrderDTO> cancelOrder(@PathVariable Long id) {
		OrderDTO orderDto = orderService.cancelOrder(id);
		return ResponseEntity.ok().body(orderDto);
	}
	
	//////////////////////////////////// requisições relacionadas ao pagamento dos pedidos do usuário
	
	@PostMapping("/profile/orders/{id}/payment")
	@Operation(summary = "Cria um Pagamento para um Pedido do Usuário atual por ID", description = "Método que cria um Pagamento para um Pedido do Usuário atual com base em seu ID")
	@ApiResponse(responseCode = "200", description = "Pagamento criado com sucesso")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<PaymentDTO> createOrderPayment(@PathVariable("id") Long orderId) {
		PaymentDTO payment = paymentService.createPayment(orderId);
		return ResponseEntity.ok(payment);
	}
	
	@PostMapping("/profile/orders/{id}/payment/confirm")
	@Operation(summary = "Confirma um Pagamento para um Pedido do Usuário atual por ID", description = "Método que confirma um Pagamento para um Pedido do Usuário atual com base em seu ID")
	@ApiResponse(responseCode = "200", description = "Usuário obtido com sucesso")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<OrderDTO> confirmOrderPayment(@PathVariable("id") Long orderId) {
		OrderDTO order = paymentService.confirmPayment(orderId);
		return ResponseEntity.ok(order);
	}
	
	
	// ============================ ENDPOINTS ADMINISTRATIVOS ==================================================================
	
	// método que retorna lista de usuários (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	@Operation(summary = "Obtém todos os Usuários", description = "Método responsável por retornar todos os Usuários, retorna um List vazio caso não haja nenhum")
	@ApiResponse(responseCode = "200", description = "Usuários obtidos com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<UserResponseDTO>> findAll() {
		List<UserResponseDTO> lista = userService.findAll();
		return ResponseEntity.ok().body(lista);
	}
	
	// buscar pelo id (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/{id}")
	@Operation(summary = "Obtém um Usuário por ID", description = "Método responsável por retornar um Usuário com base em um ID")
	@ApiResponse(responseCode = "200", description = "Usuário obtido com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<UserResponseDTO> findById(@PathVariable Long id) {
		UserResponseDTO obj = userService.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	// inserir user manualmente (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	@Operation(summary = "Insere um novo Usuário", description = "Método responsável por criar um novo Usuário manualmente (sem cadastro)")
	@ApiResponse(responseCode = "201", description = "Usuário criado com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "409", description = "Não é possível criar um Usuário com um login ou email já utilizado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<UserResponseDTO> insert(@Valid @RequestBody UserRequestDTO obj) {
		UserResponseDTO userDto = userService.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(userDto.getId()).toUri();
		return ResponseEntity.created(uri).body(userDto);
	}
	
	// deletar user (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Deleta um Usuário por ID", description = "Método responsável por deletar um Usuário com base em um ID")
	@ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@ApiResponse(responseCode = "409", description = "Não é possível excluir um Usuário que possa Pedidos vinculados a ele")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		userService.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	// atualizar user (admin-only)
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(value = "/{id}")
	@Operation(summary = "Atualiza dados de um Usuário por ID", description = "Método responsável por atualizar dados de um Usuário com base em um ID")
	@ApiResponse(responseCode = "200", description = "Usuário atualizado com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "404", description = "Usuário não encontrado")
	@ApiResponse(responseCode = "409", description = "Não é possível incluir um endereço de e-mail utilizado por outro Usuário")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO userUpdateDto) {
		UserResponseDTO updatedUser = userService.update(id, userUpdateDto);
		return ResponseEntity.ok().body(updatedUser);
	}
}