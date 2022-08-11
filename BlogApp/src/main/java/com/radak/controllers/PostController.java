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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.radak.database.entities.Post;
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
		var post=new Post();
		var categories=categoryService.getAllCategories();
		model.addAttribute("post", post);
		model.addAttribute("categories",categories);
		return "addPost";
	}
	@GetMapping("/share/{postId}")
	public String sharePost(@PathVariable("postId") int postId) {
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		var newPost=new Post();
		var oldPost=postService.getById(postId);
		newPost.setTitle(oldPost.getTitle());
		newPost.setBody(oldPost.getBody());
		newPost.setPhotoPath(oldPost.getPhotoPath());
		newPost.setCategory(oldPost.getCategory());
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar=Calendar.getInstance();
		newPost.setCreateDate(formatter.format(calendar.getTime()));
		String username=((UserDetails)principal).getUsername();
		var user=userService.findByUsername(username);
		var posts=user.getPosts();
		posts.add(newPost);
		user.addPosts(posts);
		userService.add(user);
		return "redirect:/home/";
	}
	@PostMapping("/save")
	public String add(@ModelAttribute("post") Post post,@RequestParam("image") MultipartFile multipartFile,BindingResult result) {
		String uploadDir = "photos";
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		if(!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			String fullFilePath="/photos/"+fileName;
			post.setPhotoPath(fullFilePath);
			try {
				FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			post.setPhotoPath("none");
		}
		String username=((UserDetails)principal).getUsername();
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar=Calendar.getInstance();
		System.out.println(calendar.getTime());
		post.setCreateDate(formatter.format(calendar.getTime()));
		var user=userService.findByUsername(username);
		var posts=user.getPosts();
		posts.add(post);
		user.addPosts(posts);
		userService.add(user);
		System.out.println("saved");
		return "redirect:/home/";
	}
	@GetMapping("/")
	public String getAll(Model model) {
		return "allPosts";
	}
	@GetMapping("/{id}")
	public String postDetails(@PathVariable("id") int postId,Model model) {
		var post=postService.getById(postId);
		model.addAttribute("post", post);
		return "postDetails";
	}
	@GetMapping("/like/{id}")
	public String like(@PathVariable("id") int postId) {
		var post=postService.getById(postId);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.findByUsername(username);
		post.getLikes().add(user);
		postService.add(post);
		//model.addAttribute("post", post);
		return "redirect:/home/";
	}
	@GetMapping("/dislike/{id}")
	public String dislike(@PathVariable("id") int postId) {
		var post=postService.getById(postId);
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.findByUsername(username);
		post.getLikes().remove(user);
		postService.add(post);
		//model.addAttribute("post", post);
		return "redirect:/home/";
	}
	@GetMapping("/delete/{id}")
	public String delete(@PathVariable("id") int postId) {
		var post=postService.getById(postId);
		post.getLikes().clear();
		var user=post.getUser();
		user.deletePost(post);
		System.out.println(post.getPhotoPath());
		if(!post.getPhotoPath().equals("none")) {
		if (!postService.isPictureReferenced(post.getPhotoPath(),postId)) {
		String path=post.getPhotoPath();
		Path fileToDeletePath = Paths.get(path.substring(1));
		try {
			Files.delete(fileToDeletePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
		}
		}
		postService.delete(post);
		return "redirect:/home/";
	}
	@GetMapping("/update/{id}")
	public String updatePost(@PathVariable("id") int postId,Model model) {
		var post=postService.getById(postId);
		model.addAttribute("post",post);
		return "updatePost";
	}
	@PostMapping("/saveupdated")
	public String addUpdated(@ModelAttribute("post") Post post,@RequestParam("image") MultipartFile multipartFile,BindingResult result) {
		var oldPost=postService.getById(post.getId());
		oldPost.setBody(post.getBody());
		oldPost.setTitle(post.getTitle());
		String uploadDir = "photos";
		String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
		String fullFilePath="/photos/"+fileName;
		oldPost.setPhotoPath(fullFilePath);
		DateFormat formatter = new SimpleDateFormat("MMM d, yyyy HH:mm a");
		Calendar calendar=Calendar.getInstance();
		post.setCreateDate(formatter.format(calendar.getTime()));
		var principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username=((UserDetails)principal).getUsername();
		var user=userService.findByUsername(username);
		user.replacePosts(oldPost);
		userService.add(user);
		try {
			FileUploadUtil.saveFile(uploadDir, fileName, multipartFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "redirect:/home/";
}
}
