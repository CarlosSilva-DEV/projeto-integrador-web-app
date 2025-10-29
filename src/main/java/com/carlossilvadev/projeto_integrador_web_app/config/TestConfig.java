package com.carlossilvadev.projeto_integrador_web_app.config;
import java.time.Instant;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;
import com.carlossilvadev.projeto_integrador_web_app.entities.Payment;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.carlossilvadev.projeto_integrador_web_app.repositories.CategoryRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderItemRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.ProductRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.UserRepository;

@Configuration
@Profile("test") // define como uma classe de configuração, especifica pro perfil "test"
public class TestConfig implements CommandLineRunner{
	@Autowired // injeção de dependência
	private UserRepository userRepository;
	
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Override
	public void run(String... args) throws Exception {
		User u1 = new User(null, "Maria", "maria@gmail.com", "999999999", "123456");
		User u2 = new User(null, "Alex", "alex@hotmail.com", "912345678", "123456");
		
		Order o1 = new Order(null, Instant.parse("2025-10-28T13:21:00Z"), OrderStatus.AGUARDANDO_PAGAMENTO, u1);
		Order o2 = new Order(null, Instant.parse("2025-10-27T10:42:35Z"), OrderStatus.PAGO, u2);
		Order o3 = new Order(null, Instant.parse("2025-09-10T12:35:10Z"), OrderStatus.ENVIADO, u1);
		
		Category c1 = new Category(null, "Electronics");
		Category c2 = new Category(null, "Books");
		Category c3 = new Category(null, "Computers");
		
		Product p1 = new Product(null, "Jojo's Bizarre Adventure Part 5: Golden Wind", "Lorem ipsum dolor sit amet...", 89.90, "");
		Product p2 = new Product(null, "Redmagic 10 Pro 256GB 12GB RAM Snapdragon 8 Elite", "Lorem ipsum dolor sit amet...", 4599.90, "");
		Product p3 = new Product(null, "Macbook Pro", "Lorem ipsum dolor sit amet...", 6599.90, "");
		
		OrderItem oi1 = new OrderItem(o1, p1, 2, p1.getPreco());
		OrderItem oi2 = new OrderItem(o2, p2, 1, p2.getPreco());
		OrderItem oi3 = new OrderItem(o2, p3, 1, p3.getPreco());
		OrderItem oi4 = new OrderItem(o3, p1, 1, p1.getPreco());
		OrderItem oi5 = new OrderItem(o3, p3, 1, p3.getPreco());
		
		
		userRepository.saveAll(Arrays.asList(u1, u2)); // salvando objetos dentro de um array no repositório Users
		orderRepository.saveAll(Arrays.asList(o1, o2, o3));
		categoryRepository.saveAll(Arrays.asList(c1, c2, c3));
		productRepository.saveAll(Arrays.asList(p1, p2, p3));
		orderItemRepository.saveAll(Arrays.asList(oi1, oi2, oi3, oi4, oi5));
		
		// associando produtos com categorias
		p1.getCategories().add(c2);
		p2.getCategories().add(c1);
		p3.getCategories().add(c1);
		p3.getCategories().add(c3);
		
		// salvando associação no repositório
		productRepository.saveAll(Arrays.asList(p1, p2, p3));
		
		Payment pay1 = new Payment(null, Instant.parse("2025-10-27T10:41:48Z"), o2);
		o2.setPayment(pay1); // chama método da classe dependente
		orderRepository.save(o2); // salvando relacionamento de dependência (obj dependente não usa repositorio próprio)
	}
}