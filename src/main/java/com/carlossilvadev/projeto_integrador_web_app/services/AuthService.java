package com.carlossilvadev.projeto_integrador_web_app.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.AccessDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.AuthenticationDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.RegisterDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.UserDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;
import com.carlossilvadev.projeto_integrador_web_app.security.jwt.JwtUtils;

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
		
		AccessDTO accessDto = new AccessDTO(token);
		return accessDto;
		
		} catch (BadCredentialsException exception) {
			return null;
		}
	}
	
	// registro de novo user
	public UserDTO register(RegisterDTO registerDto) {
		User user = new User();
		user.setNome(registerDto.getNome());
		user.setLogin(registerDto.getLogin());
		user.setEmail(registerDto.getEmail());
		user.setTelefone(registerDto.getTelefone());
		user.setSenha(passwordEncoder.encode(registerDto.getSenha()));
		user.setRole(UserRole.ROLE_USER);
		
		return new UserDTO(userRepository.save(user));
	}
}
