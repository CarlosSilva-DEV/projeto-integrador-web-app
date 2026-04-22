package com.carlossilvadev.projeto_integrador_web_app.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long>{
	@Query("SELECT DISTINCT p FROM Payment p LEFT JOIN FETCH p.order o LEFT JOIN FETCH o.client LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product WHERE p.order IS NOT NULL")
	List<Payment> findAllWithOrdersAndClients();
	
	@Query("SELECT p FROM Payment p LEFT JOIN FETCH p.order o LEFT JOIN FETCH o.client LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product WHERE p.id = :id")
	Optional<Payment> findByIdWithOrdersAndClients(@Param("id") Long id);
}