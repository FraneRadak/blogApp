package com.radak.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.radak.database.entities.Comment;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.exceptions.YourAccountIsBlocked;
import com.radak.services.PostService;
import com.radak.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {
	private PostService postService;
	private UserService userService;
	@GetMapping("/myposts")
	private String getMyPosts(Model model) {
		var comment=new Comment();
		model.addAttribute("comment", comment);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		if(username.equals(null)) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var user=userService.findByUsername(username);
		if(user.isBlock()) {
			throw new YourAccountIsBlocked("Admin block your account,for more info please contanct admin at admin@gmail.com");	
		}
		model.addAttribute("posts", user.getPosts());
		model.addAttribute("currentUser", user);
		return "myPosts";
	}
	@GetMapping("userposts/{userId}")
	private String getUserPosts(@PathVariable("userId") int userId,Model model) {
		var comment=new Comment();
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		if(username.equals(null)) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var currentUser=userService.findByUsername(username);
		var user=userService.findById(userId);
		if(user.isBlock()) {
			throw new YourAccountIsBlocked("Admin block your account,for more info please contanct admin at admin@gmail.com");	
		}
		model.addAttribute("comment", comment);
		model.addAttribute("currentUser", currentUser);
		model.addAttribute("posts", user.getPosts());
		return "userPosts";
	}
}
