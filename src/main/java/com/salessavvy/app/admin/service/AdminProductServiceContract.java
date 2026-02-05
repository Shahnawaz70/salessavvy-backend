package com.salessavvy.app.admin.service;
import com.salessavvy.app.common.entity.Product;
public interface AdminProductServiceContract {
	
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId, String imageUrl);
	void deleteProduct(Integer productId);
}
