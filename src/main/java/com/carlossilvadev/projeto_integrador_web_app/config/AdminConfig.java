package com.carlossilvadev.projeto_integrador_web_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;

@Configuration
public class AdminConfig {
	
	@Bean
	CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByLogin("admin").isEmpty()) {
				User admin = new User();
				admin.setNome("AdminUser");
				admin.setLogin("admin");
				admin.setEmail("admin@admin.com");
				admin.setTelefone("");
				admin.setSenha(passwordEncoder.encode("123456")); // alterar senha e inserir referencia application.properties
				admin.setRole(UserRole.ROLE_ADMIN);
				userRepository.save(admin);
			}
		};
	}
}