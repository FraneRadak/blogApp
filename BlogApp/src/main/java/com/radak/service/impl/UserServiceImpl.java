package com.radak.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.radak.database.entities.Role;
import com.radak.database.entities.User;
import com.radak.database.repositories.RoleRepository;
import com.radak.database.repositories.UserRepository;
import com.radak.services.RoleService;
import com.radak.services.UserService;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleService roleService;

	@Override
	public void add(User user) {
		userRepository.save(user);
	}
	@Override
	public User findByUsername(String username) {
		return userRepository.findByUsername(username).get(0);
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = this.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException("Invalid username or password");
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),mapRolesToAuthorities(user.getRoles()));
	}
	private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Collection<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }
	@SuppressWarnings("unchecked")
	@Override
	public List<User> getAll() {
		return userRepository.findAll();
	}
	@Override
	public User findById(int id) {
		Optional<User> optional=userRepository.findById(id);
		User u=null;
		if(optional.isPresent()) {
			u=optional.get();
			return u;
		}
		return null;
	}
	@Override
	public void delete(User user) {
		userRepository.delete(user);
	}
	@Override
	public int getAppSum() {
		// TODO Auto-generated method stub
		return userRepository.findAll().stream().mapToInt(user->user.getReview()).sum();
	}
	@Override
	public long numOfUsers() {
		return userRepository.count();
	}
	@Override
	public float getAverageMark() {
		int markSum=userRepository.findAll().stream().filter(user->user.getReview()!=0).mapToInt(user->user.getReview()).sum();
		int numOfMarked=(int) userRepository.findAll().stream().filter(user->user.getReview()!=0).count();
		if (numOfMarked==0) {
			return 0;
		}
		return markSum/numOfMarked;
	}
}
