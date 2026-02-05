package com.salessavvy.app.common.entity;

public class UserDao {
	int userid;
	String username;
	String password;
	String role;
	public UserDao() {
		// TODO Auto-generated constructor stub
	}
	public UserDao(int userid, String username, String password, String role) {
		super();
		this.userid = userid;
		this.username = username;
		this.password = password;
		this.role = role;
	}
	
	public int getUserid() {
        return userid;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }
}
