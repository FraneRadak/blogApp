package com.radak.controllers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.radak.database.entities.Category;
import com.radak.database.entities.Comment;
import com.radak.database.entities.Post;
import com.radak.database.entities.Role;
import com.radak.database.entities.User;
import com.radak.database.repositories.RoleRepository;
import com.radak.database.repositories.UserRepository;
import com.radak.services.CategoryService;
import com.radak.services.PostService;
import com.radak.services.RoleService;
import com.radak.services.UserService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/init")
public class InitController {
	
	private CategoryService categoryService;
	private UserService userService;
	private RoleService roleService;
	private PostService postService;
	private BCryptPasswordEncoder passwordEncoder;
	
	@GetMapping("/createcategories")
	public String createCategories() {
		var category1=new Category(0,"Fashion");
		var category2=new Category(0,"Politics");
		var category3=new Category(0,"Love");
		categoryService.addCategory(category1);
		categoryService.addCategory(category2);
		categoryService.addCategory(category3);
		return "created";
	}
	@GetMapping("/createRoles")
	public String createRoles() {
		var role1=new Role(0,"Admin");
		var role2=new Role(0,"User");
		roleService.add(role1);
		roleService.add(role2);
		return "created";
	}
	@GetMapping("/createUser")
	public String createUser() {
		var admin=new User(0,"admin@java.com",passwordEncoder.encode("admin"),"admin","admin","admin",null,null,0);
		userService.add(admin);
		return "created";
	}
	@GetMapping("/assignroles")
	public String test() {
		User admin=userService.findByUsername("admin");
		var role=roleService.getRoleByName("Admin");
		admin.getRoles().add(role);
		userService.add(admin);
		return "done";
	}
	@GetMapping("/test")
	public Set<Post> test1(){
		User user=userService.findByUsername("user1");
		return user.getPosts();
	}
	@GetMapping("/fix")
	public String fix(){
		var user=userService.findByUsername("admin");
		var post=postService.getById(30);
		var posts=user.getPosts();
		posts.add(post);
		user.setPosts(posts);
		userService.add(user);
		return "done";
	}
}
