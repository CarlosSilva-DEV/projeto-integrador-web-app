package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.CategoryDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.repositories.CategoryRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.BusinessException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;


@Service
public class CategoryService {
	@Autowired
	private CategoryRepository repository;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	public List<CategoryDTO> findAll() {
		List<Category> categories = repository.findAllWithProducts();
		return categories.stream().map(CategoryDTO::new).collect(Collectors.toList());
	}
	
	public CategoryDTO findById(Long id) {
		Category category = repository.findByIdWithProducts(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: ID " + id));
		return new CategoryDTO(category);
	}
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	public CategoryDTO insert(CategoryDTO categoryDto) {
		if (categoryDto != null && !categoryDto.getNome().isEmpty() && repository.findByNomeIgnoreCase(categoryDto.getNome()).isPresent()) {
			throw new BusinessException("Categoria com o nome " + categoryDto.getNome() + " já existente");
		}
		
		Category category = new Category(categoryDto);
		return new CategoryDTO(repository.save(category));
	}
	
	public void delete(Long id) {
		repository.findById(id)
			.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: ID " + id));
		
		if (repository.existsProductsByCategoryId(id)) {
			throw new BusinessException("Não é possível excluir uma Categoria que tenha Produtos vínculados a ela");
		}
		
		repository.deleteById(id);
	}
	
	public CategoryDTO update(Long id, CategoryDTO categoryDto) {
		Category entity = repository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada: ID " + id));
		updateData(entity, categoryDto);
		Category updatedCategory = repository.save(entity);
		return new CategoryDTO(updatedCategory);
	}
	
	private void updateData(Category entity, CategoryDTO obj) {
		entity.setNome(obj.getNome());
	}
}