package com.carlossilvadev.projeto_integrador_web_app.entities;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "tb_user") // classe será uma tabela do db
public class User implements Serializable { // interface que salva o estado atual de um objeto (persistência em dbs)
	private static final long serialVersionUID = 1L; // número de série padrão
	
	@Id // mapeia o atributo como um ID
	@GeneratedValue(strategy = GenerationType.IDENTITY) // o ID será auto-increment no db
	private Long id;
	private String nome;
	private String email;
	private String telefone;
	private String senha;
	
	@JsonIgnore // evita looping nas requisições de User-Order
	@OneToMany(mappedBy = "client") // define associação 1:N para relação no db
	private List<Order> orders = new ArrayList<>();
	
	// Construtores (padrão do Spring: vazio e com params)
	public User() {
	}
	
	public User(Long id, String nome, String email, String telefone, String senha) {
		super();
		this.id = id;
		this.nome = nome;
		this.email = email;
		this.telefone = telefone;
		this.senha = senha;
	}
	
	// getters e setters
	public List<Order> getOrders(){
		return orders;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefone() {
		return telefone;
	}
	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
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
		User other = (User) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
}