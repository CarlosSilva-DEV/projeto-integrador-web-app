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
		
		Category c1 = new Category("Celulares");
		Category c2 = new Category("Tablets");
		Category c3 = new Category("Fones");
		Category c4 = new Category("Acessórios");
		
		Product p1 = new Product("Redmagic Astra 512GB 16GB RAM Snapdragon 8 Elite", "Lorem ipsum dolor sit amet...", 5319.9, "https://m.media-amazon.com/images/I/61fzMP+102L.jpg");
		Product p2 = new Product("Redmagic 10 Pro 256GB 12GB RAM Snapdragon 8 Elite", "Lorem ipsum dolor sit amet...", 4599.90, "https://br.redmagic.gg/cdn/shop/files/1_77cba6ed-1d4d-40ef-97cd-44f87d79bb58_600x600.png?v=1757910898");
		Product p3 = new Product("Apple iPad M4", "Lorem ipsum dolor sit amet...", 6599.90, "https://cdn.awsli.com.br/800x800/284/284108/produto/298522188/ipad-pro-11-preto-u8w5xzl2m8.jpeg");
		Product p4 = new Product("Fone intrauricular HyperX Cloud II", "Lorem ipsum dolor sit amet...", 345.9, "https://images.kabum.com.br/produtos/fotos/483041/fone-de-ouvido-gamer-hyperx-cloud-earbuds-ii-com-microfone-vermelho-705l8aa_1701173988_gg.jpg");
		Product p5 = new Product("Headset Razer Blackshark", "Lorem ipsum dolor sit amet...", 129.90, "https://m.media-amazon.com/images/I/51FRJHB7XOL._AC_UF1000,1000_QL80_.jpg");
		Product p6 = new Product("IQOO 13 Pro 256GB 12GB RAM", "Lorem ipsum dolor sit amet...", 5499.9, "https://cdn.awsli.com.br/2500x2500/2122/2122929/produto/337379712/legend1-kfd148ytf8.jpg");
		Product p7 = new Product("Cooler Magnético Black Shark 20W", "Lorem ipsum dolor sit amet...", 450.0, "https://m.media-amazon.com/images/I/61I2JlDymSL._AC_UF1000,1000_QL80_.jpg");
		Product p8 = new Product("Headset JBL Quantum 300", "Lorem ipsum dolor sit amet...", 329.5, "https://m.media-amazon.com/images/I/61X8As0NKQL.jpg");
		Product p9 = new Product("Estação de Carga UGREEN NEXODE", "Lorem ipsum dolor sit amet...", 529.9, "https://m.media-amazon.com/images/I/51KyN6BQP-L.jpg");
		Product p10 = new Product("Adaptador Dongle MCDodo 60W", "Lorem ipsum dolor sit amet...", 249.9, "https://mcdodo.com.br/cdn/shop/files/7b994ad9861ee4c6c78ede860efc2975.jpg?v=1726160813");
		
		categoryRepository.saveAll(Arrays.asList(c1, c2, c3, c4));
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
		
		// associando produtos com categorias
		p1.getCategories().add(c2);
		p2.getCategories().add(c1);
		p3.getCategories().add(c2);
		p4.getCategories().add(c3);
		p4.getCategories().add(c4);
		p5.getCategories().add(c3);
		p5.getCategories().add(c4);
		p6.getCategories().add(c1);
		p7.getCategories().add(c4);
		p8.getCategories().add(c3);
		p8.getCategories().add(c4);
		p9.getCategories().add(c4);
		p10.getCategories().add(c4);
		
		// salvando associação no repositório
		productRepository.saveAll(Arrays.asList(p1, p2, p3, p4, p5, p6, p7, p8, p9, p10));
	}
}