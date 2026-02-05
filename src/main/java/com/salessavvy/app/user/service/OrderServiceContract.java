package com.salessavvy.app.user.service;

import java.util.Map;

import com.salessavvy.app.common.entity.User;

public interface OrderServiceContract {
	public Map<String, Object> getOrdersForUser(User user);
}
