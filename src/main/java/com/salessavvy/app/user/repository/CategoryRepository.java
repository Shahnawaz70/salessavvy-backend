package com.salessavvy.app.user.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.salessavvy.app.common.entity.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
	Optional<Category> findByCategoryName(String categoryName);
}	
