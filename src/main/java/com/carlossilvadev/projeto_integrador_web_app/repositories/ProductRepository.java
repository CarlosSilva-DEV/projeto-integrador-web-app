package com.carlossilvadev.projeto_integrador_web_app.repositories;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{
	
	// carregamento de categorias
	@Query("SELECT p FROM Product p LEFT JOIN FETCH p.categories WHERE p.id = :id")
	Optional<Product> findByIdWithCategories(@Param("id") Long id);
	
	@Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories")
	List<Product> findAllWithCategories();
	
	
	// busca de produtos por nome
	List<Product> findByNomeContainingIgnoreCase(String nome);
	List<Product> findByNomeContaining(String nome);
	
	
	// filtros para busca
	@Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories c WHERE c.id = :categoryId")
	List<Product> findByCategoryId(@Param("categoryId") Long categoryId);
	
	@Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories c WHERE c.id IN :categoryIds")
	List<Product> findByCategoryIds(@Param("categoryIds") List<Long> categoryIds);
	
	@Query("SELECT DISTINCT p FROM Product p LEFT JOIN FETCH p.categories c WHERE " +
	           "(:categoryId IS NULL OR c.id = :categoryId) AND " +
	           "(:searchTerm IS NULL OR LOWER(p.nome) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
	           "LOWER(p.descricao) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
	List<Product> findByCategoryAndSearch(
			@Param("categoryId") Long categoryId,
			@Param("searchTerm") String searchTerm);
}