package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service // registro da clase como um componente "Service" do Spring
public class UserService {
	@Autowired
	private UserRepository repository;
	
	// métodos Service que recuperam objetos no Repository (Get - All e ById)
	public List<User> findAll() {
		return repository.findAll();
	}
	
	public User findById(Long id) {
		Optional<User> obj = repository.findById(id);
		return obj.orElseThrow(() -> new ResourceNotFoundException(id)); // exceção para id inexistente
	}
	
	// método Service que salva objeto no Repository (Post)
	public User insert(User obj) {
		return repository.save(obj);
	}
	
	// método Service que deleta objeto no Repository (Delete)
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException exception) { // exceção para id inexistente
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException exception) { // exceção para id com associações
			throw new DatabaseException(exception.getMessage());
		}
	}
	
	// método Service que atualiza objeto no Repository (Put)
	public User update(Long id, User obj) {
		try {
			User entity = repository.getReferenceById(id); // cria objeto monitorado pelo JPA que vai armazenar os novos dados
			updateData(entity, obj);
			return repository.save(entity);
		} catch (EntityNotFoundException exception) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void updateData(User entity, User obj) {
		entity.setNome(obj.getNome());
		entity.setEmail(obj.getEmail());
		entity.setTelefone(obj.getTelefone());
	}
}