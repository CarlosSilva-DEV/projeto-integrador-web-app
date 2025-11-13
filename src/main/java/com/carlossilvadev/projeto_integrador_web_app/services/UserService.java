package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	
	// método auxiliar
	public User getCurrentUserEntity() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			throw new RuntimeException("Usuário não autenticado");
		}
		
		String username = authentication.getName();
        return repository.findByLogin(username).orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + username));
	}
	
	// método para retornar usuário atual logado
	public UserDTO getCurrentUser() {
		User currentUser = getCurrentUserEntity();
		return new UserDTO(currentUser);
	}
	
	public UserDTO updateCurrentUser(UserDTO userDto) {
		User currentUser = getCurrentUserEntity();
		currentUser.setNome(userDto.getNome());
        currentUser.setEmail(userDto.getEmail());
        currentUser.setTelefone(userDto.getTelefone());
        
        if (userDto.getSenha() != null && !userDto.getSenha().isEmpty()) {
            currentUser.setSenha(passwordEncoder.encode(userDto.getSenha()));
        }
        
        User updatedUser = repository.save(currentUser);
        return new UserDTO(updatedUser);
	}
	
	public void deleteCurrentUser() {
		User currentUser = getCurrentUserEntity();
		repository.deleteById(currentUser.getId());
	}
	
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	
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
		entity.setRole(obj.getRole());
	}
}