package com.carlossilvadev.projeto_integrador_web_app.services;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carlossilvadev.projeto_integrador_web_app.dto.OrderCreateDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.dto.OrderItemDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.Order;
import com.carlossilvadev.projeto_integrador_web_app.entities.OrderItem;
import com.carlossilvadev.projeto_integrador_web_app.entities.Product;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderItemRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.OrderRepository;
import com.carlossilvadev.projeto_integrador_web_app.repositories.ProductRepository;
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
	public List<OrderDTO> findOrdersByCurrentUser() {
		User currentUser = userService.getCurrentUserEntity();
		List<Order> orders = orderRepository.findByClientWithItems(currentUser);
		
		// DEBUG
		System.out.println("\n=== DEBUG: Pedidos encontrados ===\n");
	    for (Order order : orders) {
	        System.out.println("Pedido ID: " + order.getId());
	        System.out.println("Moment: " + order.getMoment());
	        System.out.println("Items count: " + order.getItems().size());
	        for (OrderItem item : order.getItems()) {
	            System.out.println(" - Item: " + item.getProduct().getNome() + ", Qtd: " + item.getQuantidade());
	        }
	    }
		
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
	
	public OrderDTO findByIdAndCurrentUser(Long id) {
		User currentUser = userService.getCurrentUserEntity();
		Order order = orderRepository.findByIdAndClientWithItems(id, currentUser)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		return new OrderDTO(order);
	}
	
	public OrderDTO createOrder(OrderCreateDTO orderCreateDto) {
		User currentUser = userService.getCurrentUserEntity();
		
		Order order = new Order();
		order.setClient(currentUser);
		order.setOrderStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
		
		
		Order savedOrder = orderRepository.save(order);
		
		for (OrderItemDTO itemDto : orderCreateDto.getItems()) {
			Product product = productRepository.findById(itemDto.getProductId())
					.orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado: " + itemDto.getProductId()));
			
			OrderItem orderItem = new OrderItem(savedOrder, product, itemDto.getQuantidade(), product.getPreco());
			order.getItems().add(orderItem);
			
			orderItemRepository.save(orderItem);
		}
		
		Order orderWithItems = orderRepository.findByIdWithItems(savedOrder.getId())
				.orElseThrow(() -> new ResourceNotFoundException(savedOrder.getId()));
		
		return new OrderDTO(orderWithItems);
	}
	
	public void cancelOrder(Long id) {
		User currentUser = userService.getCurrentUserEntity();
		Order order = orderRepository.findByIdAndClientWithItems(id, currentUser)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		
		if (order.getOrderStatus() == OrderStatus.PAGO) {
			throw new RuntimeException("Não é possivel cancelar um pedido já pago");
		}
		
		order.setOrderStatus(OrderStatus.CANCELADO);
		orderRepository.save(order);
	}
	
	// ============================ MÉTODOS ADMINISTRATIVOS ==================================================================
	public List<OrderDTO> findAll() {
		List<Order> orders = orderRepository.findAllWithItems();
		return orders.stream().map(OrderDTO::new).collect(Collectors.toList());
	}
	
	public OrderDTO findById(Long id) {
		Order order = orderRepository.findByIdWithItems(id)
				.orElseThrow(() -> new ResourceNotFoundException(id));
		return new OrderDTO(order);
	}
}