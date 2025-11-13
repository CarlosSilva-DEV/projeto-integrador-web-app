package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.ProductDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.carlossilvadev.projeto_integrador_web_app.repositories.CategoryRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.ProductRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	public List<ProductDTO> findAll() {
		List<Product> products = productRepository.findAllWithCategories();
		return products.stream().map(ProductDTO::new).collect(Collectors.toList());
	}
	
	public ProductDTO findById(Long id) {
		Optional<Product> obj = productRepository.findByIdWithCategories(id);
		Product product = obj.orElseThrow(() -> new ResourceNotFoundException(id));
		return new ProductDTO(product);
	}
	
	// realizar busca (com ou sem params)
	public List<ProductDTO> search(String searchTerm) {
		if (searchTerm == null || searchTerm.trim().isEmpty()) {
			return findAll();
		}
		return findByNome(searchTerm.trim());
	}
	
	// método auxiliar para buscar nome do produto no repositório
	public List<ProductDTO> findByNome(String nome) {
		List<Product> products = productRepository.findByNomeContainingIgnoreCase(nome);
		return products.stream().map(ProductDTO::new).collect(Collectors.toList());
	}
	
	public List<ProductDTO> findByCategory(Long categoryId) {
		List<Product> products = productRepository.findByCategoryId(categoryId);
		return products.stream().map(ProductDTO::new).collect(Collectors.toList());
	}
	
	public List<ProductDTO> findWithFilter(Long categoryId, String searchTerm) {
		List<Product> products;

		if (categoryId != null && searchTerm != null && !searchTerm.trim().isEmpty()) {
			products = productRepository.findByCategoryAndSearch(categoryId, searchTerm.trim());
		} else if (categoryId != null) {
			products = productRepository.findByCategoryId(categoryId);
		} else if (searchTerm != null && !searchTerm.trim().isEmpty()) {
			products = productRepository.findByNomeContainingIgnoreCase(searchTerm.trim());
		} else {
			products = productRepository.findAllWithCategories();
		}
		
		return products.stream().map(ProductDTO::new).collect(Collectors.toList());
	}
	
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	public ProductDTO insert(ProductDTO productDto) {
		Product product = new Product();
		product.setNome(productDto.getNome());
		product.setDescricao(productDto.getDescricao());
		product.setPreco(productDto.getPreco());
		product.setImgUrl(productDto.getImgUrl());
		
		if (productDto.getCategories() != null) {
			for (Category category : productDto.getCategories()) {
				Category managedCategory = categoryRepository.findById(category.getId())
						.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
				product.getCategories().add(managedCategory);
			}
		}
		
		return new ProductDTO(productRepository.save(product));
	}
	
	public void delete(Long id) {
		try {
			productRepository.deleteById(id);
		} catch (EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException exception) {
			throw new DatabaseException(exception.getMessage());
		}
	}
	
	public ProductDTO update(Long id, ProductDTO productDto) {
		try {
			Product entity = productRepository.getReferenceById(id);
			updateData(entity, productDto);
			
			Product updatedProduct = productRepository.save(entity);
			return new ProductDTO(updatedProduct);
		} catch (EntityNotFoundException exception) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void updateData(Product entity, ProductDTO obj) {
		entity.setNome(obj.getNome());
		entity.setDescricao(obj.getDescricao());
		entity.setPreco(obj.getPreco());
		entity.setImgUrl(obj.getImgUrl());
		
		if (obj.getCategories() != null) {
			for (Category category : obj.getCategories()) {
				Category managedCategory = categoryRepository.findById(category.getId())
						.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada"));
				obj.getCategories().add(managedCategory);
			}
		}
	}
}