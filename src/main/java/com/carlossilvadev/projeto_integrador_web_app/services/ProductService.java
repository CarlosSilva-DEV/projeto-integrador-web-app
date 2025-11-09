package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.ProductDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.carlossilvadev.projeto_integrador_web_app.repositories.ProductRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
	@Autowired
	private ProductRepository repository;
	
	public List<ProductDTO> findAll() {
		List<Product> products = repository.findAll();
		return products.stream().map(ProductDTO::new).collect(Collectors.toList());
	}
	
	public ProductDTO findById(Long id) {
		Optional<Product> obj = repository.findById(id);
		Product product = obj.orElseThrow(() -> new ResourceNotFoundException(id));
		return new ProductDTO(product);
	}
	
	public ProductDTO insert(ProductDTO productDto) {
		Product product = new Product(productDto);
		return new ProductDTO(repository.save(product));
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException exception) {
			throw new DatabaseException(exception.getMessage());
		}
	}
	
	public ProductDTO update(Long id, ProductDTO productDto) {
		try {
			Product entity = repository.getReferenceById(id);
			updateData(entity, productDto);
			Product updatedProduct = repository.save(entity);
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
	}
}