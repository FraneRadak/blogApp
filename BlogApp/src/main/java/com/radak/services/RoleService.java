package com.radak.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.radak.database.entities.Post;
import com.radak.database.entities.Role;

public interface RoleService {
	void add(Role role);
	Role getRoleByName(String name);	
}
