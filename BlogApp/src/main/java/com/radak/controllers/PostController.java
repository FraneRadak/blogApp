package com.radak.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
//import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.radak.database.entities.Comment;
import com.radak.database.entities.Post;
import com.radak.database.entities.User;
import com.radak.exceptions.OutOfAuthorities;
import com.radak.exceptions.SomethingWentWrongException;
import com.radak.exceptions.YourAccountIsBlocked;
import com.radak.service.impl.Util;
import com.radak.services.CategoryService;
import com.radak.services.CommentService;
import com.radak.services.PostService;
import com.radak.services.UserService;
import com.radak.utils.FileUploadUtil;

import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
@RequestMapping("/post")
public class PostController {
	private PostService postService;
	private UserService userService;
	private CategoryService categoryService;
	private CommentService commentService;

	@GetMapping("/create")
	public String createPost(Model model) {
		var post = new Post();
		var categories = categoryService.getAllCategories();
		model.addAttribute("post", post);
		model.addAttribute("categories", categories);
		return "addPost";
	}

	@GetMapping("/share/{postId}")
	public String sharePost(@PathVariable("postId") int postId) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var newPost = new Post();
		var oldPost = postService.getById(postId);
		newPost=postService.copyPostData(newPost, oldPost);
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		var posts = user.getPosts();
		posts.add(newPost);
		user.addPosts(posts);
		userService.add(user);
		return "redirect:/home/";
	}

	@PostMapping("/save")
	public String add(@ModelAttribute("post") Post post, @RequestParam("image") MultipartFile multipartFile,
			BindingResult result) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if (!multipartFile.isEmpty()) {
			Util.savePicture(post, multipartFile);
		} else {
			post.setPhotoPath("none");
		}
		String username = ((UserDetails) principal).getUsername();
		post=postService.setPostDefaultData(post);
		var user=userService.getValidUser(username);
		var posts = user.getPosts();
		posts.add(post);
		user.addPosts(posts);
		userService.add(user);
		return "redirect:/home/";
	}

	@GetMapping("/")
	public String getAll(Model model) {
		return "allPosts";
	}

	@GetMapping("/{id}")
	public String postDetails(@PathVariable("id") int postId, Model model) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		var post = postService.getById(postId);
		var comment=new Comment();
		model.addAttribute("post", post);
		model.addAttribute("currentUser", user);
		model.addAttribute("comment", comment);
		return "postDetails";
	}

	@GetMapping("/like/{id}")
	public String like(@PathVariable("id") int postId) {
		var post = postService.getById(postId);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		post.getLikes().add(user);
		postService.add(post);
		// model.addAttribute("post", post);
		return "redirect:/home/";
	}

	@GetMapping("/dislike/{id}")
	public String dislike(@PathVariable("id") int postId) {
		var post = postService.getById(postId);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		post.getLikes().remove(user);
		postService.add(post);
		return "redirect:/home/";
	}

	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") int postId) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		var post = postService.getOwnedPost(postId, user);
		post.getLikes().clear();
		user.deletePost(post);
		if (!post.getPhotoPath().equals("none") && !postService.isPictureReferenced(post.getPhotoPath(), postId)) {
			Util.deletePicture(post);
		}
		postService.delete(post);
		return "redirect:/home/";
	}

	@GetMapping("/update/{id}")
	public String updatePost(@PathVariable("id") int postId, Model model) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		var post = postService.getOwnedPost(postId, user);
		var categories=categoryService.getAllCategories();
		model.addAttribute("post", post);
		return "updatePost";
	}
	@PostMapping("/saveupdated")
	public String addUpdated(@ModelAttribute("post") Post post, @RequestParam("image") MultipartFile multipartFile,
			BindingResult result) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		var user=userService.getValidUser(username);
		var oldPost=postService.getOwnedPost(post.getId(), user);
		oldPost.setBody(post.getBody());
		oldPost.setTitle(post.getTitle());
		String uploadDir = "photos";
		if(!multipartFile.isEmpty()) {
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String fullFilePath = "/photos/" + fileName;
		oldPost.setPhotoPath(fullFilePath);
		try {
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		else {
			oldPost.setPhotoPath("none");
		}
		user.replacePosts(oldPost);
		userService.add(user);
		return "redirect:/home/";
	}
}
