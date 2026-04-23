package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserRequestDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserResponseDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserUpdateDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.BusinessException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.UserNotAuthenticatedException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.UserNotFoundException;

@Service // registro da clase como um componente "Service" do Spring
public class UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	
	// método auxiliar
	public User getCurrentUserEntity() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication == null) {
			throw new UserNotAuthenticatedException("Nenhuma autenticação encontrada no contexto de segurança");
		}
		
		if (!authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
			throw new UserNotAuthenticatedException("Usuário não autenticado");
		}
		
		String username = authentication.getName();
		
        return userRepository.findByLogin(username)
        		.orElseThrow(() -> new UserNotFoundException("Usuário não encontrado: " + username));
	}
	
	// método para retornar usuário atual logado
	public UserResponseDTO getCurrentUserDTO() {
		User currentUser = getCurrentUserEntity();
		return new UserResponseDTO(currentUser);
	}
	
	public UserResponseDTO updateCurrentUser(UserUpdateDTO userUpdateDto) {
		User currentUser = getCurrentUserEntity();
		
		if (userUpdateDto.hasNome()) {
			currentUser.setNome(userUpdateDto.getNome());
		}
		
		if (userUpdateDto.hasEmail()) {
			if (!currentUser.getEmail().equals(userUpdateDto.getEmail()) && userRepository.findByEmail(userUpdateDto.getEmail()).isPresent()) {
				throw new BusinessException("Endereço de email já está em uso");
			} else {
				currentUser.setEmail(userUpdateDto.getEmail());
			}
		}
		
		if (userUpdateDto.hasTelefone()) {
			currentUser.setTelefone(userUpdateDto.getTelefone());
		}
        
        if (userUpdateDto.hasSenha()) {
        	currentUser.setSenha(passwordEncoder.encode(userUpdateDto.getSenha()));
        }
        
        User updatedUser = userRepository.save(currentUser);
        return new UserResponseDTO(updatedUser);
	}
	
	public void deleteCurrentUser() {
		User currentUser = getCurrentUserEntity();
		
		if (!currentUser.getOrders().isEmpty()) {
			throw new BusinessException("Não é possível excluir um Usuário que tenha Pedidos vinculados a ele");
		}
		
		userRepository.deleteById(currentUser.getId());
	}
	
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	
	// métodos Service que recuperam objetos no Repository (admin-only)
	public List<UserResponseDTO> findAll() {
		List<User> users = userRepository.findAllWithOrders();
		return users.stream().map(UserResponseDTO::new).collect(Collectors.toList());
	}
	
	public UserResponseDTO findById(Long id) {
		User user = userRepository.findByIdWithOrders(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: ID " + id));
		return new UserResponseDTO(user);
	}
	
	// método Service para inserir novo user (admin-only)
	public UserResponseDTO insert(UserRequestDTO userDto) {
		if (userDto.getLogin() != null && !userDto.getLogin().isEmpty() && userRepository.findByLogin(userDto.getLogin()).isPresent()) {
			throw new BusinessException("Login já está em uso");
		}
		
		if (userDto.getEmail() != null && !userDto.getEmail().isEmpty() && userRepository.findByEmail(userDto.getEmail()).isPresent()) {
			throw new BusinessException("Email já está em uso");
		}
		
		User user = new User(userDto);
		user.setSenha(passwordEncoder.encode(userDto.getSenha()));
		
		return new UserResponseDTO(userRepository.save(user));
	}
	
	// método Service que deleta objeto no Repository (admin-only)
	public void delete(Long id) {
		User user = userRepository.findByIdWithOrders(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: ID " + id));
		
		if (!user.getOrders().isEmpty()) {
			throw new BusinessException("Não é possível excluir um Usuário que tenha Pedidos vinculados a ele");
		}
		
		userRepository.deleteById(id);
	}
	
	// método Service que atualiza objeto no Repository (Patch)
	public UserDTO update(Long id, UserUpdateDTO userUpdateDto) {
		User entity = userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado: ID " + id));
		updateData(entity, userUpdateDto);
		User updatedUser = userRepository.save(entity);
		return new UserDTO(updatedUser);
	}
	
	private void updateData(User entity, UserUpdateDTO obj) {
		if (obj.hasNome()) {
			entity.setNome(obj.getNome());
		}
		
		if (obj.hasEmail()) {
			if (!entity.getEmail().equals(obj.getEmail()) && userRepository.findByEmail(obj.getEmail()).isPresent()) {
				throw new BusinessException("Endereço de email já está em uso");
			} else {
				entity.setEmail(obj.getEmail());
			}
		}
		
		if (obj.hasTelefone()) {
			entity.setTelefone(obj.getTelefone());
		}
        
        if (obj.hasSenha()) {
        	entity.setSenha(passwordEncoder.encode(obj.getSenha()));
        }
        
        if (obj.hasRole()) {
    		entity.setRole(obj.getRole());
        }
	}
}