package com.carlossilvadev.projeto_integrador_web_app.resources;
import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlossilvadev.projeto_integrador_web_app.dto.product.ProductDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.product.ProductUpdateDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/products")
@Tag(name = "Produtos", description = "Controller responsável pelas requisições em endpoints de Produtos")
@SecurityRequirement(name = "bearerAuth")
public class ProductResource {
	@Autowired
	private ProductService service;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	@GetMapping
	@Operation(summary = "Obtém todos os Produtos", description = "Método responsável por retornar todos os Produtos, retorna um List vazio caso não haja nenhum")
	@ApiResponse(responseCode = "200", description = "Produtos obtidos com sucesso")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
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
	@Operation(summary = "Obtém um Produto por ID", description = "Método responsável por retornar um Produto com base em um ID")
	@ApiResponse(responseCode = "200", description = "Produto obtido com sucesso")
	@ApiResponse(responseCode = "404", description = "Produto não encontrado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<ProductDTO> findById(@PathVariable Long id) {
		ProductDTO obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@GetMapping(value = "/search")
	@Operation(summary = "Buscar Produtos pelo nome", description = "Método responsável por buscar Produtos com base em um nome, retorna todos os Produtos caso não encontre")
	@ApiResponse(responseCode = "200", description = "Produto obtido com sucesso")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam(value = "q", required = false) String nome) {
		List<ProductDTO> products = service.search(nome);
		return ResponseEntity.ok().body(products);
	}
	
	@GetMapping(value = "/by-category/{id}")
	@Operation(summary = "Buscar Produtos por ID de categoria", description = "Método responsável por buscar Produtos pelo ID de uma categoria")
	@ApiResponse(responseCode = "200", description = "Produto obtido com sucesso")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<ProductDTO>> findByCategory(@PathVariable("id") Long categoryId) {
		List<ProductDTO> products = service.findByCategory(categoryId);
		return ResponseEntity.ok().body(products);
	}
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	@Operation(summary = "Criar um novo Produto", description = "Método responsável por criar um novo Produto")
	@ApiResponse(responseCode = "201", description = "Produto criado com sucesso")
	@ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<ProductDTO> insert(@Valid @RequestBody ProductDTO obj) {
		ProductDTO productDto = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(productDto.getId()).toUri();
		return ResponseEntity.created(uri).body(productDto);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Deleta um Produto por ID", description = "Método responsável por retornar um Produto com base em um ID")
	@ApiResponse(responseCode = "204", description = "Produto deletado com sucesso")
	@ApiResponse(responseCode = "404", description = "Produto não encontrado")
	@ApiResponse(responseCode = "409", description = "Não é possível excluir um Produto vinculado a Pedidos existentes")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PatchMapping(value = "/{id}")
	@Operation(summary = "Atualiza dados de um Produto por ID", description = "Método responsável por atualizar os dados de um Produto com base em um ID")
	@ApiResponse(responseCode = "200", description = "Produto atualizado com sucesso")
	@ApiResponse(responseCode = "404", description = "Produto ou Categoria não encontrada")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<ProductDTO> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateDTO productUpdateDto) {
		ProductDTO updatedProduct = service.update(id, productUpdateDto);
		return ResponseEntity.ok().body(updatedProduct);
	}
}