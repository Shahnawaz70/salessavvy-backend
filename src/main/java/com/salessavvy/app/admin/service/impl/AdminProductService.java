package com.salessavvy.app.admin.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.salessavvy.app.admin.service.AdminProductServiceContract;
import com.salessavvy.app.common.entity.Category;
import com.salessavvy.app.common.entity.Product;
import com.salessavvy.app.common.entity.ProductImage;
import com.salessavvy.app.user.repository.CategoryRepository;
import com.salessavvy.app.user.repository.ProductImageRepository;
import com.salessavvy.app.user.repository.ProductRepository;
@Service
public class AdminProductService implements AdminProductServiceContract {
	private ProductRepository productRepository;
	private ProductImageRepository imageRepository;
	private CategoryRepository categoryRepository;

	public AdminProductService(ProductRepository productRepository, ProductImageRepository imageRepository,
			CategoryRepository categoryRepository) {
		super();
		this.productRepository = productRepository;
		this.imageRepository = imageRepository;
		this.categoryRepository = categoryRepository;
	}

	@Override
	public Product addProductWithImage(String name, String description, Double price, Integer stock, Integer categoryId,
			String imageUrl) {

		Optional<Category> category = categoryRepository.findById(categoryId);
		if (category.isEmpty()) {
			throw new IllegalArgumentException("Invalid category ID");
		}

		Product product = new Product(name, description, BigDecimal.valueOf(price), stock, category.get(),
				LocalDateTime.now(), LocalDateTime.now());
		Product savedProduct = productRepository.save(product);

		if (imageUrl != null && !imageUrl.isEmpty()) {
			ProductImage image = new ProductImage(savedProduct, imageUrl);
			imageRepository.save(image);
		}

		return savedProduct;
	}

	@Override
	public void deleteProduct(Integer productId) {
		// Check if the product exists
		if (!productRepository.existsById(productId)) {
			throw new IllegalArgumentException("Product not found");
		}
		// Delete associated product images
		imageRepository.deleteByProductId(productId);
		productRepository.deleteById(productId);

	}

}
