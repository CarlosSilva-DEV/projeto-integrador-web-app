package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.UserDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@Service // registro da clase como um componente "Service" do Spring
public class UserService {
	@Autowired
	private UserRepository repository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	// métodos Service que recuperam objetos no Repository (admin-only)
	public List<UserDTO> findAll() {
		List<User> users = repository.findAll();
		return users.stream().map(UserDTO::new).collect(Collectors.toList());
	}
	
	public UserDTO findById(Long id) {
		Optional<User> obj = repository.findById(id);
		User user = obj.orElseThrow(() -> new ResourceNotFoundException(id)); // exceção para id inexistente
		return new UserDTO(user);
	}
	
	// método Service para inserir novo user (admin-only)
	public UserDTO insert(UserDTO userDto) {
		User user = new User(userDto);
		user.setSenha(passwordEncoder.encode(userDto.getSenha()));
		user.setRole(userDto.getRole());
		return new UserDTO(repository.save(user));
	}
	
	// método Service que deleta objeto no Repository (admin-only)
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
	public UserDTO update(Long id, UserDTO userDto) {
		try {
			User entity = repository.getReferenceById(id); // cria objeto monitorado pelo JPA que vai armazenar os novos dados
			updateData(entity, userDto);
			entity.setSenha(passwordEncoder.encode(userDto.getSenha()));
			User updatedUser = repository.save(entity);
			return new UserDTO(updatedUser);
		} catch (EntityNotFoundException exception) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void updateData(User entity, UserDTO obj) {
		entity.setNome(obj.getNome());
		entity.setLogin(obj.getLogin());
		entity.setEmail(obj.getEmail());
		entity.setTelefone(obj.getTelefone());
		entity.setSenha(obj.getSenha());
	}
}