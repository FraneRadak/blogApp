package com.radak.controllers;

import java.util.regex.Pattern;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.radak.database.entities.Role;
import com.radak.database.entities.User;
import com.radak.services.RoleService;
import com.radak.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/registration")
@AllArgsConstructor
public class RegisterController {
	private UserService userService;
	private RoleService roleService;
    private BCryptPasswordEncoder passwordEncoder;
	@GetMapping
	public String registrationForm(Model model) {
		User user=new User();
		model.addAttribute("user", user);
		return "register";
	}
	@PostMapping
    public String register(@ModelAttribute("user") User user, String email){
		var isValid=userService.isValidUser(user);
		if(isValid) {
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setBlock(false);
		var role=roleService.getRoleByName("User");
		user.getRoles().add(role);
        userService.add(user);
		}
		return "redirect:home/";
}
}
