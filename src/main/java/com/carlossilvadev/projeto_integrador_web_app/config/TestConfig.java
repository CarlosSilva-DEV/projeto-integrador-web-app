package com.carlossilvadev.projeto_integrador_web_app.config;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.carlossilvadev.projeto_integrador_web_app.repositories.CategoryRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.ProductRepository;

@Configuration
@Profile("test") // define como uma classe de configuração, especifica pro perfil "test"
public class TestConfig implements CommandLineRunner{
	@Autowired
	private CategoryRepository categoryRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Override
	public void run(String... args) throws Exception {
		
		Category c1 = new Category("Electronics");
		Category c2 = new Category("Books");
		Category c3 = new Category("Computers");
		
		Product p1 = new Product("Jojo's Bizarre Adventure Part 5: Golden Wind", "Lorem ipsum dolor sit amet...", 89.90, "");
		Product p2 = new Product("Redmagic 10 Pro 256GB 12GB RAM Snapdragon 8 Elite", "Lorem ipsum dolor sit amet...", 4599.90, "");
		Product p3 = new Product("Macbook Pro", "Lorem ipsum dolor sit amet...", 6599.90, "");
		Product p4 = new Product("Jojo's Bizarre Adventure Part 8: JoJolion", "Lorem ipsum dolor sit amet...", 129.90, "");
		
		categoryRepository.saveAll(Arrays.asList(c1, c2, c3));
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4));
		
		// associando produtos com categorias
		p1.getCategories().add(c2);
		p2.getCategories().add(c1);
		p3.getCategories().add(c1);
		p3.getCategories().add(c3);
		p4.getCategories().add(c2);
		
		// salvando associação no repositório
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4));
	}
}