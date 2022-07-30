package com.radak.controllers;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.radak.database.entities.Comment;
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
		var user=userService.findByUsername(username);
		model.addAttribute("posts", user.getPosts());
		return "myPosts";
	}
	@GetMapping("userposts/{userId}")
	private String getUserPosts(@PathVariable("userId") int userId,Model model) {
		var comment=new Comment();
		var user=userService.findById(userId);
		model.addAttribute("comment", comment);
		model.addAttribute("posts", user.getPosts());
		return "userPosts";
	}
}
