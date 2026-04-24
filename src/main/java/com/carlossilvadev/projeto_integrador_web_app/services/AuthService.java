package com.carlossilvadev.projeto_integrador_web_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carlossilvadev.projeto_integrador_web_app.dto.auth.AccessDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.auth.AuthenticationDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.auth.RegisterDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.user.UserResponseDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;
import com.carlossilvadev.projeto_integrador_web_app.security.jwt.JwtUtils;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.BusinessException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.InvalidCredentialsException;

@Service
public class AuthService {
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	public AccessDTO login(AuthenticationDTO authDto) {
		try {
			// cria mecanismo de credencial para o Spring
			UsernamePasswordAuthenticationToken userAuth = new UsernamePasswordAuthenticationToken(authDto.getUsername(), authDto.getPassword());
			
			// prepara mecanismo para autenticação
			Authentication authentication = authenticationManager.authenticate(userAuth);
			
			// busca usuário autenticado
			UserDetailsImpl userAuthenticate = (UserDetailsImpl) authentication.getPrincipal();
			
			String token = jwtUtils.generateTokenFromUserDetailsImpl(userAuthenticate);
			
			return new AccessDTO(token);
			
		} catch (BadCredentialsException exception) {
			throw new InvalidCredentialsException("Credenciais inválidas: " + exception.getMessage());
		}
	}
	
	@Transactional
	// registro de novo user
	public UserResponseDTO register(RegisterDTO registerDto) {
		if (registerDto.getLogin() != null && !registerDto.getLogin().isEmpty() && 
				userRepository.findByLogin(registerDto.getLogin()).isPresent()) {
			throw new BusinessException("Login já está em uso");
		}
		
		if (registerDto.getEmail() != null && !registerDto.getEmail().isEmpty() && 
				userRepository.findByEmail(registerDto.getEmail()).isPresent()) {
			throw new BusinessException("Email já está em uso");
			
		}
		
		User user = new User(registerDto);
		user.setSenha(passwordEncoder.encode(registerDto.getSenha()));
			
		return new UserResponseDTO(userRepository.save(user));
	}
}
