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
		if (username.equals(null)) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var user=userService.findByUsername(username);
		if(user.isBlock()) {
			throw new YourAccountIsBlocked("Admin block your account,for more info please contanct admin at admin@gmail.com");
		}
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar=Calendar.getInstance();
		comment.setCreateDate(formatter.format(calendar.getTime()));
		comment.setAuthor(username);
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
		var comment=commentService.getById(commentId);
		var comments=post.getComments();
		String username=((UserDetails)principal).getUsername();
		if (username.equals(null)) {
			throw new SomethingWentWrongException("Logged user not found, please re-login and try again");
		}
		var user=userService.findByUsername(username);
		if(user.isBlock()) {
			throw new YourAccountIsBlocked("Admin block your account,for more info please contanct admin at admin@gmail.com");
		}
		if(!comment.isOwnedBy(user)) {
			throw new OutOfAuthorities("You dont have enough authorities to do this");
		}
		comments.remove(comment);
		post.setComments(comments);
		postService.add(post);
		commentService.deleteComment(comment);
		return "redirect:/home/";
	}
}
