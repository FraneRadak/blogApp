package com.radak.services;

import java.util.List;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.radak.database.entities.User;

public interface UserService extends UserDetailsService {
	void add(User user);
	User findByUsername(String username);
	List<User> getAll();
	User findById(int id);
	void delete(User user);
	int getAppSum();
	long numOfUsers();
	float getAverageMark();
	boolean ifExist(String username);
	public User findByUsernameForLogin(String username);
	public User getValidUser(String username);
}
