package com.carlossilvadev.projeto_integrador_web_app.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.User;

@Repository // registro como componente do Spring, anotação não obrigatória por conta da herança JpaRepository abaixo
public interface UserRepository extends JpaRepository<User, Long>{ // extende jparepository<entidade, ID>
	
}