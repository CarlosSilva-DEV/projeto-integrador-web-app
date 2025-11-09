package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.CategoryDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.repositories.CategoryRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service
public class CategoryService {
	@Autowired
	private CategoryRepository repository;
	
	public List<CategoryDTO> findAll() {
		List<Category> categories = repository.findAll();
		return categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
	}
	
	public CategoryDTO findById(Long id) {
		Optional<Category> obj = repository.findById(id);
		Category category = obj.orElseThrow(() -> new ResourceNotFoundException(id));
		return new CategoryDTO(category);
	}
	
	public CategoryDTO insert(CategoryDTO categoryDto) {
		Category category = new Category(categoryDto);
		return new CategoryDTO(repository.save(category));
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
	
	public CategoryDTO update(Long id, CategoryDTO categoryDto) {
		try {
			Category entity = repository.getReferenceById(id);
			updateData(entity, categoryDto);
			Category updatedCategory = repository.save(entity);
			return new CategoryDTO(updatedCategory);
		} catch (EntityNotFoundException exception) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void updateData(Category entity, CategoryDTO obj) {
		entity.setNome(obj.getNome());
	}
}