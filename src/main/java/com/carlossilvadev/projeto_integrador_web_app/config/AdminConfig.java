package com.carlossilvadev.projeto_integrador_web_app.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.UserRole;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class AdminConfig {
	@Value("${ADMIN_NAME:AdminUser}")
	private String adminName;

	@Value("${ADMIN_LOGIN:admin}")
	private String adminLogin;

	@Value("${ADMIN_PASSWORD:password}")
	private String adminPassword;
	
	@Bean
	CommandLineRunner createAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByLogin(adminLogin).isEmpty()) {
				User admin = new User();
				admin.setNome(adminName);
				admin.setLogin(adminLogin);
				admin.setEmail("admin@admin.com");
				admin.setTelefone("");
				admin.setSenha(passwordEncoder.encode(adminPassword));
				admin.setRole(UserRole.ROLE_ADMIN);
				userRepository.save(admin);
			}
		};
	}
}