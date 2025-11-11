package com.carlossilvadev.projeto_integrador_web_app.repositories;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	List<Product> findByNomeContainingIgnoreCase(String nome);
	List<Product> findByNomeContaining(String nome);
}