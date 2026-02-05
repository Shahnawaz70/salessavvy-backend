package com.salessavvy.app.user.service;

import java.util.List;

import com.salessavvy.app.common.entity.Product;

public interface ProductServiceContract {
	public List<Product> getProductsByCategory(String categoryName);
	public List<String> getProductImages(Integer productId);
}
