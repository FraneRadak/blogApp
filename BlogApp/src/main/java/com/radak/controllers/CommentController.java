package com.radak.controllers;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.radak.database.entities.Comment;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.exceptions.YourAccountIsBlocked;
import com.radak.services.CommentService;
import com.radak.services.PostService;
import com.radak.services.UserService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
	private CommentService commentService;
	private PostService postService;
	private UserService userService;
	@GetMapping("/create/{postId}")
	public String createComment(@PathVariable(value="postId") int postId,Model model) {
		var comment=new Comment();
		model.addAttribute("comment", comment);
		model.addAttribute("postId", postId);
		return "addComment";
	}
	@PostMapping("/save/{postId}")
	public String insertComment(@ModelAttribute("comment")Comment comment,@PathVariable(value="postId") int postId) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.getValidUser(username);
		comment=commentService.setDefaultCommentData(user, comment);
		var post=postService.getById(postId);
		var comments=post.getComments();
		comments.add(comment);
		post.addComments(comments);
		postService.add(post);
		return "redirect:/home/";
	}
	@GetMapping("/delete/{commentId}/{postId}")
	public String deleteComment(@PathVariable("commentId") int commentId,@PathVariable("postId") int postId) {
		var post=postService.getById(postId);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.getValidUser(username);
		var comment=commentService.getValidComment(commentId, user);
		var comments=post.getComments();
		comments.remove(comment);
		post.setComments(comments);
		postService.add(post);
		commentService.deleteComment(comment);
		return "redirect:/home/";
	}
	@GetMapping("/update/{id}")
	public String updateCommentForm(@PathVariable("id") int commentId,Model model) {
		var comment=commentService.getById(commentId);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.getValidUser(username);
		if(!comment.isCreatedBy(user)) {
			throw new OutOfAuthorities("You dont have enough authorities to do this");
		}
		model.addAttribute("comment",comment);
		model.addAttribute("currentUser", user);
		return "updateComment";
	}
	@PostMapping("/saveupdated")
	public String saveUpdated(@ModelAttribute("comment") Comment comment) {
		var uComment=commentService.getById(comment.getId());
		uComment.setBody(comment.getBody());
		commentService.save(uComment);
		return "redirect:/home/";
	}
}
