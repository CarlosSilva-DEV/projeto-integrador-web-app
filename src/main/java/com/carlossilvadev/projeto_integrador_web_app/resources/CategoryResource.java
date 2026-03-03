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
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.carlossilvadev.projeto_integrador_web_app.dto.CategoryDTO;
import com.carlossilvadev.projeto_integrador_web_app.services.CategoryService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping(value = "/categories")
@Tag(name = "Categorias", description = "Controller responsável pelas requisições em endpoints de Categorias")
@SecurityRequirement(name = "bearerAuth")
public class CategoryResource {
	@Autowired
	private CategoryService service;
	
	//============================ ENDPOINTS USUÁRIOS ==========================================================================
	@GetMapping
	@Operation(summary = "Obtém todas as Categorias", description = "Método responsável por retornar todas as Categorias, retorna um List vazio caso não haja nenhuma")
	@ApiResponse(responseCode = "200", description = "Categorias obtidas com sucesso")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<List<CategoryDTO>> findAll() {
		List<CategoryDTO> lista = service.findAll();
		return ResponseEntity.ok().body(lista);
	}
	
	// ============================ ENDPOINTS ADMINISTRATIVOS ==================================================================
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@GetMapping("/{id}")
	@Operation(summary = "Obtém uma Categoria por ID", description = "Método responsável por retornar uma Categoria com base em um ID")
	@ApiResponse(responseCode = "200", description = "Categoria obtida com sucesso")
	@ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<CategoryDTO> findById(@PathVariable Long id) {
		CategoryDTO obj = service.findById(id);
		return ResponseEntity.ok().body(obj);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PostMapping
	@Operation(summary = "Inserir uma nova Categoria", description = "Método responsável por criar uma nova Categoria")
	@ApiResponse(responseCode = "201", description = "Categoria criada com sucesso")
	@ApiResponse(responseCode = "409", description = "Não é possível criar uma nova Categoria com um nome já utilizado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<CategoryDTO> insert(@Valid @RequestBody CategoryDTO obj) {
		CategoryDTO categoryDto = service.insert(obj);
		URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(categoryDto.getId()).toUri();
		return ResponseEntity.created(uri).body(categoryDto);
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@DeleteMapping(value = "/{id}")
	@Operation(summary = "Deletar uma Categoria por ID", description = "Método responsável por deletar uma Categoria com base em um ID")
	@ApiResponse(responseCode = "204", description = "Categoria deletada com sucesso")
	@ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	@ApiResponse(responseCode = "409", description = "Não é possível excluir uma Categoria que possua Produtos vinculados a ela")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<Void> delete(@PathVariable Long id) {
		service.delete(id);
		return ResponseEntity.noContent().build();
	}
	
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@PutMapping(value = "/{id}")
	@Operation(summary = "Atualizar dados de uma Categoria por ID", description = "Método responsável por atualizar os dados de uma Categoria com base em um ID")
	@ApiResponse(responseCode = "200", description = "Categoria atualizada com sucesso")
	@ApiResponse(responseCode = "404", description = "Categoria não encontrada")
	@ApiResponse(responseCode = "409", description = "Não é possível atualizar uma Categoria com um nome já utilizado")
	@ApiResponse(responseCode = "500", description = "Erro interno no servidor")
	public ResponseEntity<CategoryDTO> update(@PathVariable Long id, @Valid @RequestBody CategoryDTO categoryDto) {
		CategoryDTO updatedCategory = service.update(id, categoryDto);
		return ResponseEntity.ok().body(updatedCategory);
	}
}