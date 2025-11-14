package com.carlossilvadev.projeto_integrador_web_app.entities;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.BeanUtils;

import com.carlossilvadev.projeto_integrador_web_app.dto.OrderDTO;
import com.carlossilvadev.projeto_integrador_web_app.entities.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;


@Entity
@Table(name = "tb_order")
public class Order implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "GMT") // formatação padrão ISO 8601
	private Instant moment;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
	@ManyToOne // define associação N:1 para relação no db
	@JoinColumn(name = "client_id") // determina chave FK do User na tabela Order
	private User client;
	
	@OneToMany(mappedBy = "id.order")
	private Set<OrderItem> items = new HashSet<>();
	
	private double total;
	
	@OneToOne(mappedBy = "order", cascade = CascadeType.ALL) // obrigatório em relacionamento 1:1
	private Payment payment;
	
	// Construtores
	public Order() {
		this.moment = Instant.now();
		this.setOrderStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
	}
	
	public Order(OrderDTO orderDto, User client) {
		this();
		BeanUtils.copyProperties(orderDto, this, "client", "orderStatus");
		this.client = client;
	}
	
	// getters e setters
	public Set<OrderItem> getItems() {
		return items;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public Instant getMoment() {
		return moment;
	}
	public void setMoment(Instant moment) {
		this.moment = moment;
	}
	
	public OrderStatus getOrderStatus() {
		return orderStatus;
	}
	
	public void setOrderStatus(OrderStatus orderStatus) {
		this.orderStatus = orderStatus;
	}
	
	public User getClient() {
		return client;
	}
	public void setClient(User client) {
		this.client = client;
	}
	
	public Payment getPayment() {
		return payment;
	}
	public void setPayment(Payment payment) {
		this.payment = payment;
	}
	
	// calculo total
	public double getTotal() {
		double soma = 0.0;
		
		for (OrderItem i : items) {
			soma += i.getSubtotal();
		}
		this.total = soma;
		return total;
	}
	
	@PrePersist
	public void prePersist() {
		if (this.moment == null) {
			this.moment = Instant.now();
		}
		
		if (this.orderStatus == null) {
			this.orderStatus = OrderStatus.AGUARDANDO_PAGAMENTO;
		}
	}
	
	
	// hashcode e equals
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Order other = (Order) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}