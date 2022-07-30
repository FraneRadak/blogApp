package com.radak.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.radak.database.entities.Post;
import com.radak.database.entities.Role;
import com.radak.database.repositories.RoleRepository;
import com.radak.services.RoleService;

import lombok.AllArgsConstructor;
@Service
public class RoleServiceImpl implements RoleService {
	@Autowired
	private RoleRepository roleRepository;

	@Override
	public void add(Role role) {
		roleRepository.save(role);
	}

	@Override
	public Role getRoleByName(String name) {
		return roleRepository.findByName(name).get(0);
	}

}
