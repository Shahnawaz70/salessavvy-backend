package com.salessavvy.app.admin.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.salessavvy.app.admin.service.AdminUserServiceContract;
import com.salessavvy.app.common.entity.Role;
import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.user.repository.JWTTokenRepository;
import com.salessavvy.app.user.repository.UserRepository;
@Service
public class AdminUserService implements AdminUserServiceContract {
	private UserRepository userRepository;
	private JWTTokenRepository jwtTokenRepository;

	public AdminUserService(UserRepository userRepository, JWTTokenRepository jwtTokenRepository) {
		super();
		this.userRepository = userRepository;
		this.jwtTokenRepository = jwtTokenRepository;
	}

	@Override
	public User modifyUser(Integer userId, String username, String email, String role) {
		Optional<User> userOptional = userRepository.findById(userId);
		if (userOptional.isEmpty()) {
			throw new IllegalArgumentException("User not found");
		}
		User existingUser = userOptional.get();

		if (username != null && !username.isEmpty()) {
			existingUser.setUsername(username);
		}
		if (email != null && !email.isEmpty()) {
			existingUser.setEmail(email);
		}
		if (role != null && !role.isEmpty()) {
			try {
				existingUser.setRole(Role.valueOf(role));
			} catch (IllegalArgumentException e) {
				throw new IllegalArgumentException("Invalid role: " + role);
			}
		}

		jwtTokenRepository.deleteByUserId(userId);
		return userRepository.save(existingUser);
	}

	@Override
	public User getUserById(Integer userId) {
		return userRepository.findById(userId)
				.orElseThrow(() -> new IllegalArgumentException("User with " + userId + " not found"));
	}

}
