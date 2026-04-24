package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderCreateDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.order.OrderItemDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.PaymentStatus;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderItemRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.ProductRepository;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.BusinessException;
import com.carlossilvadev.projeto_integrador_web_app.services.exceptions.ResourceNotFoundException;


@Service
public class OrderService {
	@Autowired
	private OrderRepository orderRepository;
	
	@Autowired
	private ProductRepository productRepository;
	
	@Autowired
	private OrderItemRepository orderItemRepository;
	
	@Autowired
	private UserService userService;
	
	//============================ MÉTODOS USUÁRIOS ==========================================================================
	@Transactional(readOnly = true)
	public List<OrderDTO> findOrdersByCurrentUser() {
		User currentUser = userService.getCurrentUserEntity();
		List<Order> orders = orderRepository.findByClientWithItems(currentUser);
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public OrderDTO findByIdAndCurrentUser(Long id) {
		User currentUser = userService.getCurrentUserEntity();
		Order order = orderRepository.findByIdAndClientWithItems(id, currentUser)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: " + id));
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO createOrder(OrderCreateDTO orderCreateDto) {
		User currentUser = userService.getCurrentUserEntity();
		
		Order order = new Order(currentUser);
		
		// armazena ID de cada produto informado no DTO
		List<Long> productIds = orderCreateDto.getItems()
				.stream().map(OrderItemDTO::getProductId).toList();
		
		// recupera cada produto do db com base na lista de IDs
		Map<Long, Product> productsById = productRepository.findAllById(productIds)
				.stream().collect(Collectors.toMap(Product::getId, p -> p));
		
		List<OrderItem> tempList = new ArrayList<>();
		
		for (OrderItemDTO itemDto : orderCreateDto.getItems()) {
			Product product = Optional.ofNullable(productsById.get(itemDto.getProductId()))
					.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.getProductId()));
			
			OrderItem orderItem = new OrderItem(order, product, itemDto.getQuantidade(), product.getPreco());
			tempList.add(orderItem);
			order.getItems().add(orderItem);
		}
		
		order.calcularTotal();
		orderRepository.save(order);		
		orderItemRepository.saveAll(tempList);
		
		return new OrderDTO(order);
	}
	
	@Transactional
	public OrderDTO cancelOrder(Long id) {
		User currentUser = userService.getCurrentUserEntity();
		Order order = orderRepository.findByIdAndClientWithItems(id, currentUser)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: ID " + id));
		
		if (order.getOrderStatus() == OrderStatus.CANCELADO) {
			throw new BusinessException("Não foi possível cancelar o pedido: Pedido já cancelado");
		}
		
		if (order.getOrderStatus() == OrderStatus.PAGO) {
			throw new BusinessException("Não é possivel cancelar um pedido já pago");
		}
		
		if (order.getPayment() != null) {
			order.getPayment().setStatus(PaymentStatus.CANCELADO);
		}
		
		order.setOrderStatus(OrderStatus.CANCELADO);
		
		orderRepository.save(order);
		
		return new OrderDTO(order);
	}
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	@Transactional(readOnly = true)
	public List<OrderDTO> findAll() {
		List<Order> orders = orderRepository.findAllWithItems();
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public OrderDTO findById(Long id) {
		Order order = orderRepository.findByIdWithItems(id)
				.orElseThrow(() -> new ResourceNotFoundException("Pedido não encontrado: ID " + id));
		return new OrderDTO(order);
	}
}