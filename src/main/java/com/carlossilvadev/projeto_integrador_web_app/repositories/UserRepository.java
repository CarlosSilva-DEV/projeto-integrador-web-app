package com.carlossilvadev.projeto_integrador_web_app.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;

@Repository // registro como componente do Spring, anotação não obrigatória por conta da herança JpaRepository abaixo
public interface UserRepository extends JpaRepository<User, Long>{ // extende jparepository<entidade, ID>
	
	Optional<User> findByLogin(String login);
	Optional<User> findByEmail(String email);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product WHERE u.id = :id")
	Optional<User> findByIdWithOrders(@Param("id") Long id);
	
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.orders o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.id.product")
	List<User> findAllWithOrders();
}