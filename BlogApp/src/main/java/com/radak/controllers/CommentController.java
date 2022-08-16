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

import com.radak.database.entities.Comment;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.services.CommentService;
import com.radak.services.PostService;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/comment")
@AllArgsConstructor
public class CommentController {
	private CommentService commentService;
	private PostService postService;
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
		if (username.equals(null)) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar=Calendar.getInstance();
		comment.setCreateDate(formatter.format(calendar.getTime()));
		comment.setAuthor(username);
		var post=postService.getById(postId);
		if(post==null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		var comments=post.getComments();
		comments.add(comment);
		post.addComments(comments);
		//commentService.save(comment);
		postService.add(post);
		return "redirect:/home/";
	}
	@GetMapping("/delete/{commentId}/{postId}")
	public String deleteComment(@PathVariable("commentId") int commentId,@PathVariable("postId") int postId) {
		var post=postService.getById(postId);
		if(post==null) {
			throw new SomethingWentWrongException("Post is not found,please try again later");
		}
		var comment=commentService.getById(commentId);
		if(comment==null) {
			throw new SomethingWentWrongException("Comment is not found,please try again later");
		}
		var comments=post.getComments();
		comments.remove(comment);
		post.setComments(comments);
		postService.add(post);
		commentService.deleteComment(comment);
		return "redirect:/home/";
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
