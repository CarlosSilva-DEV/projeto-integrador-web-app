package com.carlossilvadev.projeto_integrador_web_app.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carlossilvadev.projeto_integrador_web_app.dto.PaymentDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.PaymentService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/payments")
@Tag(name = "Pagamentos", description = "Controller responsável pelas requisições em endpoints de Pagamentos")
@SecurityRequirement(name = "bearerAuth")
public class PaymentResource {
	@Autowired
	private PaymentService service;
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping
	@Operation(summary = "Obter todos os Pagamentos", description = "Método responsável por retornar todos os Pagamentos, retorna um List vazio caso não haja nenhum")
	@ApiResponse(responseCode = "200", description = "Pagamentos obtidos com sucesso")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<PaymentDTO>> findAll() {
		List<PaymentDTO> payments = service.findAll();
		return ResponseEntity.ok().body(payments);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{id}")
	@Operation(summary = "Obter um Pagamento por ID", description = "Método responsável por retornar um Pagamento com base em um ID")
	@ApiResponse(responseCode = "200", description = "Pagamento encontrado com sucesso")
	@ApiResponse(responseCode = "404", description = "Pagamento não encontrado")
	@ApiResponse(responseCode = "401", description = "Acesso não autorizado caso o Usuário atual não tenha a role ADMIN")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<PaymentDTO> findById(@PathVariable Long id) {
		PaymentDTO payment = service.findById(id);
		return ResponseEntity.ok().body(payment);
	}
}
