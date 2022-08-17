package com.radak.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.radak.database.entities.Category;
import com.radak.database.entities.Role;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.services.CategoryService;
import com.radak.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
	private UserService userService;
	private CategoryService categoryService;
	@GetMapping("/users")
	public String getAllUsers(Model model) {
		model.addAttribute("users", userService.getAll());
		return "allUsers";
	};
	@GetMapping("/user/delete/{id}")
	public String deleteUser(@PathVariable("id") int id) {
		var user=userService.findById(id);
		if(user==null) {
			throw new SomethingWentWrongException("User not found, please re-login and try again");
		}
		var roles=user.getRoles();
		for (Role role:roles) {
			roles.remove(role);
		}
		user.setRoles(roles);
		user.clearPosts();
		userService.delete(user);
		return "redirect:/admin/users/";
	}
	@GetMapping("/category")
	public String getAll(Model model) {
		var list=categoryService.getAllCategories();
		model.addAttribute("categories", list);
		return "allCategories";
	}
	@PostMapping("/category/add")
    public String addUser(@ModelAttribute("category") Category category) {
		categoryService.addCategory(category);
		return "redirect:/admin/category/";
    }
	@GetMapping("/category/delete/{id}")
	public String deleteById(@PathVariable(value="id") int id) {
		categoryService.deleteById(id);
		return "redirect:/admin/category/";
	}
	@GetMapping("/addNewCategory")
	public String showAddCategoryForm(Model model) {
		var category=new Category();
		model.addAttribute("category", category);
		return "addCategory";
	}
	@ExceptionHandler(value = { OutOfAuthorities.class })
	public String handleOutOfAuthorities(OutOfAuthorities exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "OutOfAuthorities";
	}
	@ExceptionHandler(value = { SomethingWentWrongException.class })
	public String handleSomethingWentWrongException(SomethingWentWrongException exception, Model model) {

		String errorMsg = exception.getLocalizedMessage();
		model.addAttribute("errorMsg", errorMsg);
		return "SomethingWentWrong";
	}
}
