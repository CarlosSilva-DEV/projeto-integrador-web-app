package com.carlossilvadev.projeto_integrador_web_app.config;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;

@Configuration
@Profile("test") // define como uma classe de configuração, especifica pro perfil "test"
public class TestConfig implements CommandLineRunner{
	@Autowired // injeção de dependência
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "Maria", "maria@gmail.com", "999999999", "123456");
		User u2 = new User(null, "Alex", "alex@hotmail.com", "912345678", "123456");
		
		Order o1 = new Order(null, Instant.parse("2025-10-28T13:21:00Z"), u1);
		Order o2 = new Order(null, Instant.parse("2025-10-27T10:42:35Z"), u2);
		Order o3 = new Order(null, Instant.parse("2025-09-10T12:35:10Z"), u1);
		
		userRepository.saveAll(Arrays.asList(u1, u2)); // salvando objetos dentro de um array no repositório Users
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));
	}
}