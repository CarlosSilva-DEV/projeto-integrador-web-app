package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.DatabaseException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;

// import jakarta.persistence.EntityNotFoundException;

@Service
public class OrderService {
	@Autowired
	private OrderRepository repository;
	
	public List<OrderDTO> findAll() {
		List<Order> orders = repository.findAll();
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
	
	public OrderDTO findById(Long id) {
		Optional<Order> obj = repository.findById(id);
		Order order = obj.orElseThrow(() -> new ResourceNotFoundException(id));
		return new OrderDTO(order);
	}
	
	public OrderDTO insert(OrderDTO orderDto) {
		Order order= new Order(orderDto);
		return new OrderDTO(repository.save(order));
	}
	
	public void delete(Long id) {
		try {
			repository.deleteById(id);
		} catch (EmptyResultDataAccessException exception) {
			throw new ResourceNotFoundException(id);
		} catch (DataIntegrityViolationException exception) {
			throw new DatabaseException(exception.getMessage());
		}
	}
	
	/** AVALIAR IMPLEMENTAÇÃO DE MÉTODO PUT | POSSIVEIS REGRAS: APENAS ALTERAR ITENS DO PEDIDO (ADICIONAR/REMOVER)
	 * PEDIDO DEVE SER IMUTÁVEL?

	public OrderDTO update(Long id, OrderDTO orderDto) {
		try {
			Order entity = repository.getReferenceById(id);
			updateData(entity, orderDto);
			Order updatedOrder = repository.save(entity);
			return new OrderDTO(updatedOrder);
		} catch (EntityNotFoundException exception) {
			throw new ResourceNotFoundException(id);
		}
	}
	
	private void updateData(Order entity, OrderDTO obj) {
		entity.setNome(obj.getNome());
		entity.setLogin(obj.getLogin());
		entity.setEmail(obj.getEmail());
		entity.setTelefone(obj.getTelefone());
		entity.setSenha(obj.getSenha());
	} **/
}