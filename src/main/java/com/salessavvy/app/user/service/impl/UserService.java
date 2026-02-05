package com.salessavvy.app.user.service.impl;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.salessavvy.app.common.entity.User;
import com.salessavvy.app.user.repository.UserRepository;
import com.salessavvy.app.user.service.UserServiceContract;

@Service
public class UserService implements UserServiceContract {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	
	public UserService(UserRepository userRepository) {
		this.userRepository=userRepository;
		this.passwordEncoder= new BCryptPasswordEncoder();
	}
	
	
	@Override
	public User registerUser(User user) {
		//check if username or email already exists
		if(userRepository.findByUsername(user.getUsername()).isPresent()) {
			throw new RuntimeException("User is already taken");
		}
		
		if(userRepository.findByEmail(user.getEmail()).isPresent()) {
			throw new RuntimeException("Email is already registered");
		}
		
		//Encode password before saving
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		// save the user
		return userRepository.save(user);
	}

}
