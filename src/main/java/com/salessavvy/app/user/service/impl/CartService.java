package com.salessavvy.app.user.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.salessavvy.app.common.entity.Cart_Items;
import com.salessavvy.app.common.entity.Product;
import com.salessavvy.app.common.entity.ProductImage;
import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.user.repository.CartRepository;
import com.salessavvy.app.user.repository.ProductImageRepository;
import com.salessavvy.app.user.repository.ProductRepository;
import com.salessavvy.app.user.repository.UserRepository;
import com.salessavvy.app.user.service.CartServiceContract;

@Service
public class CartService implements CartServiceContract {

	ProductRepository productRepository;
	CartRepository cartRepository;
	ProductImageRepository productImageRepository;
	UserRepository userRepository;

	public CartService(ProductRepository productRepository, CartRepository cartRepository,
			ProductImageRepository productImageRepository, UserRepository userRepository) {
		super();
		this.productRepository = productRepository;
		this.cartRepository = cartRepository;
		this.productImageRepository = productImageRepository;
		this.userRepository = userRepository;
	}

	@Override
	public void addToCart(User user, int productId, int quantity) {

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found with ID: " + productId));

		// Fetch cart item for this userId and productId
		Optional<Cart_Items> existingItem = cartRepository.findByUserAndProduct(user.getUserId(), productId);
		if (existingItem.isPresent()) {
			Cart_Items cartItem = existingItem.get();
			cartItem.setQuantity(cartItem.getQuantity() + quantity);
			cartRepository.save(cartItem);
		} else {
			Cart_Items newItem = new Cart_Items(user, product, quantity);
			cartRepository.save(newItem);
		}
	}

	@Override
	public Map<String, Object> getCartItems(User authenticatedUser) {

		// Fetch the cart items for the user with product details
		List<Cart_Items> cartItems = cartRepository.findCartItemsWithProductDetails(authenticatedUser.getUserId());

		// Create a response map to hold the cart details
		Map<String, Object> response = new HashMap<>();
		response.put("username", authenticatedUser.getUsername());
		response.put("role", authenticatedUser.getRole().toString());

		// List to hold the product details
		List<Map<String, Object>> products = new ArrayList<>();
		int overallTotalPrice = 0;
		for (Cart_Items cartItem : cartItems) {
			Map<String, Object> productDetails = new HashMap<>();

			// Get product details
			Product product = cartItem.getProduct();
			List<ProductImage> productImages = productImageRepository.findByProduct_ProductId(product.getProductId());
			

			// Populate product details
			String imageUrl = (productImages != null && !productImages.isEmpty()) ? productImages.get(0).getImageUrl()
					: "default-image-url";
			productDetails.put("product_id", product.getProductId());
			productDetails.put("image_url", imageUrl);
			productDetails.put("name", product.getName());
			productDetails.put("description", product.getDescription());
			productDetails.put("quantity", cartItem.getQuantity());

			productDetails.put("price_per_unit", product.getPrice());
			productDetails.put(
			    "total_price",
			    product.getPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity()))
			);


			// Add to products list
			products.add(productDetails);

			// Update overall total price
			overallTotalPrice += cartItem.getQuantity() * product.getPrice().doubleValue();
		}

		// Prepare the final cart response
		Map<String, Object> cart = new HashMap<>();
		cart.put("products", products);
		cart.put("overall_total_price", overallTotalPrice);
		response.put("cart", cart);

		return response;
	}

	@Override
	public void updateCartItemQuantity(User authenticatedUser, int productId, int quantity) {
		User user = userRepository.findById(authenticatedUser.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("User not found"));

		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));

		Optional<Cart_Items> existingItem = cartRepository.findByUserAndProduct(authenticatedUser.getUserId(),
				productId);
		if (existingItem.isPresent()) {
			Cart_Items item = existingItem.get();
			if (quantity == 0) {
				deleteCartItem(authenticatedUser.getUserId(), productId);
			} else {
				item.setQuantity(quantity);
				cartRepository.save(item);
			}
		} else {
			throw new RuntimeException("Cart item not found associated with product and user");
		}
	}

	public void deleteCartItem(int userId, int productId) {
		Product product = productRepository.findById(productId)
				.orElseThrow(() -> new IllegalArgumentException("Product not found"));
		cartRepository.deleteCartItem(userId, productId);
	}

	@Override
	public int getCartItemCount(int userId) {
		int count = cartRepository.countTotalItems(userId);
		return count;
	}

}
