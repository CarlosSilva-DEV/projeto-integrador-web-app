package com.carlossilvadev.projeto_integrador_web_app.resources;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping(value = "/orders")
@Tag(name = "Pedidos", description = "Controller responsável pelas requisições em endpoints de Pedidos")
@SecurityRequirement(name = "bearerAuth")
public class OrderResource {
	@Autowired
	private OrderService service;
	
	// ============================ ENDPOINTS ADMINISTRATIVOS ==================================================================
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	@Operation(summary = "Obter todos os Pedidos", description = "Método responsável por retornar todos os Pedidos, retorna um List vazio caso não haja nenhum")
	@ApiResponse(responseCode = "200", description = "Pedidos obtidos com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<OrderDTO>> findAll() {
		List<OrderDTO> lista = service.findAll();
		return ResponseEntity.ok().body(lista);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping(value = "/{id}")
	@Operation(summary = "Obter um Pedido por ID", description = "Método responsável por retornar um Pedido com base em um ID")
	@ApiResponse(responseCode = "200", description = "Pedido obtido com sucesso")
	@ApiResponse(responseCode = "404", description = "Pedido não encontrado")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<OrderDTO> findById(@PathVariable Long id) {
		OrderDTO obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
}