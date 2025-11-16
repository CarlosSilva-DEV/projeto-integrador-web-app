package com.carlossilvadev.projeto_integrador_web_app.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>{
	
	// métodos de busca com join fetch
	@Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product WHERE o.client = :client")
	List<Order> findByClientWithItems(@Param("client") User client);
	
	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product WHERE o.id = :id AND o.client = :client")
	Optional<Order> findByIdAndClientWithItems(@Param("id") Long id, @Param("client") User client);
	
	@Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product")
	List<Order> findAllWithItems();
	
	@Query("SELECT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product WHERE o.id = :id")
	Optional<Order> findByIdWithItems(@Param("id") Long id);
	
	
	// métodos de busca (filtros)
	@Query("SELECT o FROM Order o WHERE o.orderStatus IN :statusList")
	List<Order> findByStatusIn(@Param("statusList") List<OrderStatus> statusList);
	
	List<Order> findByOrderStatus(OrderStatus status);
	
	@Query("SELECT COUNT(o) > 0 FROM Order o WHERE o.client = :client")
	boolean existsByClient(@Param("client") User client);
}