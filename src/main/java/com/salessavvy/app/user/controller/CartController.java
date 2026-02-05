package com.salessavvy.app.user.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.user.service.CartServiceContract;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
@RequestMapping("/api/cart")
public class CartController {

	
	CartServiceContract cartService;
	
	
	public CartController(CartServiceContract cartService) {
		super();
		this.cartService = cartService;
		
	}

	@PostMapping("/add")
	public ResponseEntity<Void> addToCart(@RequestBody Map<String, Object> request, HttpServletRequest req) {
		User user = (User) req.getAttribute("authenticatedUser");
		String username = (String) request.get("username");
		int productId = (int) request.get("productId");
		int quantity = request.containsKey("quantity")?(int) request.get("quantity"):1;
		cartService.addToCart(user, productId, quantity);
		return ResponseEntity.status(HttpStatus.CREATED).build();
	}
	
	// Fetch all cart items for the user
    @GetMapping("/items")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpServletRequest request) {
    	
    	User user = (User) request.getAttribute("authenticatedUser");
    	// Call the service to get cart items for the user
        Map<String, Object> response = cartService.getCartItems(user);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/update")
    public ResponseEntity<Void> updateCartItemQuantity(@RequestBody Map<String, Object> request, HttpServletRequest req) {
    	String username = (String) request.get("username");
        int productId = (int) request.get("productId");
        int quantity = (int) request.get("quantity");
        User user =(User) req.getAttribute("authenticatedUser");
        cartService.updateCartItemQuantity(user, productId, quantity);
        
        return ResponseEntity.ok().build();
    }
    
    @DeleteMapping("/delete")
    public ResponseEntity<Void> deleteCartItem(@RequestBody Map<String, Object> request, HttpServletRequest req) {
    	String username = (String) request.get("username");
    	int productId = (int) request.get("productId");
    	User user = (User) req.getAttribute("authenticatedUser");
    	cartService.deleteCartItem(user.getUserId(), productId);
    	return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
    
    @GetMapping("/items/count")
    public ResponseEntity<Integer> getCartItemCount(@RequestParam String username, HttpServletRequest request) {
    User user =(User) request.getAttribute("authenticatedUser");
    int carCount = cartService.getCartItemCount(user.getUserId());
    return ResponseEntity.ok(carCount);
    }
}
