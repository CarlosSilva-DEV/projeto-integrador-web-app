package com.carlossilvadev.projeto_integrador_web_app.services;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.PaymentDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.PaymentStatus;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.PaymentRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

@Service
public class PaymentService {
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private PaymentRepository paymentRepository;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	public PaymentDTO createPayment(Long orderId) {
		User currentUser = userService.getCurrentUserEntity();
		Order order = orderRepository.findByIdAndClientWithItems(orderId, currentUser)
				.orElseThrow(() -> new ResourceNotFoundException(orderId));
		
		Optional<Payment> existingPayment = paymentRepository.findByOrderId(orderId);
		
		if (existingPayment.isPresent()) {
			Payment payment = existingPayment.get();
			String qrCode = generatePixQrCode(order);
			String copiaCola = generatePixCopiaCola(order);
			return new PaymentDTO(payment, qrCode, copiaCola);
		}
		
		Payment payment = new Payment(order);
		Payment savedPayment = paymentRepository.save(payment);
		
		order.setPayment(savedPayment);
		order.setOrderStatus(OrderStatus.PROCESSANDO_PAGAMENTO);
		orderRepository.save(order);
		
		String qrCode = generatePixQrCode(order);
		String copiaCola = generatePixCopiaCola(order);
		return new PaymentDTO(savedPayment, qrCode, copiaCola);
	}
	
	public OrderDTO confirmPayment(Long orderId) {
		User currentUser = userService.getCurrentUserEntity();
		Order order = orderRepository.findByIdAndClientWithItems(orderId, currentUser)
				.orElseThrow(() -> new ResourceNotFoundException(orderId));
		
		order.setOrderStatus(OrderStatus.PAGO);
		if (order.getPayment() != null) {
			order.getPayment().setStatus(PaymentStatus.PAGO);
		}
		
		Order updatedOrder = orderRepository.save(order);
		return new OrderDTO(updatedOrder);
	}
	
	
	// métodos auxiliares (gerar códigos Pix e cálculo CRC16)
	private String generatePixQrCode(Order order) {
		String payload = String.format(
				"00020126580014BR.GOV.BCB.PIX0136123e4567-e89b-12d3-a456-4266141740005204000053039865406%.2f5802BR5901%6001%62070503***6304",
				order.getTotal(),
				"MINHA_LOJA"
				);
		
		String crc = calculateCRC16(payload);
		return payload + crc;
	}
	
	private String generatePixCopiaCola(Order order) {
		String payload = String.format(
				"00020126580014BR.GOV.BCB.PIX0136123e4567-e89b-12d3-a456-4266141740005204000053039865406%.2f5802BR5901%6001%62070503***6304",
				order.getTotal(), "MINHA_LOJA"
				);
		
		String crc = calculateCRC16(payload);
		return payload + crc;
	}
	
	private String calculateCRC16(String payload) {
		 int crc = 0xFFFF; // Valor inicial para CRC-16/CCITT-FALSE
	        int polynomial = 0x1021; // Polinômio para CRC-16/CCITT-FALSE
	        
	        byte[] bytes = payload.getBytes(StandardCharsets.UTF_8);
	        
	        for (byte b : bytes) {
	            for (int i = 0; i < 8; i++) {
	                boolean bit = ((b >> (7 - i) & 1) == 1);
	                boolean c15 = ((crc >> 15 & 1) == 1);
	                crc <<= 1;
	                
	                if (c15 ^ bit) {
	                    crc ^= polynomial;
	                }
	            }
	        }
	        
	        crc &= 0xFFFF; // Garante que fique em 16 bits
	        
	        // Formata para 4 caracteres hexadecimais em maiúsculo
	        return String.format("%04X", crc);
	}
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	public List<PaymentDTO> findAll() {
		List<Payment> payments = paymentRepository.findAllWithOrdersAndClients();
		return payments.stream().map(PaymentDTO::new).collect(Collectors.toList());
	}
	
	public PaymentDTO findById(Long id) {
		Payment payment = paymentRepository.findByIdWithOrdersAndClients(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		return new PaymentDTO(payment);
	}
	
	public Payment findPaymentByOrderId(Long orderId) {
		return paymentRepository.findByOrderId(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Pagamento não encontrado para o pedido: " + orderId));
	}
	
	public PaymentDTO updatedPaymentStatus(Long paymentId, PaymentStatus newStatus) {
		Payment payment = paymentRepository.findById(paymentId)
				.orElseThrow(() -> new ResourceNotFoundException(paymentId));
		
		payment.setStatus(newStatus);
		Payment updatedPayment = paymentRepository.save(payment);
		
		return new PaymentDTO(updatedPayment, null, null);
	}
}