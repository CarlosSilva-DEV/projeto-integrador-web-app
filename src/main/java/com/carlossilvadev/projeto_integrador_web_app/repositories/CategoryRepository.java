package com.carlossilvadev.projeto_integrador_web_app.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	Optional<Category> findByNome(String nome);
	
	@Query("SELECT c FROM Category c WHERE LOWER(c.nome) = LOWER(:nome)")
	Optional<Category> findByNomeIgnoreCase(@Param("nome") String nome);
	
	// carregamento de produtos
	@Query("SELECT c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
	Optional<Category> findByIdWithProducts(@Param("id") Long id);
		
	@Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products")
	List<Category> findAllWithProducts();
	
	@Query("SELECT COUNT(p) > 0 FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    boolean existsProductsByCategoryId(@Param("categoryId") Long categoryId);
}