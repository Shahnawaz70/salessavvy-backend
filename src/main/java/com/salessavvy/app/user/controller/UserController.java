package com.salessavvy.app.user.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.common.entity.UserDao;
import com.salessavvy.app.user.service.UserServiceContract;


@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/api/users")
public class UserController {
	private final UserServiceContract userService;
	public UserController(UserServiceContract userService) {
		this.userService = userService;
	}
	
	@PostMapping("/register")
	public ResponseEntity<?> registerUser(@RequestBody User user) {
	    try {
	        User registeredUser = userService.registerUser(user);

	        return ResponseEntity.ok(
	            Map.of(
	                "message", "User Registered Successfully",
	                "user", new UserDao(
	                    registeredUser.getUserId(),
	                    registeredUser.getUsername(),
	                    registeredUser.getPassword(),
	                    registeredUser.getRole().toString()
	                )
	            )
	        );
	    } catch (RuntimeException e) {
	        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
	    }
	}

}
