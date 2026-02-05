package com.salessavvy.app.user.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.salessavvy.app.common.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
	List<Product> findByCategory_CategoryId(Integer categoryId);
	
	@Query("SELECT p.category.categoryName FROM Product p WHERE p.productId = :productId")
    String findCategoryNameByProductId(int productId);
}
