package com.carlossilvadev.projeto_integrador_web_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.PaymentStatus;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
	Optional<Payment> findByOrder(Order order);
	
	@Query("SELECT p FROM Payment p WHERE p.order.id = :orderId")
	Optional<Payment> findByOrderId(@Param("orderId") Long orderId);
	
	List<Payment> findByStatus(PaymentStatus status);
	
	@Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.order o LEFT JOIN FETCH o.client WHERE p.order IS NOT NULL")
	List<Payment> findAllWithOrdersAndClients();
	
	@Query("SELECT p FROM Payment p LEFT JOIN FETCH p.order o LEFT JOIN FETCH o.client WHERE p.id = :id")
	Optional<Payment> findByIdWithOrdersAndClients(@Param("id") Long id);
}