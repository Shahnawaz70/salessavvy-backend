package com.salessavvy.app.user.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.salessavvy.app.common.entity.Category;
import com.salessavvy.app.common.entity.Product;
import com.salessavvy.app.common.entity.ProductImage;
import com.salessavvy.app.user.repository.CategoryRepository;
import com.salessavvy.app.user.repository.ProductImageRepository;
import com.salessavvy.app.user.repository.ProductRepository;
import com.salessavvy.app.user.service.ProductServiceContract;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Service
public class ProductService implements ProductServiceContract{

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public List<Product> getProductsByCategory(String categoryName) {
        if (categoryName != null && !categoryName.isEmpty()) {
            Optional<Category> categoryOpt = categoryRepository.findByCategoryName(categoryName);
            if (categoryOpt.isPresent()) {
                Category category = categoryOpt.get();
                return productRepository.findByCategory_CategoryId(category.getCategoryId());
            } else {
                throw new RuntimeException("Category not found");
            }
        } else {
            return productRepository.findAll();
        }
    }

    public List<String> getProductImages(Integer productId) {
        List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(productId);
        List<String> imageUrls = new ArrayList<>();
        for (ProductImage image : productImages) {
            imageUrls.add(image.getImageUrl());
        }
        return imageUrls;
    }
}
