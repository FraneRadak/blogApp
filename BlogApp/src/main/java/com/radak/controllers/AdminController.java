package com.radak.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.radak.database.entities.Category;
import com.radak.database.entities.Post;
import com.radak.database.entities.Role;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.services.CategoryService;
import com.radak.services.PostService;
import com.radak.services.UserService;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
	private UserService userService;
	private CategoryService categoryService;
	private PostService postService;

	@GetMapping("/users")
	public String getAllUsers(Model model) {
		model.addAttribute("users", userService.getAll());
		return "allUsers";
	};
	@GetMapping("/user/block/{id}")
	public String blockUser(@PathVariable("id") int id) {
		var user=userService.findById(id);
		user.setBlock(true);
		userService.add(user);
		return "redirect:/admin/users/";
	}
	@GetMapping("/user/unblock/{id}")
	public String unblockUser(@PathVariable("id") int id) {
		var user=userService.findById(id);
		user.setBlock(false);
		userService.add(user);
		return "redirect:/admin/users/";
	}
	@GetMapping("/category")
	public String getAll(Model model) {
		var list = categoryService.getAllCategories();
		model.addAttribute("categories", list);
		return "allCategories";
	}
	@GetMapping("/category/update/{id}")
	public String showUpdateVategoryForm(@PathVariable(value="id") int id, Model model) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		model.addAttribute("currentUser",user);
		var category=categoryService.getById(id);
		model.addAttribute("category", category);
		return "updateCategory";
	}
	@PostMapping("/category/add")
	public String addUser(@ModelAttribute("category") Category category) {
		categoryService.addCategory(category);
		return "redirect:/admin/category/";
	}

	@GetMapping("/category/delete/{id}")
	public String deleteById(@PathVariable(value = "id") int id) {
		categoryService.deleteById(id);
		return "redirect:/admin/category/";
	}

	@GetMapping("/addNewCategory")
	public String showAddCategoryForm(Model model) {
		var category = new Category();
		model.addAttribute("category", category);
		return "addCategory";
	}
}
