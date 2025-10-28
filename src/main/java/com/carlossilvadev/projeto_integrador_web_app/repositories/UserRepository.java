package com.carlossilvadev.projeto_integrador_web_app.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import com.carlossilvadev.projeto_integrador_web_app.entities.User;

public interface UserRepository extends JpaRepository<User, Long>{ // extende jparepository<entidade, ID>
	
}