package com.carlossilvadev.projeto_integrador_web_app.repositories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carlossilvadev.projeto_integrador_web_app.entities.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long>{
	
}