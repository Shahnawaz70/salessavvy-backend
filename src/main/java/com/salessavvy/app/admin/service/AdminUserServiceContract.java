package com.salessavvy.app.admin.service;

import com.salessavvy.app.common.entity.User;

public interface AdminUserServiceContract {
	public User modifyUser(Integer userId, String username, String email, String role);
	public User getUserById(Integer userId);

}
