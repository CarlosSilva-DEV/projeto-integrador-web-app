package com.carlossilvadev.projeto_integrador_web_app.config;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;

@Configuration
@Profile("test") // define como uma classe de configuração, especifica pro perfil "test"
public class TestConfig implements CommandLineRunner{
	@Autowired // injeção de dependência
	private UserRepository userRepository;
	
	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "Maria", "maria@gmail.com", "999999999", "123456");
		User u2 = new User(null, "Alex", "alex@hotmail.com", "912345678", "123456");
		
		userRepository.saveAll(Arrays.asList(u1, u2)); // salvando objetos dentro de um array no repositório Users
	}
}