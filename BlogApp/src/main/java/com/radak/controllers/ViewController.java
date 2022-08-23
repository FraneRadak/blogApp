package com.radak.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.radak.database.entities.Category;
import com.radak.database.entities.Comment;
import com.radak.database.entities.Post;
import com.radak.services.CategoryService;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/admin/")
public class ViewController {
	private CategoryService categoryService;
	@GetMapping("/createComment/{postId}")
	public String createComment(@PathVariable(value="postId") int postId,Model model) {
		var comment=new Comment();
		model.addAttribute("comment", comment);
		model.addAttribute("postId", postId);
		return "addComment";
	}
}
