package com.carlossilvadev.projeto_integrador_web_app.resources;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.carlossilvadev.projeto_integrador_web_app.dto.PaymentDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.PaymentService;

@RestController
@RequestMapping("/payments")
public class PaymentResource {
	@Autowired
	private PaymentService service;
	
	@GetMapping
	public ResponseEntity<List<PaymentDTO>> findAll() {
		List<PaymentDTO> payments = service.findAll();
		return ResponseEntity.ok().body(payments);
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<PaymentDTO> findById(@PathVariable Long id) {
		PaymentDTO payment = service.findById(id);
		return ResponseEntity.ok().body(payment);
	}
}
