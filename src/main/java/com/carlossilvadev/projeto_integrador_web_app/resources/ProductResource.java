package com.carlossilvadev.projeto_integrador_web_app.resources;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlossilvadev.projeto_integrador_web_app.dto.ProductDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.ProductService;

import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/products")
public class ProductResource {
	@Autowired
	private ProductService service;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	@GetMapping
	public ResponseEntity<List<ProductDTO>> findAll(@RequestParam(value = "categoryId", required = false) Long categoryId, @RequestParam(value = "q", required = false) String searchTerm) {
		List<ProductDTO> products;
		
		if (categoryId != null || (searchTerm != null && !searchTerm.trim().isEmpty())) {
			products = service.findWithFilter(categoryId, searchTerm);
		} else {
			products = service.findAll();
		}
		
		return ResponseEntity.ok().body(products);
	}
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
		ProductDTO obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@GetMapping(value = "/search")
	public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam(value = "q", required = false) String nome) {
		List<ProductDTO> products = service.search(nome);
		return ResponseEntity.ok().body(products);
	}
	
	@GetMapping(value = "/by-category/{id}")
	public ResponseEntity<List<ProductDTO>> findByCategory(@PathVariable("id") Long categoryId) {
		List<ProductDTO> products = service.findByCategory(categoryId);
		return ResponseEntity.ok().body(products);
	}
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping
	public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO obj) {
		ProductDTO productDto = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productDto.getId()).toUri();
		return ResponseEntity.created(uri).body(productDto);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping(value = "/{id}")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PutMapping(value = "/{id}")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductDTO productDto) {
		ProductDTO updatedProduct = service.update(id, productDto);
		return ResponseEntity.ok().body(updatedProduct);
	}
}